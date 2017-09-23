package fr.gstraymond.ocr.ui.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.os.Build
import android.os.SystemClock
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import com.google.android.gms.common.images.Size
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Frame
import com.magic.card.search.commons.log.Log
import java.nio.ByteBuffer
import java.util.*

class CameraSource() {

    private val log = Log(javaClass)
    private var context: Context? = null

    private val cameraLock = Any()

    private var camera: Camera? = null

    var cameraFacing = CAMERA_FACING_BACK

    private var rotation: Int = 0

    var previewSize: Size? = null

    private var requestedFps = 30.0f
    private var requestedPreviewWidth = 1024
    private var requestedPreviewHeight = 768


    private var focusMode: String? = null
    private var flashMode: String? = null

    private var dummySurfaceView: SurfaceView? = null
    private var dummySurfaceTexture: SurfaceTexture? = null

    private var processingThread: Thread? = null
    private var frameProcessor: FrameProcessingRunnable? = null

    private val bytesToByteBuffer = HashMap<ByteArray, ByteBuffer>()

    class Builder(context: Context,
                  private val detector: Detector<*>) {
        private val cameraSource = CameraSource().apply { this.context = context }

        fun setRequestedFps(fps: Float): Builder {
            if (fps <= 0) {
                throw IllegalArgumentException("Invalid fps: " + fps)
            }
            cameraSource.requestedFps = fps
            return this
        }

        fun setFocusMode(mode: String): Builder {
            cameraSource.focusMode = mode
            return this
        }

        fun setFlashMode(mode: String): Builder {
            cameraSource.flashMode = mode
            return this
        }

        fun setRequestedPreviewSize(width: Int, height: Int): Builder {
            val MAX = 1000000
            if (width <= 0 || width > MAX || height <= 0 || height > MAX) {
                throw IllegalArgumentException("Invalid preview size: " + width + "x" + height)
            }
            cameraSource.requestedPreviewWidth = width
            cameraSource.requestedPreviewHeight = height
            return this
        }

        fun setFacing(facing: Int): Builder {
            if (facing != CAMERA_FACING_BACK && facing != CAMERA_FACING_FRONT) {
                throw IllegalArgumentException("Invalid camera: " + facing)
            }
            cameraSource.cameraFacing = facing
            return this
        }

        fun build(): CameraSource {
            cameraSource.frameProcessor = cameraSource.FrameProcessingRunnable(detector)
            return cameraSource
        }
    }

    interface ShutterCallback {
        fun onShutter()
    }

    interface PictureCallback {
        fun onPictureTaken(data: ByteArray)
    }

    interface AutoFocusCallback {
        fun onAutoFocus(success: Boolean)
    }

    interface AutoFocusMoveCallback {
        fun onAutoFocusMoving(start: Boolean)
    }

    fun release() {
        synchronized(cameraLock) {
            stop()
            frameProcessor!!.release()
        }
    }

    fun start(): CameraSource {
        synchronized(cameraLock) {
            if (camera != null) {
                return this
            }

            camera = createCamera().apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    dummySurfaceTexture = SurfaceTexture(DUMMY_TEXTURE_NAME)
                    setPreviewTexture(dummySurfaceTexture)
                } else {
                    dummySurfaceView = SurfaceView(context)
                    setPreviewDisplay(dummySurfaceView!!.holder)
                }
                startPreview()
            }

