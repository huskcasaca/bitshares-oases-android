package modulon.extensions.text

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.SpannedString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import androidx.core.graphics.ColorUtils
import androidx.core.text.toSpanned
import modulon.R
import modulon.extensions.charset.BLANK_SPACE
import modulon.extensions.charset.NEWLINE
import modulon.extensions.charset.ZERO_WIDTH_BLANK_SPACE
import modulon.extensions.view.sp
import modulon.spans.BackgroundSpan
import modulon.spans.FONT_SCALE_FACTOR_90
import modulon.spans.MultiBackgroundSpan
import modulon.spans.RoundedBackgroundSpan
import modulon.union.UnionContext
import modulon.union.toUnion
import java.text.DateFormat
import java.util.*

// TODO: 2022/2/10 remove toUnion()
class ContextSpannableStringBuilder(override val context: Context): SpannableStringBuilder(), UnionContext by context.toUnion()

fun UnionContext.buildContextSpannedString(block: ContextSpannableStringBuilder.() -> Unit) =
    ContextSpannableStringBuilder(context).apply(block).toSpanned()


fun ContextSpannableStringBuilder.appendBlankSpan(): CharSequence = appendScaled(BLANK_SPACE, 0.8f)
fun ContextSpannableStringBuilder.appendSeparator(): CharSequence = if (length != 0 && (get(length - 1).toString() != BLANK_SPACE && get(length - 1).toString() != NEWLINE)) appendBlankSpan() else this
fun ContextSpannableStringBuilder.appendNewLine(): CharSequence = append("\n")

