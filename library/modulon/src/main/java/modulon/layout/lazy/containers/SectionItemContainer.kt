package modulon.layout.lazy.containers

import android.content.Context
import android.graphics.*
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.recyclerview.widget.RecyclerView
import modulon.R
import modulon.extensions.view.MATCH_PARENT
import modulon.extensions.view.WRAP_CONTENT
import modulon.extensions.view.backgroundTintColor
import modulon.extensions.view.dpf
import modulon.layout.lazy.LazyListView
import modulon.layout.stack.StackView

class SectionItemContainer(
    private val view: View,
    private val params: ViewGroup.LayoutParams? = null,
    private val isGrouped: Boolean = false
) : LazyListView.Container<View>() {

    init {
        if (params != null) {
            view.layoutParams = params
        }
    }

    override var creator: () -> View = { view }

    override val adapter =
        object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, dropped: MutableList<Any>) {}
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
                if (isGrouped)
                    GroupedRowHolder(parent.context).apply {
                        (view.parent as? ViewGroup)?.removeView(view)
                        replace(view)
                    }
                else
                    SingleRowHolder(parent.context).apply {
                        (view.parent as? ViewGroup)?.removeView(view)
                        replace(view)
                    }
            override fun getItemCount() = 1
        }
}

// TODO: 2022/4/26
open class GroupedRowHolder(context: Context) : RecyclerView.ViewHolder(Item(context)) {

    private val container get() = itemView as Item
    fun setDrawType(start: Boolean, end: Boolean) {
        container.setDrawType(start, end)
    }

    class Item(context: Context) : StackView(context) {

        private var drawStart = false
        private var drawEnd = false

        fun setDrawType(start: Boolean, end: Boolean) {
            if (drawStart != start || drawEnd != end) {
                drawStart = start
                drawEnd = end
                outlined.invalidateOutline()
                invalidate()
            }
        }
        private val radius = modulon.UI.CORNER_RADIUS.dpf

        // TODO: 2022/2/8 add shaderColor to cells
        private val shaderEnd = context.getColor(R.color.shader_end)
        private val shaderCenter = context.getColor(R.color.shader_center)
        private val backgroundColor = context.getColor(R.color.background)
        private val bounds = Rect()
        private val boundsF = RectF()
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        private val shaderSize = R.dimen.global_corner_shader.contextDimen()

