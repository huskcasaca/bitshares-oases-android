package modulon.spans

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.RectF
import android.text.style.ReplacementSpan
import modulon.extensions.font.typefaceBold
import modulon.extensions.font.typefaceRegular
import modulon.extensions.view.spf
import modulon.union.UnionContext
import kotlin.math.roundToInt

// TODO: 2022/2/11 replace bold with font
class RoundedBackgroundSpan(
    override val context: Context,
    private val fontSize: Float,
    private val fontColor: Int,
    private val backgroundColor: Int,
    private val bold: Boolean = true
) : ReplacementSpan(), UnionContext {

    val offset = 4.spf

    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: FontMetricsInt?): Int {
        val oldsize = paint.textSize
        paint.typeface = if (bold) typefaceBold else typefaceRegular
        paint.textSize = fontSize.spf
        val measured = (paint.measureText(text, start, end) + offset).roundToInt() + 1
        paint.textSize = oldsize
        return measured
//        return (paint.measureText(text, start, end) * (fontSize.spf / paint.textSize) + (ColoredTag.START_OFFSET * 2.4).sp).toInt()
    }

    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        paint.typeface = if (bold) typefaceBold else typefaceRegular
        paint.color = backgroundColor
        val rect = RectF(x, y + paint.fontMetrics.ascent, x + getSize(paint, text, start, end, paint.fontMetricsInt) + offset, y + paint.fontMetrics.descent)
        canvas.drawRoundRect(rect, 3.spf, 3.spf, paint)

        paint.textAlign = Paint.Align.CENTER
        paint.color = fontColor
        paint.textSize = fontSize.spf
//        canvas.drawTextRun(text, start, end, start, end, x + (paint.measureText(text, start, end) + offset) / 2f, y.toFloat() + (paint.fontMetrics.descent + paint.fontMetrics.ascent )/ 2f + (paint.fontMetrics.descent - paint.fontMetrics.ascent )/ 4f, false, paint)
        val fontMetrics = paint.fontMetrics
        canvas.drawTextRun(text, start, end, start, end, x + (getSize(paint, text, start, end, paint.fontMetricsInt) + offset) / 2f, rect.centerY() + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom, false, paint)
//        log("${text} paint.fontMetrics ${paint.fontMetrics.top} ${paint.fontMetrics.bottom} ${paint.fontMetrics.ascent} ${paint.fontMetrics.descent}")
//        log("${text} y.toFloat() - (paint.fontMetrics.descent) / 2f  ${y.toFloat() - (paint.fontMetrics.descent) / 2f}")
//        canvas.drawTextRun(text, start, end, start, end, x + (paint.measureText(text, start, end) + offset) / 2f, (y - paint.fontMetrics.descent - paint.fontMetrics.ascent) / 2f + paint.fontMetrics.descent, false, paint)

    }
}
//
//class TagSpan : ReplacementSpan() {
//
//    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: FontMetricsInt?): Int {
//        mSize = paint.measureText(text, start, end).toInt() + mRightMarginPx
//        return mSize
//    }
//    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
//        drawTagRect(canvas, x, y, paint)
//        drawTagText(canvas, text, start, end, y, paint)
//    }
//
//    private fun drawTagRect(canvas: Canvas, x: Float, y: Int, paint: Paint) {
//        paint.color = mColor
//        paint.isAntiAlias = true
//        val fontMetrics = paint.fontMetricsInt
//        val strokeWidth = paint.strokeWidth
//        val oval = RectF(x + strokeWidth + 0.5f, (y + fontMetrics.ascent).toFloat(), x + mSize + strokeWidth + 0.5f - mRightMarginPx, (y + fontMetrics.descent).toFloat())
//        paint.style = Paint.Style.STROKE
//        canvas.drawRoundRect(oval, mRadiusPx.toFloat(), mRadiusPx.toFloat(), paint)
//    }
//
//    private fun drawTagText(canvas: Canvas, text: CharSequence, start: Int, end: Int, y: Int, paint: Paint) {
//        paint.textSize = mTextSizePx.toFloat()
//        paint.color = mColor
//        paint.isAntiAlias = true
//        paint.textAlign = Paint.Align.CENTER
//        val fontMetrics = paint.fontMetricsInt
//        val textCenterX = (mSize - mRightMarginPx) / 2
//        val textBaselineY = (y - fontMetrics.descent - fontMetrics.ascent) / 2 + fontMetrics.descent
//        val tag = text.subSequence(start, end).toString()
//        canvas.drawText(tag, textCenterX.toFloat(), textBaselineY.toFloat(), paint)
//    }
//    private var mSize = 0
//    private var mColor = 0
//    private var mTextSizePx = 0
//    private var mRadiusPx = 0
//    private var mRightMarginPx = 0
//    fun RadiusBackgroundSpan(color: Int, textSizePx: Int, radiusPx: Int, rightMarginPx: Int) {
//        mColor = color
//        mTextSizePx = textSizePx
//        mRadiusPx = radiusPx
//        mRightMarginPx = rightMarginPx
//    }
//}