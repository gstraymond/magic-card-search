package fr.gstraymond.ocr.ui.camera

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

import com.google.android.gms.vision.CameraSource

class GraphicOverlay<T : GraphicOverlay.Graphic>(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val lock = Any()
    private var previewWidth: Int = 0
    private var widthScaleFactor = 1.0f
    private var previewHeight: Int = 0
    private var heightScaleFactor = 1.0f
    private var facing = CameraSource.CAMERA_FACING_BACK
    private val graphics = HashSet<T>()

    abstract class Graphic(private val overlay: GraphicOverlay<*>) {

        abstract fun draw(canvas: Canvas)

        abstract fun contains(x: Float, y: Float): Boolean

        private fun scaleX(horizontal: Float) = horizontal * overlay.widthScaleFactor

        private fun scaleY(vertical: Float) = vertical * overlay.heightScaleFactor

        fun translateX(x: Float) = when (overlay.facing) {
            CameraSource.CAMERA_FACING_FRONT -> overlay.width - scaleX(x)
            else -> scaleX(x)
        }

        fun translateY(y: Float) = scaleY(y)

        fun postInvalidate() {
            overlay.postInvalidate()
        }
    }

    fun clear() {
        synchronized(lock) {
            graphics.clear()
        }
        postInvalidate()
    }

    fun add(graphic: T) {
        synchronized(lock) {
            graphics.add(graphic)
        }
        postInvalidate()
    }

    fun setCameraInfo(previewWidth: Int, previewHeight: Int, facing: Int) {
        synchronized(lock) {
            this.previewWidth = previewWidth
            this.previewHeight = previewHeight
            this.facing = facing
        }
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        synchronized(lock) {
            if (previewWidth != 0 && previewHeight != 0) {
                widthScaleFactor = canvas.width.toFloat() / previewWidth.toFloat()
                heightScaleFactor = canvas.height.toFloat() / previewHeight.toFloat()
            }

            graphics.forEach { it.draw(canvas) }
        }
    }
}