            processingThread = Thread(frameProcessor)
            frameProcessor!!.setActive(true)
            processingThread!!.start()
        }
        return this
    }

    fun start(surfaceHolder: SurfaceHolder): CameraSource {
        synchronized(cameraLock) {
            if (camera != null) {
                return this
            }

            camera = createCamera()
            camera!!.setPreviewDisplay(surfaceHolder)
            camera!!.startPreview()

            processingThread = Thread(frameProcessor)
            frameProcessor!!.setActive(true)
            processingThread!!.start()
        }
        return this
    }

    fun stop() {
        synchronized(cameraLock) {
            frameProcessor!!.setActive(false)
            if (processingThread != null) {
                try {
                    processingThread!!.join()
                } catch (e: InterruptedException) {
                    log.d("Frame processing thread interrupted on release.")
                }

                processingThread = null
            }

            // clear the buffer to prevent oom exceptions
            bytesToByteBuffer.clear()

            camera?.apply {
                stopPreview()
                setPreviewCallbackWithBuffer(null)
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        setPreviewTexture(null)
                    } else {
                        setPreviewDisplay(null)
                    }
                } catch (e: Exception) {
                    log.e("Failed to clear camera preview: " + e, e)
                }

                release()
                camera = null
            }
        }
    }

    private fun createCamera(): Camera {
        val requestedCameraId = getIdForRequestedCamera(cameraFacing)
        if (requestedCameraId == -1) {
            throw RuntimeException("Could not find requested camera.")
        }
        val camera = Camera.open(requestedCameraId)

        val sizePair = selectSizePair(camera, requestedPreviewWidth, requestedPreviewHeight) ?: throw RuntimeException("Could not find suitable preview size.")
        val pictureSize = sizePair.pictureSize()
        previewSize = sizePair.previewSize()

        val previewFpsRange = selectPreviewFpsRange(camera, requestedFps) ?: throw RuntimeException("Could not find suitable preview frames per second range.")

        val parameters = camera.parameters

        if (pictureSize != null) {
            parameters.setPictureSize(pictureSize.width, pictureSize.height)
        }

        val previewSize1 = previewSize
        parameters.setPreviewSize(previewSize1!!.width, previewSize1.height)
        parameters.setPreviewFpsRange(
                previewFpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
                previewFpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX])
        parameters.previewFormat = ImageFormat.NV21

        setRotation(camera, parameters, requestedCameraId)

        if (focusMode != null) {
            if (parameters.supportedFocusModes.contains(
                    focusMode)) {
                parameters.focusMode = focusMode
            } else {
                log.i("Camera focus mode: $focusMode is not supported on this device.")
            }
        }

        // setting focusMode to the one set in the params
        focusMode = parameters.focusMode

        if (flashMode != null) {
            if (parameters.supportedFlashModes.contains(
                    flashMode)) {
                parameters.flashMode = flashMode
            } else {
                log.i("Camera flash mode: $flashMode is not supported on this device.")
            }
        }

        // setting flashMode to the one set in the params
        flashMode = parameters.flashMode

        camera.parameters = parameters

        camera.setPreviewCallbackWithBuffer(CameraPreviewCallback())
        camera.addCallbackBuffer(createPreviewBuffer(previewSize1))
        camera.addCallbackBuffer(createPreviewBuffer(previewSize1))
        camera.addCallbackBuffer(createPreviewBuffer(previewSize1))
        camera.addCallbackBuffer(createPreviewBuffer(previewSize1))

        return camera
    }

    private class SizePair(previewSize: android.hardware.Camera.Size,
                           pictureSize: android.hardware.Camera.Size?) {
        private val preview = Size(previewSize.width, previewSize.height)
        private var picture: Size? = null

        init {
            if (pictureSize != null) {
                picture = Size(pictureSize.width, pictureSize.height)
            }
        }

        fun previewSize(): Size = preview

        fun pictureSize(): Size? = picture
    }

    private fun selectPreviewFpsRange(camera: Camera, desiredPreviewFps: Float): IntArray? {
        val desiredPreviewFpsScaled = (desiredPreviewFps * 1000.0f).toInt()

        var selectedFpsRange: IntArray? = null
        var minDiff = Integer.MAX_VALUE
        val previewFpsRangeList = camera.parameters.supportedPreviewFpsRange
        for (range in previewFpsRangeList) {
            val deltaMin = desiredPreviewFpsScaled - range[Camera.Parameters.PREVIEW_FPS_MIN_INDEX]
            val deltaMax = desiredPreviewFpsScaled - range[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]
            val diff = Math.abs(deltaMin) + Math.abs(deltaMax)
            if (diff < minDiff) {
                selectedFpsRange = range
                minDiff = diff
            }
        }
        return selectedFpsRange
    }

    private fun setRotation(camera: Camera, parameters: Camera.Parameters, cameraId: Int) {
        val windowManager = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        var degrees = 0
        val rotation = windowManager.defaultDisplay.rotation
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
            else -> log.w("Bad rotation value: $rotation")
        }

        val cameraInfo = CameraInfo()
        Camera.getCameraInfo(cameraId, cameraInfo)

        val angle: Int
        val displayAngle: Int
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            angle = (cameraInfo.orientation + degrees) % 360
            displayAngle = 360 - angle // compensate for it being mirrored
        } else {  // back-facing
            angle = (cameraInfo.orientation - degrees + 360) % 360
            displayAngle = angle
        }

        // This corresponds to the rotation constants in {@link Frame}.
        this.rotation = angle / 90

        camera.setDisplayOrientation(displayAngle)
        parameters.setRotation(angle)
    }

    private fun createPreviewBuffer(previewSize: Size): ByteArray {
        val bitsPerPixel = ImageFormat.getBitsPerPixel(ImageFormat.NV21)
        val sizeInBits = (previewSize.height * previewSize.width * bitsPerPixel).toLong()
        val bufferSize = Math.ceil(sizeInBits / 8.0).toInt() + 1

        val byteArray = ByteArray(bufferSize)
        val buffer = ByteBuffer.wrap(byteArray)
        if (!buffer.hasArray() || buffer.array() != byteArray) {
            throw IllegalStateException("Failed to create valid buffer for camera source.")
        }

        bytesToByteBuffer.put(byteArray, buffer)
        return byteArray
    }

    private inner class CameraPreviewCallback : Camera.PreviewCallback {
        override fun onPreviewFrame(data: ByteArray, camera: Camera) {
            frameProcessor!!.setNextFrame(data, camera)
        }
    }

    private inner class FrameProcessingRunnable(private var detector: Detector<*>?) : Runnable {
        private val startTimeMillis = SystemClock.elapsedRealtime()

        private val lock = Object()
        private var active = true

        private var pendingTimeMillis: Long = 0
        private var pendingFrameId = 0
        private var pendingFrameData: ByteBuffer? = null

        internal fun release() {
            // FIXME chelou assert(processingThread!!.state == State.TERMINATED)
            detector!!.release()
            detector = null
        }

        internal fun setActive(active: Boolean) {
            synchronized(lock) {
                this.active = active
                lock.notifyAll()
            }
        }

        internal fun setNextFrame(data: ByteArray, camera: Camera) {
            synchronized(lock) {
                if (pendingFrameData != null) {
                    camera.addCallbackBuffer(pendingFrameData!!.array())
                    pendingFrameData = null
                }

                if (!bytesToByteBuffer.containsKey(data)) {
                    log.d("Skipping frame.  Could not find ByteBuffer associated with the image data from the camera.")
                    return
                }

                pendingTimeMillis = SystemClock.elapsedRealtime() - startTimeMillis
                pendingFrameId++
                pendingFrameData = bytesToByteBuffer[data]

                lock.notifyAll()
            }
        }

        override fun run() {
            var outputFrame: Frame? = null
            var data: ByteBuffer? = null

            while (true) {
                synchronized(lock) {
                    while (active && pendingFrameData == null) {
                        try {
                            lock.wait()
                        } catch (e: InterruptedException) {
                            log.d("Frame processing loop terminated.", e)
                            return
                        }

                    }

                    if (!active) {
                        return
                    }

                    outputFrame = Frame.Builder()
                            .setImageData(pendingFrameData!!, previewSize!!.width,
                                    previewSize!!.height, ImageFormat.NV21)
                            .setId(pendingFrameId)
                            .setTimestampMillis(pendingTimeMillis)
                            .setRotation(rotation)
                            .build()

                    data = pendingFrameData
                    pendingFrameData = null
                }

                try {
                    detector!!.receiveFrame(outputFrame)
                } catch (t: Throwable) {
                    log.e("Exception thrown from receiver.", t)
                } finally {
                    camera!!.addCallbackBuffer(data?.array())
                }
            }
        }
    }

    companion object {
        val CAMERA_FACING_BACK = CameraInfo.CAMERA_FACING_BACK
        val CAMERA_FACING_FRONT = CameraInfo.CAMERA_FACING_FRONT

        private val DUMMY_TEXTURE_NAME = 100
        private val ASPECT_RATIO_TOLERANCE = 0.01f

        private fun getIdForRequestedCamera(facing: Int): Int {
            val cameraInfo = CameraInfo()
            for (i in 0..Camera.getNumberOfCameras() - 1) {
                Camera.getCameraInfo(i, cameraInfo)
                if (cameraInfo.facing == facing) {
                    return i
                }
            }
            return -1
        }

        private fun selectSizePair(camera: Camera, desiredWidth: Int, desiredHeight: Int): SizePair? {
            val validPreviewSizes = generateValidPreviewSizeList(camera)

            var selectedPair: SizePair? = null
            var minDiff = Integer.MAX_VALUE
            for (sizePair in validPreviewSizes) {
                val size = sizePair.previewSize()
                val diff = Math.abs(size.width - desiredWidth) + Math.abs(size.height - desiredHeight)
                if (diff < minDiff) {
                    selectedPair = sizePair
                    minDiff = diff
                }
            }

            return selectedPair
        }

        private fun generateValidPreviewSizeList(camera: Camera): List<SizePair> {
            val parameters = camera.parameters
            val supportedPreviewSizes = parameters.supportedPreviewSizes
            val supportedPictureSizes = parameters.supportedPictureSizes
            val validPreviewSizes = ArrayList<SizePair>()
            for (previewSize in supportedPreviewSizes) {
                val previewAspectRatio = previewSize.width.toFloat() / previewSize.height.toFloat()

                for (pictureSize in supportedPictureSizes) {
                    val pictureAspectRatio = pictureSize.width.toFloat() / pictureSize.height.toFloat()
                    if (Math.abs(previewAspectRatio - pictureAspectRatio) < ASPECT_RATIO_TOLERANCE) {
                        validPreviewSizes.add(SizePair(previewSize, pictureSize))
                        break
                    }
                }
            }

            if (validPreviewSizes.size == 0) {
                Log(this::class.java).w("No preview sizes have a corresponding same-aspect-ratio picture size")
                supportedPreviewSizes.forEach { previewSize ->
                    validPreviewSizes.add(SizePair(previewSize, null))
                }
            }
            return validPreviewSizes
        }
    }
}
