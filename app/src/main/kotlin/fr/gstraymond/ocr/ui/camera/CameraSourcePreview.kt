package fr.gstraymond.ocr.ui.camera

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import com.magic.card.search.commons.log.Log
import java.io.IOException

class CameraSourcePreview(context: Context,
                          attrs: AttributeSet) : ViewGroup(context, attrs) {

    private val log = Log(javaClass)

    private val surfaceView = SurfaceView(context).apply {
        holder.addCallback(SurfaceCallback())
    }

    private var startRequested = false
    private var surfaceAvailable = false
    private var cameraSource: CameraSource? = null

    private var overlay: GraphicOverlay<*>? = null

    init {
        addView(surfaceView)
    }

    fun start(cameraSource: CameraSource?) {
        if (cameraSource == null) {
            stop()
        }

        this.cameraSource = cameraSource

        if (cameraSource != null) {
            startRequested = true
            startIfReady()
        }
    }

    fun start(cameraSource: CameraSource, overlay: GraphicOverlay<*>) {
        this.overlay = overlay
        start(cameraSource)
    }

    fun stop() {
        cameraSource?.stop()
    }

    fun release() {
        cameraSource?.apply {
            release()
            cameraSource = null
        }
    }

    private fun startIfReady() {
        if (startRequested && surfaceAvailable) {
            cameraSource!!.start(surfaceView.holder)
            overlay?.apply {
                val size = cameraSource!!.previewSize
                val min = Math.min(size!!.width, size.height)
                val max = Math.max(size.width, size.height)
                if (isPortraitMode) {
                    setCameraInfo(min, max, cameraSource!!.cameraFacing)
                } else {
                    setCameraInfo(max, min, cameraSource!!.cameraFacing)
                }
                clear()
            }
            startRequested = false
        }
    }

    private inner class SurfaceCallback : SurfaceHolder.Callback {
        override fun surfaceCreated(surface: SurfaceHolder) {
            surfaceAvailable = true
            try {
                startIfReady()
            } catch (se: SecurityException) {
                log.e("Do not have permission to start the camera", se)
            } catch (e: IOException) {
                log.e("Could not start camera source.", e)
            }

        }

        override fun surfaceDestroyed(surface: SurfaceHolder) {
            surfaceAvailable = false
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var previewWidth = 320
        var previewHeight = 240
        cameraSource?.apply {
            val size = previewSize
            if (size != null) {
                previewWidth = size.width
                previewHeight = size.height
            }
        }

        if (isPortraitMode) {
            val tmp = previewWidth
            previewWidth = previewHeight
            previewHeight = tmp
        }

        val viewWidth = right - left
        val viewHeight = bottom - top

        val childWidth: Int
        val childHeight: Int
        var childXOffset = 0
        var childYOffset = 0
        val widthRatio = viewWidth.toFloat() / previewWidth.toFloat()
        val heightRatio = viewHeight.toFloat() / previewHeight.toFloat()

        if (widthRatio > heightRatio) {
            childWidth = viewWidth
            childHeight = (previewHeight.toFloat() * widthRatio).toInt()
            childYOffset = (childHeight - viewHeight) / 2
        } else {
            childWidth = (previewWidth.toFloat() * heightRatio).toInt()
            childHeight = viewHeight
            childXOffset = (childWidth - viewWidth) / 2
        }

        for (i in 0..childCount - 1) {
            getChildAt(i).layout(
                    -1 * childXOffset,
                    -1 * childYOffset,
                    childWidth - childXOffset,
                    childHeight - childYOffset)
        }

        try {
            startIfReady()
        } catch (se: SecurityException) {
            log.e("Do not have permission to start the camera", se)
        } catch (e: IOException) {
            log.e("Could not start camera source.", e)
        }

    }

    private val isPortraitMode =
            context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
}
