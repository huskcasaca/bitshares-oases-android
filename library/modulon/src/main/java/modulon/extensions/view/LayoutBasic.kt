package modulon.extensions.view

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import modulon.dialog.button
import modulon.extensions.compat.showBottomDialog
import modulon.union.Union

const val WRAP_CONTENT = -2
const val MATCH_PARENT = -1

fun ViewGroup.addNoParams(view: View) = addView(view)

fun ViewGroup.addViewIndexed(view: View, index: Int = -1) = addView(view, index)

fun ViewGroup.addDefault(view: View, width: Int = WRAP_CONTENT, height: Int = WRAP_CONTENT, block: ViewGroup.LayoutParams.() -> Unit = {}) = addViewWithParams(view, viewParams(width, height).apply(block))
fun ViewGroup.addDefaultRow(view: View, width: Int = MATCH_PARENT, height: Int = WRAP_CONTENT, block: ViewGroup.LayoutParams.() -> Unit = {}) = addViewWithParams(view, viewParams(width, height).apply(block))
fun ViewGroup.addDefaultFill(view: View, width: Int = MATCH_PARENT, height: Int = MATCH_PARENT, block: ViewGroup.LayoutParams.() -> Unit = {}) = addViewWithParams(view, viewParams(width, height).apply(block))


fun View.setParams(width: Int = WRAP_CONTENT, height: Int = WRAP_CONTENT, block: ViewGroup.LayoutParams.() -> Unit = {}) { layoutParams = viewParams(width, height).apply(block) }
fun View.setParamsRow(width: Int = MATCH_PARENT, height: Int = WRAP_CONTENT, block: ViewGroup.LayoutParams.() -> Unit = {}) = setParams(width, height, block)
fun View.setParamsFill(width: Int = MATCH_PARENT, height: Int = MATCH_PARENT, block: ViewGroup.LayoutParams.() -> Unit = {}) = setParams(width, height, block)


fun View.setMarginParams(width: Int = WRAP_CONTENT, height: Int = WRAP_CONTENT, block: ViewGroup.MarginLayoutParams.() -> Unit = {}) { layoutParams = marginParams(width, height).apply(block) }
fun View.setMarginParamsRow(width: Int = MATCH_PARENT, height: Int = WRAP_CONTENT, block: ViewGroup.MarginLayoutParams.() -> Unit = {}) = setMarginParams(width, height, block)
fun View.setMarginParamsFill(width: Int = MATCH_PARENT, height: Int = MATCH_PARENT, block: ViewGroup.MarginLayoutParams.() -> Unit = {}) = setMarginParams(width, height, block)


// MarginLayoutParams Start


// FrameLayoutParams
// TODO: 2021/1/28 rename
fun FrameLayout.addWrap(view: View, width: Int = WRAP_CONTENT, height: Int = WRAP_CONTENT, start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0, gravity: Int = Gravity.NO_GRAVITY, block: FrameLayout.LayoutParams.() -> Unit = {}) = addViewWithParams(view, frameParams(width, height, start, top, end, bottom, gravity).apply(block))
fun FrameLayout.addRow(view: View, width: Int = MATCH_PARENT, height: Int = WRAP_CONTENT, start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0, gravity: Int = Gravity.NO_GRAVITY, block: FrameLayout.LayoutParams.() -> Unit = {}) = addWrap(view, width, height, start, top, end, bottom, gravity, block)
fun FrameLayout.addFill(view: View, width: Int = MATCH_PARENT, height: Int = MATCH_PARENT, start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0, gravity: Int = Gravity.NO_GRAVITY, block: FrameLayout.LayoutParams.() -> Unit = {}) = addWrap(view, width, height, start, top, end, bottom, gravity, block)

fun View.setFrameParams(width: Int = WRAP_CONTENT, height: Int = WRAP_CONTENT, start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0, gravity: Int = Gravity.NO_GRAVITY, block: FrameLayout.LayoutParams.() -> Unit = {}) { layoutParams = frameParams(width, height, start, top, end, bottom, gravity).apply(block) }
fun View.setFrameParamsRow(width: Int = MATCH_PARENT, height: Int = WRAP_CONTENT, start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0, gravity: Int = Gravity.NO_GRAVITY, block: FrameLayout.LayoutParams.() -> Unit = {}) = setFrameParams(width, height, start, top, end, bottom, gravity, block)
fun View.setFrameParamsFill(width: Int = MATCH_PARENT, height: Int = MATCH_PARENT, start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0, gravity: Int = Gravity.NO_GRAVITY, block: FrameLayout.LayoutParams.() -> Unit = {}) = setFrameParams(width, height, start, top, end, bottom, gravity, block)


