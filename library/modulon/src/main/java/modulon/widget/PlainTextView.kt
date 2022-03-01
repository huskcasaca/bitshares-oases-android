package modulon.widget

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import modulon.R
import modulon.extensions.compat.isForceDarkAllowedCompat

import modulon.union.toUnion
import modulon.extensions.font.typefaceRegular
import modulon.union.UnionContext

@SuppressLint("AppCompatCustomView")
class PlainTextView(context: Context) : TextView(context), UnionContext by context.toUnion() {

    // TODO: 10/12/2021 normalize
    var textColor: Int
        @JvmName("getTextColorKt") get() = currentTextColor
        @JvmName("setTextColorKt") set(value) {
            setTextColor(value)
        }

    var lineSpacingMultiplier: Float
        @JvmName("getLineSpacingMultiplierKt") get() = getLineSpacingMultiplier()
        set(value) {
            setLineSpacing(lineSpacingExtra, value)
        }

    var lineSpacingExtra: Float
        @JvmName("getLineSpacingExtraKt") get() = getLineSpacingExtra()
        set(value) {
            setLineSpacing(value, lineSpacingMultiplier)
        }

    var isSelectable: Boolean
        @JvmName("isSelectableKt") get() = isTextSelectable()
        set(value) {
            setTextIsSelectable(value)
        }

    init {
        textSize = 16.5f
        typeface = typefaceRegular
        textColor = context.getColor(R.color.cell_text_primary)
        isForceDarkAllowedCompat = false
    }

}