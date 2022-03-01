package modulon.extensions.view

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.drawerlayout.widget.DrawerLayout

private fun View.generateViewId() = View.generateViewId().also { id = it }
fun View.ensureViewId() = if (id == 0 || id == View.NO_ID) generateViewId() else id


val View.ensuredViewId get() = if (id == 0 || id == View.NO_ID) generateViewId() else id

private fun drawerParams(width: Int = ViewGroup.LayoutParams.MATCH_PARENT, height: Int = ViewGroup.LayoutParams.MATCH_PARENT, gravity: Int = Gravity.NO_GRAVITY, block: DrawerLayout.LayoutParams.() -> Unit = {}): DrawerLayout.LayoutParams {
    return DrawerLayout.LayoutParams(width, height, gravity).apply(block)
}
fun View.setDrawerParams(width: Int = ViewGroup.LayoutParams.MATCH_PARENT, height: Int = ViewGroup.LayoutParams.MATCH_PARENT, gravity: Int = Gravity.NO_GRAVITY, block: DrawerLayout.LayoutParams.() -> Unit = {}) {
    layoutParams = drawerParams(width, height, gravity, block)
}


internal fun ViewGroup.addViewWithParams(view: View, params: ViewGroup.LayoutParams? = null) = addView(view, view.layoutParams ?: params)

internal fun viewParams(params: ViewGroup.LayoutParams?): ViewGroup.LayoutParams {
    return when (params) {
        is ViewGroup.LayoutParams -> params
        else -> ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}

internal fun viewParams(width: Int = ViewGroup.LayoutParams.WRAP_CONTENT, height: Int = ViewGroup.LayoutParams.WRAP_CONTENT): ViewGroup.LayoutParams {
    return ViewGroup.LayoutParams(width, height)
}

internal fun marginParams(params: ViewGroup.LayoutParams?): ViewGroup.MarginLayoutParams {
    return when (params) {
        is ViewGroup.MarginLayoutParams -> params
        is ViewGroup.LayoutParams -> ViewGroup.MarginLayoutParams(params)
        else -> ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}

internal fun marginParams(width: Int = ViewGroup.LayoutParams.WRAP_CONTENT, height: Int = ViewGroup.LayoutParams.WRAP_CONTENT, start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0): ViewGroup.MarginLayoutParams {
    return ViewGroup.MarginLayoutParams(width, height).apply {
        setMargins(start, top, end, bottom)
    }
}

internal fun frameParams(params: ViewGroup.LayoutParams?): FrameLayout.LayoutParams {
    return when (params) {
        is FrameLayout.LayoutParams -> params
        is ViewGroup.MarginLayoutParams -> FrameLayout.LayoutParams(params)
        is ViewGroup.LayoutParams -> FrameLayout.LayoutParams(params)
        else -> FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.NO_GRAVITY)
    }
}

internal fun frameParams(width: Int, height: Int, start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0, gravity: Int = Gravity.NO_GRAVITY): FrameLayout.LayoutParams {
    return FrameLayout.LayoutParams(width, height, gravity).apply {
        setMargins(start, top, end, bottom)
    }
}


fun coordinatorParams(params: ViewGroup.LayoutParams?): CoordinatorLayout.LayoutParams {
    return when (params) {
        is CoordinatorLayout.LayoutParams -> params
        is ViewGroup.MarginLayoutParams -> CoordinatorLayout.LayoutParams(params)
        is ViewGroup.LayoutParams -> CoordinatorLayout.LayoutParams(params)
        else -> CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}

fun coordinatorParams(width: Int, height: Int, start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0): CoordinatorLayout.LayoutParams {
    return CoordinatorLayout.LayoutParams(width, height).apply {
        setMargins(start, top, end, bottom)
    }
}

fun coordinatorParams(width: Int = ViewGroup.LayoutParams.MATCH_PARENT, height: Int = ViewGroup.LayoutParams.MATCH_PARENT, block: CoordinatorLayout.LayoutParams.() -> Unit = {}): CoordinatorLayout.LayoutParams = CoordinatorLayout.LayoutParams(width, height).apply(block)
//fun View.setCoordinatorParams(width: Int = ViewGroup.LayoutParams.MATCH_PARENT, height: Int = ViewGroup.LayoutParams.MATCH_PARENT, block: CoordinatorLayout.LayoutParams.() -> Unit = {}) {
//    layoutParams = coordinatorParams(width, height, block)
//}



internal fun linearParams(params: ViewGroup.LayoutParams?): LinearLayout.LayoutParams {
    return when (params) {
        is LinearLayout.LayoutParams -> params
        is ViewGroup.MarginLayoutParams -> LinearLayout.LayoutParams(params)
        is ViewGroup.LayoutParams -> LinearLayout.LayoutParams(params)
        else -> LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}

internal fun linearParams(width: Int, height: Int, weight: Float = 0f, start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0, gravity: Int = Gravity.NO_GRAVITY): LinearLayout.LayoutParams {
    return LinearLayout.LayoutParams(width, height, weight).apply {
        this.gravity = gravity
        setMargins(start, top, end, bottom)
    }
}