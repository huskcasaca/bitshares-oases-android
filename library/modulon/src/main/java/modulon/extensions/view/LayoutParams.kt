package modulon.extensions.view

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout.AttachedBehavior
import androidx.drawerlayout.widget.DrawerLayout

fun View.ensureViewId() {
    val temp = id
    if (temp == 0 || temp == View.NO_ID) {
        id = View.generateViewId()
    }
}
val View.ensuredViewId: Int
    get() {
        ensureViewId()
        return id
    }

fun ViewGroup.addViewWithParams(view: View, params: ViewGroup.LayoutParams? = null) = addView(view, view.layoutParams ?: params)

// drawerParams
inline fun drawerParams(width: Int = ViewGroup.LayoutParams.MATCH_PARENT, height: Int = ViewGroup.LayoutParams.MATCH_PARENT, gravity: Int = Gravity.NO_GRAVITY, block: DrawerLayout.LayoutParams.() -> Unit = {}): DrawerLayout.LayoutParams {
    return DrawerLayout.LayoutParams(width, height, gravity).apply(block)
}
inline fun viewParams(params: ViewGroup.LayoutParams?): ViewGroup.LayoutParams {
    return when (params) {
        is ViewGroup.LayoutParams -> params
        else -> ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}

// viewParams
inline fun viewParams(width: Int = ViewGroup.LayoutParams.WRAP_CONTENT, height: Int = ViewGroup.LayoutParams.WRAP_CONTENT): ViewGroup.LayoutParams {
    return ViewGroup.LayoutParams(width, height)
}

// marginParams
inline fun marginParams(params: ViewGroup.LayoutParams?): ViewGroup.MarginLayoutParams {
    return when (params) {
        is ViewGroup.MarginLayoutParams -> params
        is ViewGroup.LayoutParams -> ViewGroup.MarginLayoutParams(params)
        else -> ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
inline fun marginParams(width: Int = ViewGroup.LayoutParams.WRAP_CONTENT, height: Int = ViewGroup.LayoutParams.WRAP_CONTENT, start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0): ViewGroup.MarginLayoutParams {
    return ViewGroup.MarginLayoutParams(width, height).apply {
        setMargins(start, top, end, bottom)
    }
}

// frameParams
inline fun frameParams(params: ViewGroup.LayoutParams?): FrameLayout.LayoutParams {
    return when (params) {
        is FrameLayout.LayoutParams -> params
        is ViewGroup.MarginLayoutParams -> FrameLayout.LayoutParams(params)
        is ViewGroup.LayoutParams -> FrameLayout.LayoutParams(params)
        else -> FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.NO_GRAVITY)
    }
}
// TODO: 2022/4/26 remove
inline fun frameParams(width: Int, height: Int, start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0, gravity: Int = Gravity.NO_GRAVITY): FrameLayout.LayoutParams {
    return FrameLayout.LayoutParams(width, height, gravity).apply {
        setMargins(start, top, end, bottom)
    }
}
inline fun frameParams(
    width: Int = WRAP_CONTENT,
    height: Int = WRAP_CONTENT,
    start: Int = 0,
    top: Int = 0,
    end: Int = 0,
    bottom: Int = 0,
    gravity: Int = Gravity.NO_GRAVITY,
    block: FrameLayout.LayoutParams.() -> Unit = {},
): FrameLayout.LayoutParams {
    return FrameLayout.LayoutParams(width, height, gravity).apply {
        setMargins(start, top, end, bottom)
        block()
    }
}

// coordinatorParams
inline fun coordinatorParams(params: ViewGroup.LayoutParams?): CoordinatorLayout.LayoutParams {
    return when (params) {
        is CoordinatorLayout.LayoutParams -> params
        is ViewGroup.MarginLayoutParams -> CoordinatorLayout.LayoutParams(params)
        is ViewGroup.LayoutParams -> CoordinatorLayout.LayoutParams(params)
        else -> CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
inline fun coordinatorParams(width: Int, height: Int, start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0): CoordinatorLayout.LayoutParams {
    return CoordinatorLayout.LayoutParams(width, height).apply {
        setMargins(start, top, end, bottom)
    }
}
inline fun coordinatorParams(
    width: Int = ViewGroup.LayoutParams.WRAP_CONTENT, // TODO
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT, // TODO
    block: CoordinatorLayout.LayoutParams.() -> Unit = {},
    behavior: CoordinatorLayout.Behavior<*>? = null
): CoordinatorLayout.LayoutParams =
    CoordinatorLayout.LayoutParams(width, height).apply {
        this.behavior = behavior
        block()
    }



// linearParams
inline fun linearParams(params: ViewGroup.LayoutParams?): LinearLayout.LayoutParams {
    return when (params) {
        is LinearLayout.LayoutParams -> params
        is ViewGroup.MarginLayoutParams -> LinearLayout.LayoutParams(params)
        is ViewGroup.LayoutParams -> LinearLayout.LayoutParams(params)
        else -> LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
inline fun linearParams(
    width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    weight: Float = 0f,
    start: Int = 0,
    top: Int = 0,
    end: Int = 0,
    bottom: Int = 0,
    gravity: Int = Gravity.NO_GRAVITY,
    block: LinearLayout.LayoutParams.() -> Unit = {},
): LinearLayout.LayoutParams {
    return LinearLayout.LayoutParams(width, height, weight).apply {
        this.gravity = gravity
        setMargins(start, top, end, bottom)
        block()
    }
}