fun ContextSpannableStringBuilder.appendSpan(string: CharSequence, backgroundColor: Int, fontColor: Int, bold: Boolean = true, scale: Float = FONT_SCALE_FACTOR_90) = apply {
    appendSeparator()
    append(string)
    setSpan(BackgroundSpan(context, fontColor, backgroundColor, bold, scale), length - string.length, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    append(ZERO_WIDTH_BLANK_SPACE)
}
fun ContextSpannableStringBuilder.appendMultiSpan(stringFirst: CharSequence, stringSecond: CharSequence, backgroundColor: Int, foregroundColor: Int, fontColorStart: Int, fontColorEnd: Int, revert: Boolean = false, bold: Boolean = true, scale: Float = FONT_SCALE_FACTOR_90) = apply {
    appendSeparator()
    append(stringFirst)
    append(stringSecond)
    setSpan(MultiBackgroundSpan(context, stringFirst.length, backgroundColor, foregroundColor, fontColorStart, fontColorEnd, revert, bold, scale), length - stringFirst.length - stringSecond.length, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    append(ZERO_WIDTH_BLANK_SPACE)
}

fun ContextSpannableStringBuilder.appendFormatArguments(string: String, vararg formatArgs: CharSequence) = apply {
    append(string)
    formatArgs.forEachIndexed { position, args ->
        val sym = "%${position + 1}\$s"
        val index = indexOf(sym)
        if (index != -1) replace(index, index + sym.length, args)
    }
}

fun ContextSpannableStringBuilder.appendScaled(string: CharSequence, scale: Float) = apply {
    append(string)
    setSpan(RelativeSizeSpan(scale.coerceAtLeast(0f)), length - string.length, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}
fun ContextSpannableStringBuilder.appendScaled(scale: Float, string: ContextSpannableStringBuilder.() -> Unit) = appendScaled(buildContextSpannedString(string), scale)

fun ContextSpannableStringBuilder.appendResized(string: CharSequence, size: Float) = apply {
    append(string)
    setSpan(AbsoluteSizeSpan(size.sp), length - string.length, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}
fun ContextSpannableStringBuilder.appendResized(size: Float, string: ContextSpannableStringBuilder.() -> Unit) = appendResized(buildContextSpannedString(string), size)

fun ContextSpannableStringBuilder.appendColored(string: CharSequence, color: Int) = apply {
    append(string)
    setSpan(ForegroundColorSpan(color), length - string.length, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    append(ZERO_WIDTH_BLANK_SPACE)
}

fun ContextSpannableStringBuilder.appendColored(color: Int, string: ContextSpannableStringBuilder.() -> Unit) = appendColored(buildContextSpannedString(string), color)

fun ContextSpannableStringBuilder.appendTag(string: CharSequence, backgroundColor: Int, fontColor: Int, fontSize: Float = 12f, bold: Boolean = true) = apply {
    if (length != 0) append(BLANK_SPACE)
    append(string)
    setSpan(RoundedBackgroundSpan(context, fontSize, fontColor, backgroundColor, bold), length - string.length, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    append(BLANK_SPACE)
}


fun ContextSpannableStringBuilder.appendItem(vararg string: CharSequence) = apply { string.forEach { append(it) } }


fun UnionContext.createFormatArguments(string: String, vararg formatArgs: CharSequence) = buildContextSpannedString { appendFormatArguments(string, *formatArgs) }
fun UnionContext.createScaled(string: CharSequence, scale: Float): CharSequence = buildContextSpannedString { appendScaled(string, scale) }
fun UnionContext.createResized(string: CharSequence, size: Float): CharSequence = buildContextSpannedString { appendResized(string, size) }
fun UnionContext.createColored(string: CharSequence, color: Int) = buildContextSpannedString { appendColored(string, color) }
fun UnionContext.createTag(string: CharSequence, backgroundColor: Int, fontColor: Int, fontSize: Float = 12f, bold: Boolean = true) = buildContextSpannedString { appendTag(string, backgroundColor, fontColor, fontSize, bold) }









fun ContextSpannableStringBuilder.appendSimpleSpan(string: CharSequence, bold: Boolean = true, scale: Float = FONT_SCALE_FACTOR_90) =
    appendSpan(string, context.getColor(R.color.tag_simple_background), context.getColor(R.color.tag_text_default), bold, scale)
fun ContextSpannableStringBuilder.appendSimpleColoredSpan(string: CharSequence, color: Int, bold: Boolean = true, scale: Float = FONT_SCALE_FACTOR_90) =
    appendSpan(string, color, context.getColor(R.color.tag_text_inverted), bold, scale)

fun ContextSpannableStringBuilder.appendSimpleMultiSpan(stringFirst: CharSequence, stringSecond: CharSequence, bold: Boolean = true, scale: Float = FONT_SCALE_FACTOR_90) =
    appendMultiSpan(
        stringFirst,
        stringSecond,
        context.getColor(R.color.tag_simple_background),
        context.getColor(R.color.tag_simple_default),
        context.getColor(R.color.tag_text_default),
        context.getColor(R.color.tag_text_default),
        true,
        bold,
        scale
    )
fun ContextSpannableStringBuilder.appendSimpleColoredMultiSpan(stringFirst: CharSequence, stringSecond: CharSequence, color: Int, bold: Boolean = true, scale: Float = FONT_SCALE_FACTOR_90) =
    appendMultiSpan(
        stringFirst,
        stringSecond,
        ColorUtils.blendARGB(color, context.getColor(R.color.light_0), 0.05f),
        color,
        context.getColor(R.color.tag_text_inverted),
        context.getColor(R.color.tag_text_inverted),
        true,
        bold,
        scale
    )

fun ContextSpannableStringBuilder.appendSimpleReversedMultiSpan(stringFirst: CharSequence, stringSecond: CharSequence, bold: Boolean = true, scale: Float = FONT_SCALE_FACTOR_90) =  appendMultiSpan(stringFirst, stringSecond, context.getColor(R.color.tag_normal_dark), context.getColor(R.color.tag_normal), context.getColor(R.color.tag_text_default), context.getColor(R.color.tag_text_default), false, bold, scale)
fun ContextSpannableStringBuilder.appendSimpleColoredReversedMultiSpan(stringFirst: CharSequence, stringSecond: CharSequence, color: Int, bold: Boolean = true, scale: Float = FONT_SCALE_FACTOR_90) = appendMultiSpan(stringFirst, stringSecond, color, ColorUtils.blendARGB(color, context.getColor(R.color.light_0), 0.05f), context.getColor(R.color.tag_text_inverted), context.getColor(R.color.tag_text_inverted), false, bold, scale)



fun ContextSpannableStringBuilder.appendInstanceSpan(uid: Long) = appendSimpleMultiSpan("UID", uid.toString())
fun ContextSpannableStringBuilder.appendInstanceSpan(uid: Long, color: Int) = appendSimpleColoredMultiSpan("UID", uid.toString(), color)
fun UnionContext.createInstanceSpan(uid: Long) = buildContextSpannedString { appendInstanceSpan(uid) }
fun UnionContext.createInstanceSpan(uid: Long, color: Int) = buildContextSpannedString { appendInstanceSpan(uid, color) }




fun UnionContext.createSpan(string: CharSequence, backgroundColor: Int, fontColor: Int, bold: Boolean = false, scale: Float = FONT_SCALE_FACTOR_90) = buildContextSpannedString { appendSpan(string, backgroundColor, fontColor, bold, scale) }
fun UnionContext.createMultiSpan(stringFirst: CharSequence, stringSecond: CharSequence, backgroundColorStart: Int, backgroundColorEnd: Int, fontColorStart: Int, fontColorEnd: Int, revert: Boolean = false, bold: Boolean = false, scale: Float = FONT_SCALE_FACTOR_90) =
    buildContextSpannedString { appendMultiSpan(stringFirst, stringSecond, backgroundColorStart, backgroundColorEnd, fontColorStart, fontColorEnd, revert, bold, scale) }

fun UnionContext.createSimpleSpan(string: CharSequence, bold: Boolean = true, scale: Float = FONT_SCALE_FACTOR_90) = buildContextSpannedString { appendSimpleSpan(string, bold, scale) }
fun UnionContext.createSimpleMultiSpan(stringFirst: CharSequence, stringSecond: CharSequence, bold: Boolean = true, scale: Float = FONT_SCALE_FACTOR_90) = buildContextSpannedString { appendSimpleMultiSpan(stringFirst, stringSecond, bold, scale) }
fun UnionContext.createSimpleReversedMultiSpan(stringFirst: CharSequence, stringSecond: CharSequence, bold: Boolean = true, scale: Float = FONT_SCALE_FACTOR_90) = buildContextSpannedString { appendSimpleReversedMultiSpan(stringFirst, stringSecond, bold, scale) }



fun UnionContext.createDateBackground(date: Date): CharSequence = buildContextSpannedString { appendSimpleSpan(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(date)) }


fun ContextSpannableStringBuilder.appendDateTimeInstance(dateStyle: Int, timeStyle: Int, date: Date) = appendItem(DateFormat.getDateTimeInstance(dateStyle, timeStyle).format(date))
fun ContextSpannableStringBuilder.appendMediumDateTimeInstance(date: Date) = appendDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, date)




