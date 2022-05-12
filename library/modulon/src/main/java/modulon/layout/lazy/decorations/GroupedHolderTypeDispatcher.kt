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
import modulon.UI
import modulon.UI.USE_FALLBACK_SHADER
import modulon.extensions.view.dpf
import modulon.layout.lazy.containers.*

class GroupedHolderTypeDispatcher(context: Context) : RecyclerView.ItemDecoration() {

    companion object {
        const val USE_DIVIDER = true
    }

    private val bounds = Rect()
    private val boundsF = RectF()
    private val dividerBounds = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.component_separator)
    }
    private fun moveBoundsWithTranslation(view: View, bounds: RectF) {
        bounds.offset(view.translationX, view.translationY)
    }
    private val dividerHeight = 1.5.dpf


    private val radius = UI.CORNER_RADIUS.dpf
    private val shaderEnd = context.getColor(R.color.shader_end)
    private val shaderCenter = context.getColor(R.color.shader_center)
    private val backgroundColor = context.getColor(R.color.background)
    private val shaderSize = context.resources.getDimension(R.dimen.global_corner_shader)


    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)
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
                if (previousViewHolder is ItemSetHolder) {
                    parent.getDecoratedBoundsWithoutMargins(previous, bounds)
                    // FIXME: 2022/5/12
//                    drawEnd = current == null || (parent.getChildViewHolder(current) !is ItemSetMarginHolder)
                    drawEnd = current != null && (parent.getChildViewHolder(current) !is ItemSetMarginHolder)
                    if (!USE_FALLBACK_SHADER) {
                        (previousViewHolder as ItemSetMarginHolder).setDrawType(drawStart, drawEnd)
                        if (USE_DIVIDER && !drawStart) {
                            if (bounds.top != bounds.bottom) {
                                dividerBounds.left = (bounds.left).toFloat()
                                dividerBounds.right = (bounds.right).toFloat()
                                dividerBounds.top = bounds.top + dividerHeight / 2
                                dividerBounds.bottom = bounds.top - dividerHeight / 2
                                canvas.drawRect(dividerBounds, dividerPaint)
                            }
                        }
                    } else {
                        boundsF.set(bounds)
                        when {
                            drawStart && drawEnd -> {
                                drawAVS(canvas, boundsF, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
                                drawTC(canvas, boundsF, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
                                drawBC(canvas, boundsF, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
                                drawTS(canvas, boundsF, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
                                drawBS(canvas, boundsF, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
                            }
                            drawStart -> {
                                drawTC(canvas, boundsF, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
                                drawTS(canvas, boundsF, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
                                drawTVS(canvas, boundsF, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
                            }
                            drawEnd -> {
                                drawBC(canvas, boundsF, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
                                drawBS(canvas, boundsF, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
                                drawBVS(canvas, boundsF, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
                            }
                            else -> {
                                drawVS(canvas, boundsF, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
                            }
                        }
                        if (USE_DIVIDER && !drawStart) {
                            if (bounds.top != bounds.bottom) {
                                dividerBounds.left = (bounds.left).toFloat()
                                dividerBounds.right = (bounds.right).toFloat()
                                dividerBounds.top = bounds.top + dividerHeight / 2
                                dividerBounds.bottom = bounds.top - dividerHeight / 2
                                canvas.drawRect(dividerBounds, dividerPaint)
                            }
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
            parent.getDecoratedBoundsWithoutMargins(it, bounds)
            if (!bounds.isEmpty) {
                attach(previousView, it)
            }
        }
        attach(previousView, null)
    }
}