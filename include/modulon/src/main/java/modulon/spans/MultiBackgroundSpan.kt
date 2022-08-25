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
class MultiBackgroundSpan(
    override val context: Context,
    private val sepPos: Int,
    private val backgroundColor: Int,
    private val foregroundColor: Int,
    private val fontColorStart: Int,
    private val fontColorEnd: Int,
    private val revert: Boolean,
    private val bold: Boolean,
    private val scale: Float
) : ReplacementSpan(), UnionContext {

    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        paint.textScaleX = 1f
        val oldSize = paint.textSize
        val size = oldSize * FRAME_SCALE_FACTOR
        val padding = size * PADDING_SCALE_FACTOR
        paint.typeface = if (bold) typefaceBold else typefaceRegular
        paint.textSize = oldSize * scale
        val measuredSize = (paint.measureText(text, start, end) + padding * 4).roundToInt()
        paint.textSize = oldSize
        return measuredSize
    }

    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        paint.typeface = if (bold) typefaceBold else typefaceRegular
        paint.textScaleX = 1f
        val oldSize = paint.textSize
        val oldColor = paint.color

        val size = oldSize * FRAME_SCALE_FACTOR

        val radius = size * RADIUS_SCALE_FACTOR
        val padding = size * PADDING_SCALE_FACTOR
        paint.textSize = oldSize * scale

        val verticalScaleFactor = size / (paint.fontMetrics.descent - paint.fontMetrics.ascent)

        val rectInner = if (revert)
            RectF(x, y + paint.fontMetrics.ascent * verticalScaleFactor, x + paint.measureText(text, start, start + sepPos) + padding * 2, y + paint.fontMetrics.descent * verticalScaleFactor)
        else
            RectF(x + paint.measureText(text, start, start + sepPos) + padding * 2, y + paint.fontMetrics.ascent * verticalScaleFactor, x + paint.measureText(text, start, end) + padding * 4, y + paint.fontMetrics.descent * verticalScaleFactor)

        val rectOuter = RectF(x, y + paint.fontMetrics.ascent * verticalScaleFactor, x + paint.measureText(text, start, end) + padding * 4, y + paint.fontMetrics.descent * verticalScaleFactor)


        paint.color = if (revert) backgroundColor else foregroundColor
        canvas.drawRoundRect(rectOuter, radius, radius, paint)
        paint.color = if (revert) foregroundColor else backgroundColor
        canvas.drawRoundRect(rectInner, radius, radius, paint)

        val fontMetrics = paint.fontMetrics
        paint.color = fontColorEnd
        canvas.drawText(text, start + sepPos, end, x + paint.measureText(text, start, start + sepPos) + padding * 3, rectOuter.centerY() + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom, paint)
        paint.color = fontColorStart
        canvas.drawText(text, start, start + sepPos, x + padding, rectOuter.centerY() + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom, paint)
        paint.textSize = oldSize
        paint.color = oldColor
    }
}


