package modulon.spans

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.style.ReplacementSpan
import modulon.extensions.font.typefaceBold
import modulon.extensions.font.typefaceRegular
import modulon.union.UnionContext
import kotlin.math.roundToInt

// TODO: 2022/2/11 replace bold with font
class BackgroundSpan(
    override val context: Context,
    private val fontColor: Int,
    private val backgroundColor: Int,
    private val bold: Boolean = false,
    private val scale: Float
) : ReplacementSpan(), UnionContext {

    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        paint.textScaleX = 1f
        if (bold) paint.typeface = typefaceBold else typefaceRegular
        val oldSize = paint.textSize
        val padding = oldSize * FRAME_SCALE_FACTOR * PADDING_SCALE_FACTOR
        paint.textSize = oldSize * scale
        val measuredSize = (paint.measureText(text, start, end) + padding * 2).roundToInt()
        paint.textSize = oldSize
        return measuredSize
    }

    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        paint.textScaleX = 1f
        if (bold) paint.typeface = typefaceBold else typefaceRegular

        val oldSize = paint.textSize
        val oldColor = paint.color

        val size = oldSize * FRAME_SCALE_FACTOR

        val radius = size * RADIUS_SCALE_FACTOR
        val padding = size * PADDING_SCALE_FACTOR
        paint.textSize = oldSize * scale

        val verticalScaleFactor = size / (paint.fontMetrics.descent - paint.fontMetrics.ascent)

        val rectOuterRight = RectF(x, y + paint.fontMetrics.ascent * verticalScaleFactor, x + paint.measureText(text, start, end) + padding * 2, y + paint.fontMetrics.descent * verticalScaleFactor)

        paint.color = backgroundColor
        canvas.drawRoundRect(rectOuterRight, radius, radius, paint)

        paint.color = fontColor
        val fontMetrics = paint.fontMetrics
        canvas.drawText(text, start, end, x + padding, rectOuterRight.centerY() + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom, paint)

        paint.textSize = oldSize
        paint.color = oldColor
    }


}


