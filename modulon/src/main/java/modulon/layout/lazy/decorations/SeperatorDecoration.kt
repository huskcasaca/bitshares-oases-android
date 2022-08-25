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
import modulon.extensions.view.dpf
import modulon.layout.lazy.containers.*

class SeperatorDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val bounds = Rect()
    private val boundsF = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.component_separator)
    }
    private val dividerHeight = 1.5.dpf

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)
        var previousView: View? = null
        var drawStart = false

        fun attach(previous: View?, current: View?) {
            if (previous == null) {
                previousView = current
                drawStart = false
            } else {
                val previousViewHolder = parent.getChildViewHolder(previous)
                if (previousViewHolder is ItemSetHolder) {
                    parent.getDecoratedBoundsWithoutMargins(previous, bounds)
                    if (!drawStart) {
                        if (bounds.top != bounds.bottom) {
                            boundsF.left = (bounds.left).toFloat()
                            boundsF.right = (bounds.right).toFloat()
                            boundsF.top = bounds.top + dividerHeight / 2
                            boundsF.bottom = bounds.top - dividerHeight / 2
                            canvas.drawRect(boundsF, paint)
                        }
                    }
                    previousView = current
                    drawStart = false
                } else {
                    previousView = current
                    drawStart = true
                }
            }
        }
        parent.forEach {
            parent.getDecoratedBoundsWithoutMargins(it, bounds)
            if (!bounds.isEmpty) {
                attach(previousView, it)
            }
        }
        attach(previousView, null)
    }
}