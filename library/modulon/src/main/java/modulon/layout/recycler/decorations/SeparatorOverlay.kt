package modulon.layout.recycler.decorations

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.*
import android.view.View
import androidx.core.graphics.ColorUtils
import androidx.core.view.forEach
import androidx.recyclerview.widget.RecyclerView
import modulon.R
import modulon.component.ComponentHintCell
import modulon.extensions.view.dpf
import modulon.layout.recycler.containers.FrameHolderLayout
import modulon.layout.recycler.section.RecyclerContentLocator
import modulon.layout.recycler.section.RecyclerHeader
import modulon.layout.recycler.section.RecyclerHeaderSpacer
import modulon.union.UnionContext
import modulon.union.toUnion

class SeparatorOverlay(context: Context) : RecyclerView.ItemDecoration(), UnionContext by context.toUnion() {

    private val bounds = Rect()
    private val dividerBounds = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.component_separator)
    }

    private val height = 1.5.dpf

//    // TODO: 2022/2/7 extract
//    private fun shouldDrawOver(view: View?): Boolean {
//        return view != null && view !is RecyclerContentLocator && view !is RecyclerHeaderSpacer && view !is ComponentHintCell && (view !is FrameHolderLayout || shouldDrawOver(view.child))
////            return view is BaseComponentCell || view is BaseTextCell || view is BaseTitleFieldTextCell || (view is FrameHolderLayout && shouldDrawOver(view.child))
//    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)
        if (parent.itemAnimator?.isRunning == true) return
        var lastView: View? = null
        parent.forEach {
            val temp = lastView
            if (temp != null && shouldDrawOver(temp) && shouldDrawOver(it)) {
                parent.getDecoratedBoundsWithMargins(it, bounds)
                moveBoundsWithTranslation(it, bounds)
                if (bounds.top != bounds.bottom) {
                    dividerBounds.apply {
                        set(bounds)
                        top = bounds.top + height / 2
                        bottom = bounds.top - height / 2
                    }
                    canvas.drawRect(dividerBounds, paint)
                }
            }
            lastView = it
        }
    }

    private val transparentBackground = R.color.transparent.contextColor()


    private val Drawable.tintCompat: Int
        get() = when (this) {
            is RippleDrawable -> getDrawable(0).tintCompat
            is ColorDrawable -> color
            is ShapeDrawable -> paint.color
            is GradientDrawable -> colors?.let { ColorUtils.blendARGB(it.first(), it.last(), 0.5f) } ?: 0
            else -> 0
        }


    private fun shouldDrawOver(view: View) : Boolean {
        val inner = if (view is FrameHolderLayout) view.child ?: return false else view
        return inner !is RecyclerHeaderSpacer && inner !is RecyclerContentLocator  && inner !is RecyclerHeader && inner.background?.tintCompat.let { it != null && it != transparentBackground }
    }


}