package modulon.component.cell

import android.content.Context
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import modulon.R
import modulon.extensions.animation.TransitionExtended
import modulon.extensions.compat.isForceDarkAllowedCompat
import modulon.extensions.view.backgroundSelectorColor
import modulon.extensions.viewbinder.noClipping
import modulon.extensions.viewbinder.noMotion

open class BaseCell(context: Context) : AbstractCell(context) {

    init {
        noMotion()
        noClipping()
        isForceDarkAllowedCompat = false
    }

    final override fun addView(child: View) = super.addView(child)
    final override fun addView(child: View, index: Int) = super.addView(child, index)
    final override fun addView(child: View, params: ViewGroup.LayoutParams) = super.addView(child, params)
    final override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) = super.addView(child, index, params)
    final override fun addView(child: View, width: Int, height: Int) = super.addView(child, width, height)
    final override fun addViewInLayout(child: View, index: Int, params: ViewGroup.LayoutParams, preventRequestLayout: Boolean): Boolean = super.addViewInLayout(child, index, params, preventRequestLayout)
    final override fun addViewInLayout(child: View, index: Int, params: ViewGroup.LayoutParams): Boolean = super.addViewInLayout(child, index, params)

    private var drawCallback: (Canvas) -> Unit = {}

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCallback(canvas)
    }

    fun BaseCell.doOnDraw(callback: (Canvas) -> Unit) {
        drawCallback = callback
    }

}