        private val outlined: StackView = StackView(context).apply {
            clipToOutline = true
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(0, 0, view.width, view.height, 0.dpf)
                    when {
                        drawStart && drawEnd ->
                            outline.setRoundRect(0, 0, view.width, view.height, radius)
                        drawStart ->
                            outline.setRoundRect(0, 0, view.width, (view.height + radius).toInt(), radius)
                        drawEnd ->
                            outline.setRoundRect(0, (-radius).toInt(), view.width, view.height, radius)
                        else ->
                            outline.setRoundRect(0, 0, view.width, view.height, 0f)
                    }
                }
            }
        }

        val child: View? get() = (getChildAt(0) as StackView).getChildAt(0)


        init {
            clipToOutline = false
            layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                leftMargin = context.resources.getDimensionPixelSize(R.dimen.global_spacer_size)
                rightMargin = context.resources.getDimensionPixelSize(R.dimen.global_spacer_size)
            }
            setWillNotDraw(false)
            addView(outlined)
        }

        override fun onDraw(canvas: Canvas) {
            canvas.getClipBounds(bounds)
            boundsF.set(bounds)
            when {
                drawStart && drawEnd -> {
                    drawTS(canvas, boundsF, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
                    drawBS(canvas, boundsF, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
                    drawAVS(canvas, boundsF, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
                }
                drawStart -> {
                    drawTS(canvas, boundsF, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
                    drawTVS(canvas, boundsF, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
                }
                drawEnd -> {
                    drawBS(canvas, boundsF, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
                    drawBVS(canvas, boundsF, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
                }
                else -> {
                    drawVS(canvas, boundsF, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
                }
            }
            super.onDraw(canvas)
        }
        fun replace(view: View) {
            outlined.removeAllViews()
            outlined.addView(view)
        }
    }

    fun replace(view: View) = container.replace(view)

}

open class SingleRowHolder(context: Context) : RecyclerView.ViewHolder(Item(context)) {
    private val container get() = itemView as Item
    class Item(context: Context) : StackView(context) {
        init {
            clipToOutline = true
            clipChildren = false
            clipToPadding = false
            layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                leftMargin = context.resources.getDimensionPixelSize(R.dimen.global_spacer_size)
                rightMargin = context.resources.getDimensionPixelSize(R.dimen.global_spacer_size)
            }
        }
    }
    fun replace(view: View) {
        container.removeAllViews()
        container.addView(view)
    }
}



fun drawTS(canvas: Canvas, bounds: RectF, paint: Paint, radius: Float, shaderSize: Float, shaderCenter: Int, shaderEnd: Int, backgroundColor: Int){
    // top
    paint.shader = LinearGradient(0f, bounds.top + radius, 0f, bounds.top - shaderSize, shaderCenter, shaderEnd, Shader.TileMode.MIRROR)
    canvas.drawRect(bounds.left + radius,  bounds.top, bounds.right - radius, bounds.top - shaderSize, paint)

    paint.shader = RadialGradient(bounds.left + radius, bounds.top + radius, radius + shaderSize, shaderCenter, shaderEnd, Shader.TileMode.MIRROR)
    canvas.drawPath(Path().apply {
        moveTo(bounds.left - shaderSize, bounds.top + radius)
        quadTo(bounds.left - shaderSize, bounds.top - shaderSize, bounds.left + radius, bounds.top - shaderSize)
        lineTo(bounds.left + radius, bounds.top)
        quadTo(bounds.left, bounds.top, bounds.left, bounds.top + radius)
        close()
    }, paint)

    paint.shader = RadialGradient(bounds.right - radius, bounds.top + radius, radius + shaderSize, shaderCenter, shaderEnd, Shader.TileMode.MIRROR)
    canvas.drawPath(Path().apply {
        moveTo(bounds.right + shaderSize, bounds.top + radius)
        quadTo(bounds.right + shaderSize, bounds.top - shaderSize, bounds.right - radius, bounds.top - shaderSize)
        lineTo(bounds.right - radius, bounds.top)
        quadTo(bounds.right, bounds.top, bounds.right, bounds.top + radius)
        close()
    }, paint)
}

fun drawBS(canvas: Canvas, bounds: RectF, paint: Paint, radius: Float, shaderSize: Float, shaderCenter: Int, shaderEnd: Int, backgroundColor: Int){
    // bottom
    paint.shader = LinearGradient(0f, bounds.bottom - radius, 0f, bounds.bottom + shaderSize, shaderCenter, shaderEnd, Shader.TileMode.MIRROR);
    canvas.drawRect(bounds.left + radius,  bounds.bottom, bounds.right - radius, bounds.bottom + shaderSize, paint)

    paint.shader = RadialGradient(bounds.left + radius, bounds.bottom - radius, radius + shaderSize, shaderCenter, shaderEnd, Shader.TileMode.MIRROR)
    canvas.drawPath(Path().apply {
        moveTo(bounds.left - shaderSize, bounds.bottom - radius)
        quadTo(bounds.left - shaderSize, bounds.bottom + shaderSize, bounds.left + radius, bounds.bottom + shaderSize)
        lineTo(bounds.left + radius, bounds.bottom)
        quadTo(bounds.left, bounds.bottom, bounds.left, bounds.bottom - radius)
        close()
    }, paint)

    paint.shader = RadialGradient(bounds.right - radius, bounds.bottom - radius, radius + shaderSize, shaderCenter, shaderEnd, Shader.TileMode.MIRROR)
    canvas.drawPath(Path().apply {
        moveTo(bounds.right + shaderSize, bounds.bottom - radius)
        quadTo(bounds.right + shaderSize, bounds.bottom + shaderSize, bounds.right - radius, bounds.bottom + shaderSize)
        lineTo(bounds.right - radius, bounds.bottom)
        quadTo(bounds.right, bounds.bottom, bounds.right, bounds.bottom - radius)
        close()
    }, paint)
}

fun drawVS(canvas: Canvas, bounds: RectF, paint: Paint, radius: Float, shaderSize: Float, shaderCenter: Int, shaderEnd: Int, backgroundColor: Int){
    if (bounds.bottom - bounds.top <= 0f) return
    paint.shader = LinearGradient(bounds.left + radius, 0f, bounds.left - shaderSize, 0f, shaderCenter, shaderEnd, Shader.TileMode.MIRROR)
    canvas.drawRect(bounds.left, bounds.top, bounds.left - shaderSize, bounds.bottom, paint)

    paint.shader = LinearGradient(bounds.right - radius, 0f, bounds.right + shaderSize, 0f, shaderCenter, shaderEnd, Shader.TileMode.MIRROR)
    canvas.drawRect(bounds.right, bounds.top, bounds.right + shaderSize, bounds.bottom, paint)
}


fun drawTVS(canvas: Canvas, bounds: RectF, paint: Paint, radius: Float, shaderSize: Float, shaderCenter: Int, shaderEnd: Int, backgroundColor: Int){
    if (bounds.bottom - bounds.top <= radius) return
    paint.shader = LinearGradient(bounds.left + radius, 0f, bounds.left - shaderSize, 0f, shaderCenter, shaderEnd, Shader.TileMode.MIRROR)
    canvas.drawRect(bounds.left, bounds.top + radius, bounds.left - shaderSize, bounds.bottom, paint)

    paint.shader = LinearGradient(bounds.right - radius, 0f, bounds.right + shaderSize, 0f, shaderCenter, shaderEnd, Shader.TileMode.MIRROR)
    canvas.drawRect(bounds.right, bounds.top + radius, bounds.right + shaderSize, bounds.bottom, paint)
}

fun drawBVS(canvas: Canvas, bounds: RectF, paint: Paint, radius: Float, shaderSize: Float, shaderCenter: Int, shaderEnd: Int, backgroundColor: Int){
    if (bounds.bottom - bounds.top <= radius) return
    paint.shader = LinearGradient(bounds.left + radius, 0f, bounds.left - shaderSize, 0f, shaderCenter, shaderEnd, Shader.TileMode.MIRROR)
    canvas.drawRect(bounds.left, bounds.top, bounds.left - shaderSize, bounds.bottom - radius, paint)

    paint.shader = LinearGradient(bounds.right - radius, 0f, bounds.right + shaderSize, 0f, shaderCenter, shaderEnd, Shader.TileMode.MIRROR)
    canvas.drawRect(bounds.right, bounds.top, bounds.right + shaderSize, bounds.bottom - radius, paint)
}

fun drawAVS(canvas: Canvas, bounds: RectF, paint: Paint, radius: Float, shaderSize: Float, shaderCenter: Int, shaderEnd: Int, backgroundColor: Int){
    if (bounds.bottom - bounds.top <= radius * 2) return
    paint.shader = LinearGradient(bounds.left + radius, 0f, bounds.left - shaderSize, 0f, shaderCenter, shaderEnd, Shader.TileMode.MIRROR)
    canvas.drawRect(bounds.left, bounds.top + radius, bounds.left - shaderSize, bounds.bottom - radius, paint)

    paint.shader = LinearGradient(bounds.right - radius, 0f, bounds.right + shaderSize, 0f, shaderCenter, shaderEnd, Shader.TileMode.MIRROR)
    canvas.drawRect(bounds.right, bounds.top + radius, bounds.right + shaderSize, bounds.bottom - radius, paint)
}