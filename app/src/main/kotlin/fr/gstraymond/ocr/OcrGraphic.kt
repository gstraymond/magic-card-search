package fr.gstraymond.ocr

import android.graphics.*
import android.graphics.drawable.Drawable
import com.google.mlkit.vision.text.Text

class OcrGraphic(private val textBlock: Text.TextBlock,
                 private val textColor: Int = Color.WHITE) : Drawable() {

    private val rectPaint = Paint().apply {
        color = textColor
        style = Paint.Style.FILL
        alpha = 128
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRect(RectF(textBlock.boundingBox), rectPaint)
    }

    override fun setAlpha(p0: Int) {
    }

    override fun setColorFilter(p0: ColorFilter?) {
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = 0
}
