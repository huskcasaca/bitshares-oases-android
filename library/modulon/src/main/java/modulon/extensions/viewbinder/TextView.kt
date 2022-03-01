package modulon.extensions.viewbinder

import android.text.TextUtils
import android.widget.FrameLayout
import modulon.R
import modulon.extensions.font.typefaceRegular
import modulon.extensions.view.dpf
import modulon.widget.FieldTextView
import modulon.widget.PlainTextView

fun PlainTextView.titleStyle() {
    textSize = 16.5f
//    isSingleLine = true
    typeface = typefaceRegular
    textColor = context.getColor(R.color.cell_text_primary)
    autoSpacing()
}

fun PlainTextView.subtitleStyle() {
    textSize = 16.5f
//    isSingleLine = true
    typeface = typefaceRegular
    textColor = context.getColor(R.color.cell_text_secondary)
    autoSpacing()
}

fun PlainTextView.textStyle() {
    textSize = 16.5f
//    isSingleLine = true
    typeface = typefaceRegular
    textColor = context.getColor(R.color.cell_text_primary)
    autoSpacing()
}

fun PlainTextView.subtextStyle() {
    textSize = 14.5f
    typeface = typefaceRegular
    textColor = context.getColor(R.color.cell_text_secondary)
    autoSpacing()
}

fun PlainTextView.autoSpacing(multiplier: Float = 1f) {
//    lineSpacingExtra = textSize / 8
    lineSpacingExtra = 2.dpf
    lineSpacingMultiplier = multiplier
}

fun PlainTextView.startScrolling() {
    isSingleLine = true
    isSelected = true
    ellipsize = TextUtils.TruncateAt.MARQUEE
    focusable = FrameLayout.FOCUSABLE_AUTO
    marqueeRepeatLimit = -1
}

fun FieldTextView.autoSpacing(multiplier: Float = 1f) {
//    lineSpacingExtra = textSize / 8
    lineSpacingExtra = 2.dpf
    lineSpacingMultiplier = multiplier
}


var PlainTextView.isTextError: Boolean
    get() = textColor == context.getColor(R.color.component_error)
    set(value) {
        textColor = if (value) context.getColor(R.color.component_error) else context.getColor(R.color.cell_text_secondary)
    }