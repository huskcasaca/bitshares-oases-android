package modulon.layout.lazy.decorations

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import androidx.core.view.forEach
import androidx.recyclerview.widget.RecyclerView
import modulon.R
import modulon.extensions.stdlib.logcat
import modulon.extensions.view.dpf
import modulon.layout.lazy.containers.GroupedRowHolder
import kotlin.math.roundToInt

class GroupedHolderTypeDispatcher(context: Context) : RecyclerView.ItemDecoration() {

    private val bounds = Rect()
    private val dividerBounds = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.component_separator)
    }
    private fun moveBoundsWithTranslation(view: View, bounds: RectF) {
        bounds.offset(view.translationX, view.translationY)
    }
    private val dividerHeight = 1.5.dpf

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        var previousView: View? = null
        var drawStart = false
        var drawEnd = false

        fun attach(previous: View?, current: View?) {
            if (previous == null) {
                previousView = current
                drawStart = false
                drawEnd = false
            } else {
                val previousViewHolder = parent.getChildViewHolder(previous)
                if (previousViewHolder is GroupedRowHolder) {
                    drawEnd = current != null && parent.getChildViewHolder(current) !is GroupedRowHolder
                    previousViewHolder.setDrawType(drawStart, drawEnd)
                    if (!drawStart) {
                        parent.getDecoratedBoundsWithMargins(previous, bounds)
                        val p = previous.layoutParams as RecyclerView.LayoutParams
                        if (bounds.top != bounds.bottom) {
                            dividerBounds.left = (bounds.left + p.leftMargin).toFloat()
                            dividerBounds.right = (bounds.right - p.rightMargin).toFloat()
                            dividerBounds.top = bounds.top + dividerHeight / 2
                            dividerBounds.bottom = bounds.top - dividerHeight / 2
                            moveBoundsWithTranslation(previous, dividerBounds)
                            c.drawRect(dividerBounds, paint)
                        }
                    }
                    previousView = current
                    drawStart = false
                    drawEnd = false
                } else {
                    previousView = current
                    drawStart = true
                    drawEnd = false
                }
            }
        }
        parent.forEach {
            attach(previousView, it)
        }
        attach(previousView, null)
    }
}