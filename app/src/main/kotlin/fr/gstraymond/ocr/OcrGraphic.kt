package fr.gstraymond.ocr

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.google.android.gms.vision.text.TextBlock
import fr.gstraymond.ocr.ui.camera.GraphicOverlay

class OcrGraphic(overlay: GraphicOverlay<*>,
                 private val textBlock: TextBlock,
                 private val textColor: Int = Color.WHITE) : GraphicOverlay.Graphic(overlay) {

    private val rectPaint = Paint().apply {
        color = textColor
        style = Paint.Style.FILL
        alpha = 128
        postInvalidate()
    }

    override fun contains(x: Float, y: Float) = RectF(textBlock.boundingBox).run {
        left = translateX(left)
        top = translateY(top)
        right = translateX(right)
        bottom = translateY(bottom)
        left < x && right > x && top < y && bottom > y
    }

    override fun draw(canvas: Canvas) {
        RectF(textBlock.boundingBox).apply {
            left = translateX(left)
            top = translateY(top)
            right = translateX(right)
            bottom = translateY(bottom)
            canvas.drawRect(this, rectPaint)
        }
    }
}
