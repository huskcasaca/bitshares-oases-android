package modulon.extensions.view

import android.graphics.drawable.Drawable
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import modulon.extensions.reflect.invokeField
import modulon.extensions.reflect.invokeFieldAs
import modulon.extensions.reflect.setField
import modulon.extensions.text.toStringOrEmpty
import modulon.widget.PlainTextView


// TextView
var TextView.textSpecVisibility: CharSequence
    get() = text.toStringOrEmpty()
    set(text) {
        isVisible = false
        if (text.isNotBlank()) isVisible = true
        this.text = text
    }

var TextView.textWithVisibility: CharSequence
    get() = text.toStringOrEmpty()
    set(text) {
        isVisible = text.isNotBlank()
        this.text = text
    }

var TextView.textVisible: CharSequence
    get() = text.toStringOrEmpty()
    set(text) {
        isVisible = true
        this.text = text
    }

fun EditText.moveCursorEnd() = setSelection(text?.length ?: 0)

val TextView.isEllipsized: Boolean get() = (layout != null && layout.lineCount > 0 && layout.getEllipsisCount(layout.lineCount) > 0)



var PlainTextView.cursorColor: Int
    get() = 0
    set(value) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q){
            textCursorDrawable = textCursorDrawable?.mutate()?.apply { setTint(value) }
        } else {
            try {
                val drawableRes: Int = invokeFieldAs("mCursorDrawableRes")
                val drawableArray = Array(2) {
                    drawableRes.contextDrawable().mutate().apply { setTint(value) }
                }
                invokeField("mEditor")?.setField("mCursorDrawable", drawableArray)
            } catch (e: NoSuchFieldException) { }
        }
    }


// FIXME: 19/12/2021 require multi drawables
var PlainTextView.cursorDrawable: Drawable?
    get() {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q){
            textCursorDrawable
        } else {
            try {
                invokeField("mEditor")?.invokeFieldAs("mCursorDrawable")
            } catch (e: NoSuchFieldException) {
                null
            }
        }
    }
    set(value) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q){
            textCursorDrawable = value
        } else {
            try {
                invokeField("mEditor")?.setField("mCursorDrawable", Array(2) { value })
            } catch (e: NoSuchFieldException) { }
        }
    }


var PlainTextView.cursorHandleCenterColor: Int
    get() = 0
    set(value) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q){
            textSelectHandle?.mutate()?.apply { setTint(value) }?.let { setTextSelectHandle(it) }
        } else {
            try {
                val drawableRes: Int = invokeFieldAs("mTextSelectHandleRes")
                val drawable = drawableRes.contextDrawable().mutate().apply { setTint(value) }
                invokeField("mEditor")?.setField("mSelectHandleCenter", drawable)
            } catch (e: NoSuchFieldException) { }
        }
    }


var PlainTextView.cursorHandleLeftColor: Int
    get() = 0
    set(value) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q){
            textSelectHandleLeft?.mutate()?.apply { setTint(value) }?.let { setTextSelectHandleLeft(it) }
        } else {
            try {
                val drawableRes: Int = invokeFieldAs("mTextSelectHandleLeftRes")
                val drawable = drawableRes.contextDrawable().mutate().apply { setTint(value) }
                invokeField("mEditor")?.setField("mSelectHandleLeft", drawable)
            } catch (e: NoSuchFieldException) { }
        }
    }


var PlainTextView.cursorHandleRightColor: Int
    get() = 0
    set(value) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q){
            textSelectHandleRight?.mutate()?.apply { setTint(value) }?.let { setTextSelectHandleRight(it) }
        } else {
            try {
                val drawableRes: Int = invokeFieldAs("mTextSelectHandleRightRes")
                val drawable = drawableRes.contextDrawable().mutate().apply { setTint(value) }
                invokeField("mEditor")?.setField("mSelectHandleRight", drawable)
            } catch (e: NoSuchFieldException) { }
        }
    }



var PlainTextView.cursorHandleColor: Int
    get() = 0
    set(value) {
        cursorHandleCenterColor = value
        cursorHandleLeftColor = value
        cursorHandleRightColor = value
    }

