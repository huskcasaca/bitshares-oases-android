package modulon.extensions.view

import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.core.view.doOnLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import modulon.R
import modulon.extensions.coroutine.throttleFirst
import modulon.extensions.graphics.createRoundRectRipple
import modulon.extensions.graphics.createSelectorDrawable

var View.backgroundTintColor
    get() = (background as? ColorDrawable)?.color ?: 0
    set(value) {
        val drawable = background
        if (drawable is ColorDrawable) {
            (drawable.mutate() as ColorDrawable).color = value
        } else {
            background = ColorDrawable(value)
        }
    }

var View.backgroundSelectorColor
    get() = (background as? ColorDrawable)?.color ?: 0
    set(value) {
        background = createSelectorDrawable(value)
    }

var View.foregroundSelectorColor
    get() = (foreground as? ColorDrawable)?.color ?: 0
    set(value) {
        foreground = createRoundRectRipple(context.getColor(R.color.background_component) and 0x00ffffff xor 0x003a3a3a or 0xff000000.toInt(), context.getColor(R.color.transparent),  -0x01, 0f)
    }

var LinearLayout.isHorizontal
    get() = orientation == LinearLayout.HORIZONTAL
    set(value) {
        orientation = if(value) LinearLayout.HORIZONTAL else  LinearLayout.VERTICAL
    }

var LinearLayout.isVertical
    get() = orientation == LinearLayout.VERTICAL
    set(value) {
        orientation = if(value) LinearLayout.VERTICAL else  LinearLayout.HORIZONTAL
    }

inline fun View.doOnClick(crossinline block: () -> Unit = {}) {
    setOnClickListener { block.invoke() }
}

// TODO: 20/12/2021 apply to all
inline fun View.doOnThrottledClick(crossinline block: () -> Unit = {}) {
    val throttled = throttleFirst(CoroutineScope(Dispatchers.Main)) {
        block.invoke()
    }
    setOnClickListener { throttled.invoke() }
}

inline fun View.doOnContextClick(crossinline block: () -> Unit = {}) {
    setOnContextClickListener {
        block.invoke()
        true
    }
}

inline fun View.doOnLongClick(crossinline block: () -> Unit = {}) {
    setOnLongClickListener {
        block.invoke()
        true
    }
}

inline fun View.doOnFocusChange(crossinline block: (Boolean) -> Unit) {
    setOnFocusChangeListener { _, hasFocus -> block.invoke(hasFocus) }
}

fun View.expandTouchArea(start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0) {
    doOnLayout {
        val rect = Rect()
        getHitRect(rect)
        rect.left -= start   // increase buyTotalLeft hit area
        rect.top -= top    // increase top hit area
        rect.right += end  // increase right hit area
        rect.bottom += bottom // increase bottom hit area
        parentView.touchDelegate = TouchDelegate(rect, this)
    }
}


// ViewGroup
val View.parentView get() = parent as View

val View.parentViewGroup get() = parent as ViewGroup
val View.parentViewGroupOrNull get() = parent as? ViewGroup

fun <V : View> ViewGroup.getChildAt(index: Int) = getChildAt(index) as V
fun <V : View?> ViewGroup.getChildOrNullAt(index: Int) = getChildAt(index) as V?
fun <V : View> ViewGroup.getFirstChild() = getChildAt(0) as V
fun <V : View> ViewGroup.getAllChildren() = object : Sequence<V> {
    override fun iterator(): Iterator<V> {
        return this@getAllChildren.children.iterator() as Iterator<V>
    }
}


fun <V : View> View.getParentChildAt(index: Int) = parentViewGroup.getChildAt(index) as V
fun <V : View> View.getParentFirstChild() = parentViewGroup.getChildAt(0) as V


fun <V : View> View.nextView(): V = parentViewGroup.getChildAt(parentViewGroup.indexOfChild(this) + 1) as V
fun <V : View> View.previousView(): V = parentViewGroup.getChildAt(parentViewGroup.indexOfChild(this) - 1) as V


inline fun <V : View> ViewGroup.forEach(action: (view: V) -> Unit) {
    for (index in 0 until childCount) {
        action(getChildAt(index) as V)
    }
}


//fun setCursorDrawableColor(editText: EditText, color: Int) {
//    try {
//        val fCursorDrawableRes = TextView::class.java.getDeclaredField("mCursorDrawableRes")
//        fCursorDrawableRes.isAccessible = true
//        val mCursorDrawableRes = fCursorDrawableRes.getInt(editText)
//        val fEditor = TextView::class.java.getDeclaredField("mEditor")
//        fEditor.isAccessible = true
//        val editor = fEditor[editText]
//        val clazz: Class<*> = editor.javaClass
//        val fCursorDrawable = clazz.getDeclaredField("mCursorDrawable")
//        fCursorDrawable.isAccessible = true
//        val drawables = arrayOfNulls<Drawable>(2)
//        drawables[0] = editText.context.resources.getDrawable(mCursorDrawableRes)
//        drawables[1] = editText.context.resources.getDrawable(mCursorDrawableRes)
//        drawables[0]?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
//        drawables[1]?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
//        fCursorDrawable[editor] = drawables
//    } catch (ignored: Throwable) {
//    }
//}
