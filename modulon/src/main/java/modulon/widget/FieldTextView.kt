package modulon.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.widget.EditText
import androidx.core.view.updatePadding
import modulon.R
import modulon.extensions.compat.isForceDarkAllowedCompat
import modulon.extensions.font.typefaceRegular
import modulon.extensions.graphics.createRoundRectDrawable
import modulon.extensions.view.dp
import modulon.extensions.view.dpf
import modulon.extensions.view.moveCursorEnd
import modulon.union.UnionContext
import modulon.union.toUnion

@SuppressLint("AppCompatCustomView")
class FieldTextView(context: Context) : EditText(context), UnionContext by context.toUnion() {

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
        @JvmName("isSelectableKt") get() = isTextSelectable
        set(value) {
            setTextIsSelectable(value)
        }

    var fieldtext: CharSequence
        get() = text ?: ""
        set(text) {
            setText(text)
        }

    // TODO: 25/1/2022
    var isError = false
        set(value) {
            field = value
            background = if (value) backgroundError else backgroundNormal
        }

    var isEditable: Boolean = true
        set(value) {
            isFocusable = value
            isFocusableInTouchMode = value
            isClickable = value
            isContextClickable = value
            isLongClickable = value
            isEnabled = value
            field = value
        }

    companion object {
        private val FOCUSED_STATE_SET = intArrayOf(android.R.attr.state_focused)
        private val PRESSED_STATE_SET = intArrayOf(android.R.attr.state_pressed)
        private val FOCUSED_PRESSED_STATE_SET = intArrayOf(android.R.attr.state_focused, android.R.attr.state_focused)
        private val DISABLED_STATE_SET = intArrayOf(-android.R.attr.state_enabled)
        private val EMPTY_STATE_SET = intArrayOf()

        private val STROKE = 1.6.dp
        private val FOCUSED_STROKE = 2.dp
        private val PADDING_VERTICAL = 5.dp
    }

    private val backgroundNormal: Drawable
    private val backgroundError: Drawable

//    private val backgroundListNormal: StateListDrawable
//    private val backgroundListError: StateListDrawable

    init {
//        val textColorStateList = ColorStateList(
//            arrayOf(
//                FOCUSED_STATE_SET,
//                PRESSED_STATE_SET,
//                EMPTY_STATE_SET
//            ), intArrayOf(
//                context.getColor(R.color.text_hint_normal),
//                context.getColor(R.color.text_secondary),
//                context.getColor(R.color.text_hint_normal)
//            )
//        )
//        setHintTextColor(textColorStateList)
//
//        val focusLine = ShapeDrawable(RectShape()).apply {
//            paint.color = context.getColor(R.color.component)
//        }
//
//        val normalLine = ShapeDrawable(RectShape()).apply {
//            paint.color = context.getColor(R.color.field_text_indicator)
//        }
//
//        val errorLine = ShapeDrawable(RectShape()).apply {
//            paint.color = context.getColor(R.color.component_error)
//        }
//
//        val disabledLine = ShapeDrawable(RectShape()).apply {
//            paint.color = context.getColor(R.color.transparent)
//        }
//
//        val normalFocusedDrawable = LayerDrawable(arrayOf(focusLine)).apply {
//            setLayerHeight(0, FOCUSED_STROKE)
//            setLayerGravity(0, Gravity.BOTTOM)
//            setPadding(0, PADDING_VERTICAL, 0, PADDING_VERTICAL)
//        }
//
//        val errorFocusedDrawable = LayerDrawable(arrayOf(errorLine)).apply {
//            setLayerHeight(0, FOCUSED_STROKE)
//            setLayerGravity(0, Gravity.BOTTOM)
//            setPadding(0, PADDING_VERTICAL, 0, PADDING_VERTICAL)
//        }
//
//        val normalDrawable = LayerDrawable(arrayOf(normalLine)).apply {
//            setLayerHeight(0, STROKE)
//            setLayerGravity(0, Gravity.BOTTOM)
//            setPadding(0, PADDING_VERTICAL, 0, PADDING_VERTICAL)
//        }
//
//        val errorDrawable = LayerDrawable(arrayOf(errorLine)).apply {
//            setLayerHeight(0, STROKE)
//            setLayerGravity(0, Gravity.BOTTOM)
//            setPadding(0, PADDING_VERTICAL, 0, PADDING_VERTICAL)
//        }
//
//        val disabledDrawable = LayerDrawable(arrayOf(disabledLine)).apply {
//            setLayerHeight(0, STROKE)
//            setLayerGravity(0, Gravity.BOTTOM)
//            setPadding(0, PADDING_VERTICAL, 0, PADDING_VERTICAL)
//        }
//
//        backgroundListNormal = StateListDrawable().apply {
//            addState(DISABLED_STATE_SET, disabledDrawable)
//            addState(FOCUSED_STATE_SET, normalFocusedDrawable)
//            addState(PRESSED_STATE_SET, normalFocusedDrawable)
//            addState(FOCUSED_PRESSED_STATE_SET, normalFocusedDrawable)
//            addState(EMPTY_STATE_SET, normalDrawable)
//        }
//
//        backgroundListError = StateListDrawable().apply {
//            addState(DISABLED_STATE_SET, disabledDrawable)
//            addState(FOCUSED_STATE_SET, errorFocusedDrawable)
//            addState(PRESSED_STATE_SET, errorFocusedDrawable)
//            addState(FOCUSED_PRESSED_STATE_SET, errorFocusedDrawable)
//            addState(EMPTY_STATE_SET, errorDrawable)
//        }
//
//        background = backgroundListNormal

        textSize = 16f

        backgroundNormal = LayerDrawable(arrayOf(createRoundRectDrawable(context.getColor(R.color.drawer_background_avatar), 8.dpf)))
        backgroundError = LayerDrawable(arrayOf(createRoundRectDrawable(context.getColor(R.color.drawer_background_avatar_e), 8.dpf)))
        background = backgroundNormal

        typeface = typefaceRegular

        // FIXME: 2022/2/16 not working on MIUI and HUAWEI
//        cursorColor = context.getColor(R.color.component)
//        cursorHandleColor = context.getColor(R.color.component_light_gray)
        highlightColor = context.getColor(R.color.component)

        post { moveCursorEnd() }
        updatePadding(left = 8.dp, right = 8.dp, top = 6.dp, bottom = 6.dp)

        isForceDarkAllowedCompat = false

    }
}