// LinearLayoutParams
// TODO: 2021/1/28 remove block
fun LinearLayout.addWrap(view: View, width: Int = WRAP_CONTENT, height: Int = WRAP_CONTENT, weight: Float = 0f, start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0, gravity: Int = Gravity.NO_GRAVITY, block: LinearLayout.LayoutParams.() -> Unit = {}) = addViewWithParams(view, linearParams(width, height, weight, start, top, end, bottom, gravity).apply(block))
fun LinearLayout.addRow(view: View, width: Int = MATCH_PARENT, height: Int = WRAP_CONTENT, weight: Float = 0f, start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0, gravity: Int = Gravity.NO_GRAVITY, block: LinearLayout.LayoutParams.() -> Unit = {}) = addWrap(view, width, height, weight, start, top, end, bottom, gravity, block)
fun LinearLayout.addFill(view: View, width: Int = MATCH_PARENT, height: Int = MATCH_PARENT, weight: Float = 0f, start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0, gravity: Int = Gravity.NO_GRAVITY, block: LinearLayout.LayoutParams.() -> Unit = {}) = addWrap(view, width, height, weight, start, top, end, bottom, gravity, block)

// TODO: 2021/1/28 remove block
fun View.setLinearParams(width: Int = WRAP_CONTENT, height: Int = WRAP_CONTENT, weight: Float = 0f, start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0, gravity: Int = Gravity.NO_GRAVITY, block: LinearLayout.LayoutParams.() -> Unit = {}) { layoutParams = linearParams(width, height, weight, start, top, end, bottom, gravity).apply(block) }
fun View.setLinearParamsRow(width: Int = MATCH_PARENT, height: Int = WRAP_CONTENT, weight: Float = 0f, start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0, gravity: Int = Gravity.NO_GRAVITY, block: LinearLayout.LayoutParams.() -> Unit = {}) = setLinearParams(width, height, weight, start, top, end, bottom, gravity, block)
fun View.setLinearParamsFill(width: Int = MATCH_PARENT, height: Int = MATCH_PARENT, weight: Float = 0f, start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0, gravity: Int = Gravity.NO_GRAVITY, block: LinearLayout.LayoutParams.() -> Unit = {}) = setLinearParams(width, height, weight, start, top, end, bottom, gravity, block)


var View.layoutMarginStart: Int
    get() = marginParams(layoutParams).leftMargin // TODO
    set(value) { layoutParams = marginParams(layoutParams).apply { leftMargin = value } }
var View.layoutMarginTop: Int
    get() = marginParams(layoutParams).topMargin // TODO
    set(value) { layoutParams = marginParams(layoutParams).apply { topMargin = value } }
var View.layoutMarginEnd: Int
    get() = marginParams(layoutParams).rightMargin // TODO
    set(value) { layoutParams = marginParams(layoutParams).apply { rightMargin = value } }
var View.layoutMarginBottom: Int
    get() = marginParams(layoutParams).bottomMargin // TODO
    set(value) { layoutParams = marginParams(layoutParams).apply { bottomMargin = value } }


var View.layoutWidth: Int
    get() = viewParams(layoutParams).width // TODO
    set(value) { layoutParams = viewParams(layoutParams).apply { width = value } }
var View.layoutHeight: Int
    get() = viewParams(layoutParams).height // TODO
    set(value) { layoutParams = viewParams(layoutParams).apply { height = value } }
var View.layoutGravityFrame: Int
    get() = frameParams(layoutParams).gravity // TODO
    set(value) { layoutParams = frameParams(layoutParams).apply { gravity = value } }

var View.layoutGravityLinear: Int
    get() = linearParams(layoutParams).gravity // TODO
    set(value) { layoutParams = linearParams(layoutParams).apply { gravity = value } }
var View.layoutWeightLinear: Float
    get() = linearParams(layoutParams).weight // TODO
    set(value) { layoutParams = linearParams(layoutParams).apply { weight = value } }


var View.layoutGravity: Int
    get() = TODO()
    set(value) { layoutParams.apply {
        when (this) {
            is FrameLayout.LayoutParams -> gravity = value
            is LinearLayout.LayoutParams -> gravity = value
        }
    } }
