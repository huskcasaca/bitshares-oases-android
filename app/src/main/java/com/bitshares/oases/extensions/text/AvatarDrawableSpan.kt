package com.bitshares.oases.extensions.text

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.style.ReplacementSpan
import com.caverock.androidsvg.SVG
import kdenticon.HashUtils
import kdenticon.Kdenticon
import modulon.spans.FRAME_SCALE_FACTOR
import modulon.spans.PADDING_SCALE_FACTOR
import modulon.spans.RADIUS_SCALE_FACTOR
import java.util.*
import kotlin.math.roundToInt

class AvatarDrawableSpan(
    private val avatarBackgroundColor: Int,
    private val backgroundColor: Int,
    private val fontColor: Int,
//    private val appendName: Boolean = false,
    private val scale: Float
) : ReplacementSpan() {

    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        paint.textScaleX = 1f
        val oldSize = paint.textSize
        val size = paint.textSize * FRAME_SCALE_FACTOR
        val padding = size * PADDING_SCALE_FACTOR

        paint.textSize = oldSize * scale
        val measuredSize = (size + paint.measureText(text, start, end) + padding * 2).roundToInt()
        paint.textSize = oldSize
        return measuredSize
    }

    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        paint.textScaleX = 1f
        val oldSize = paint.textSize
        val oldColor = paint.color

        val size = paint.textSize * FRAME_SCALE_FACTOR

        val radius = size * RADIUS_SCALE_FACTOR
        val padding = size * PADDING_SCALE_FACTOR
        paint.textSize = oldSize * scale

        val verticalScaleFactor = size / (paint.fontMetrics.descent - paint.fontMetrics.ascent)

        val rectAvatar = RectF(x, y + paint.fontMetrics.ascent * verticalScaleFactor, x + size, y + paint.fontMetrics.descent * verticalScaleFactor)
        val rectOuterRight = RectF(x, y + paint.fontMetrics.ascent * verticalScaleFactor, x + size + paint.measureText(text, start, end) + padding * 2, y + paint.fontMetrics.descent * verticalScaleFactor)
        val rectAvatarInner = RectF(x + radius, y + paint.fontMetrics.ascent * verticalScaleFactor + radius, x + size - radius, y + paint.fontMetrics.descent * verticalScaleFactor - radius)

        paint.color = backgroundColor
        canvas.drawRoundRect(rectOuterRight, radius, radius, paint)
        paint.color = avatarBackgroundColor
        canvas.drawRoundRect(rectAvatar, radius, radius, paint)
        val pic = SVG.getFromString(Kdenticon.toSvg(HashUtils.sha256(text.substring(start, end).toLowerCase(Locale.ROOT)), size.toInt() * 10, 0f)).renderToPicture()
        canvas.drawPicture(pic, rectAvatarInner)


        paint.color = fontColor
        val fontMetrics = paint.fontMetrics
        canvas.drawText(text, start, end, x + size + padding, rectOuterRight.centerY() + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom, paint)
        paint.color = oldColor
        paint.textSize = oldSize
    }
}


