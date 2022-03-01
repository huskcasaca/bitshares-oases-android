package modulon.layout.recycler.decorations

import android.content.Context
import android.graphics.*
import android.graphics.drawable.*
import android.view.View
import androidx.core.graphics.ColorUtils
import androidx.core.view.forEach
import androidx.recyclerview.widget.RecyclerView
import modulon.R
import modulon.extensions.view.dpf
import modulon.layout.recycler.containers.FrameHolderLayout
import modulon.layout.recycler.section.RecyclerContentLocator
import modulon.union.UnionContext
import modulon.union.toUnion
import kotlin.math.roundToInt

// TODO: 2022/2/12 make transparent
class ShaderOverlay(context: Context) : RecyclerView.ItemDecoration(), UnionContext by context.toUnion() {

    // TODO: 2022/2/8 add shaderColor to cells
    private val shaderEnd = context.getColor(R.color.shader_end)
    private val shaderCenter = context.getColor(R.color.shader_center)

    private val backgroundColor = context.getColor(R.color.background)

    private val bounds = Rect()
    private val paint  = Paint(Paint.ANTI_ALIAS_FLAG)

    private val radius = modulon.UI.CORNER_RADIUS.dpf
    
    private val shaderSize = R.dimen.global_corner_shader.contextDimen()

    private fun drawTopCorners(canvas: Canvas) {
        drawTopCorners(canvas, bounds, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
    }
    private fun drawBottomCorner(canvas: Canvas) {
        drawBottomCorner(canvas, bounds, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
    }

    // TODO: 2022/2/7 test
    private fun drawTopShader(canvas: Canvas) {
        drawTopShader(canvas, bounds, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
    }
    private fun drawBottomShader(canvas: Canvas) {
        drawBottomShader(canvas, bounds, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
    }
    private fun drawVerticalShader(canvas: Canvas) {
        drawVerticalShader(canvas, bounds, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
    }

    // TODO: 2022/2/22  not stable
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)
        var draw = false
        var t: View? = null
        var b: View? = null
        fun drawCorners() {
            if (!draw) return
            t?.let {
                parent.getDecoratedBoundsWithMargins(it, bounds)
                moveBoundsWithTranslation(it, bounds)
                drawTopCorners(canvas) to drawTopShader(canvas)
                t = null
            }
            b?.let {
                parent.getDecoratedBoundsWithMargins(it, bounds)
                moveBoundsWithTranslation(it, bounds)
                drawBottomCorner(canvas) to drawBottomShader(canvas)
                b = null
            }
        }

        parent.forEach {
            if (it is RecyclerContentLocator) {
                if (it.type == RecyclerContentLocator.SpacerType.TOP) {
                    draw = false
                    t = it
                } else {
                    b = it
                    drawCorners()
                }
            } else {
                if (shouldDrawShader(it)) {
                    parent.getDecoratedBoundsWithMargins(it, bounds)
                    moveBoundsWithTranslation(it, bounds)
                    drawVerticalShader(canvas)
                    draw = true
                }
            }
        }
        drawCorners()
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

    private fun shouldDrawShader(view: View) : Boolean {
        val inner = if (view is FrameHolderLayout) view.child ?: return false else view
        return inner.background?.tintCompat.let { it != null && it != transparentBackground }
    }

}

fun moveBoundsWithTranslation(view: View, bounds: Rect) {
    bounds.offset(view.translationX.roundToInt(), view.translationY.roundToInt())
}

fun drawVerticalShader(canvas: Canvas, bounds: Rect, paint: Paint, radius: Float, shaderSize: Float, shaderCenter: Int, shaderEnd: Int, backgroundColor: Int){
    if (bounds.top == bounds.bottom) return
    val c = RectF(bounds)
    paint.shader = LinearGradient(c.left + radius, 0f, c.left - shaderSize, 0f, shaderCenter, shaderEnd, Shader.TileMode.MIRROR)
    canvas.drawPath(Path().apply {
        moveTo(c.left - shaderSize, c.top)
        lineTo(c.left, c.top)
        lineTo(c.left, c.bottom)
        lineTo(c.left - shaderSize, c.bottom)
        close()
    }, paint)
    paint.shader = LinearGradient(c.right - radius, 0f, c.right + shaderSize, 0f, shaderCenter, shaderEnd, Shader.TileMode.MIRROR)
    canvas.drawPath(Path().apply {
        moveTo(c.right + shaderSize, c.top)
        lineTo(c.right, c.top)
        lineTo(c.right, c.bottom)
        lineTo(c.right + shaderSize, c.bottom)
        close()
    }, paint)
}


fun drawTopShader(canvas: Canvas, bounds: Rect, paint: Paint, radius: Float, shaderSize: Float, shaderCenter: Int, shaderEnd: Int, backgroundColor: Int){
    val c = RectF(bounds)
    // top
    paint.shader = LinearGradient(0f, c.top + radius, 0f, c.top - shaderSize, shaderCenter, shaderEnd, Shader.TileMode.MIRROR)
    canvas.drawPath(Path().apply {
        moveTo(c.left + radius, c.top)
        lineTo(c.right - radius, c.top)
        lineTo(c.right - radius, c.top - shaderSize)
        lineTo(c.left + radius, c.top - shaderSize)
        close()
    }, paint)
    paint.shader = RadialGradient(c.left + radius, c.top + radius, radius + shaderSize, shaderCenter, shaderEnd, Shader.TileMode.MIRROR)
    canvas.drawPath(Path().apply {
        moveTo(c.left - shaderSize, c.top + radius)
        quadTo(c.left - shaderSize, c.top - shaderSize, c.left + radius, c.top - shaderSize)
        lineTo(c.left + radius, c.top)
        quadTo(c.left, c.top, c.left, c.top + radius)
        close()
    }, paint)
    paint.shader = RadialGradient(c.right - radius, c.top + radius, radius + shaderSize, shaderCenter, shaderEnd, Shader.TileMode.MIRROR)
    canvas.drawPath(Path().apply {
        moveTo(c.right + shaderSize, c.top + radius)
        quadTo(c.right + shaderSize, c.top - shaderSize, c.right - radius, c.top - shaderSize)
        lineTo(c.right - radius, c.top)
        quadTo(c.right, c.top, c.right, c.top + radius)
        close()
    }, paint)
}

fun drawBottomShader(canvas: Canvas, bounds: Rect, paint: Paint, radius: Float, shaderSize: Float, shaderCenter: Int, shaderEnd: Int, backgroundColor: Int){
    val c = RectF(bounds)
    // bottom
    paint.shader = LinearGradient(0f, c.bottom - radius, 0f, c.bottom + shaderSize, shaderCenter, shaderEnd, Shader.TileMode.MIRROR);
    canvas.drawPath(Path().apply {
        moveTo(c.left + radius, c.bottom)
        lineTo(c.right - radius, c.bottom)
        lineTo(c.right - radius, c.bottom + shaderSize)
        lineTo(c.left + radius, c.bottom + shaderSize)
        close()
    }, paint)
    paint.shader = RadialGradient(c.left + radius, c.bottom - radius, radius + shaderSize, shaderCenter, shaderEnd, Shader.TileMode.MIRROR)
    canvas.drawPath(Path().apply {
        moveTo(c.left - shaderSize, c.bottom - radius)
        quadTo(c.left - shaderSize, c.bottom + shaderSize, c.left + radius, c.bottom + shaderSize)
        lineTo(c.left + radius, c.bottom)
        quadTo(c.left, c.bottom, c.left, c.bottom - radius)
        close()
    }, paint)
    paint.shader = RadialGradient(c.right - radius, c.bottom - radius, radius + shaderSize, shaderCenter, shaderEnd, Shader.TileMode.MIRROR)
    canvas.drawPath(Path().apply {
        moveTo(c.right + shaderSize, c.bottom - radius)
        quadTo(c.right + shaderSize, c.bottom + shaderSize, c.right - radius, c.bottom + shaderSize)
        lineTo(c.right - radius, c.bottom)
        quadTo(c.right, c.bottom, c.right, c.bottom - radius)
        close()
    }, paint)
}

fun drawTopCorners(canvas: Canvas, bounds: Rect, paint: Paint, radius: Float, shaderSize: Float, shaderCenter: Int, shaderEnd: Int, backgroundColor: Int){
    val c = RectF(bounds)
    paint.color = backgroundColor
    paint.shader = null
    canvas.drawColor(Color.TRANSPARENT)
    canvas.drawPath(Path().apply {
        moveTo(c.left - shaderSize, c.top - shaderSize)
        lineTo(c.right + shaderSize, c.top - shaderSize)
        lineTo(c.right + shaderSize, c.top)
        lineTo(c.left - shaderSize, c.top)
        close()
    }, paint)
    canvas.drawPath(Path().apply {
        moveTo(c.left - shaderSize, c.top - shaderSize)
        lineTo(c.left + radius, c.top - shaderSize)
        lineTo(c.left + radius, c.top)
        quadTo(c.left, c.top, c.left, c.top + radius)
        lineTo(c.left - shaderSize, c.top + radius)
        close()
    }, paint)
    canvas.drawPath(Path().apply {
        moveTo(c.right + shaderSize, c.top - shaderSize)
        lineTo(c.right - radius, c.top - shaderSize)
        lineTo(c.right - radius, c.top)
        quadTo(c.right, c.top, c.right, c.top + radius)
        lineTo(c.right + shaderSize, c.top + radius)
        close()
    }, paint)
}

fun drawBottomCorner(canvas: Canvas, bounds: Rect, paint: Paint, radius: Float, shaderSize: Float, shaderCenter: Int, shaderEnd: Int, backgroundColor: Int){
    val c = RectF(bounds)
    paint.color = backgroundColor
    paint.shader = null
    canvas.drawColor(Color.TRANSPARENT)
    canvas.drawPath(Path().apply {
        moveTo(c.left - shaderSize, c.bottom + shaderSize)
        lineTo(c.right + shaderSize, c.bottom + shaderSize)
        lineTo(c.right + shaderSize, c.bottom)
        lineTo(c.left - shaderSize, c.bottom)
        close()
    }, paint)
    canvas.drawPath(Path().apply {
        moveTo(c.left - shaderSize, c.bottom + shaderSize)
        lineTo(c.left + radius, c.bottom + shaderSize)
        lineTo(c.left + radius, c.bottom)
        quadTo(c.left, c.bottom, c.left, c.bottom - radius)
        lineTo(c.left - shaderSize, c.bottom - radius)
        close()
    }, paint)
    canvas.drawPath(Path().apply {
        moveTo(c.right + shaderSize, c.bottom + shaderSize)
        lineTo(c.right - radius, c.bottom + shaderSize)
        lineTo(c.right - radius, c.bottom)
        quadTo(c.right, c.bottom, c.right, c.bottom - radius)
        lineTo(c.right + shaderSize, c.bottom - radius)
        close()
    }, paint)
}

fun drawAllShaders(canvas: Canvas, bounds: Rect, paint: Paint, radius: Float, shaderSize: Float, shaderCenter: Int, shaderEnd: Int, backgroundColor: Int){
    drawVerticalShader(canvas, bounds, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
    drawTopCorners(canvas, bounds, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
    drawBottomCorner(canvas, bounds, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
    drawTopShader(canvas, bounds, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
    drawBottomShader(canvas, bounds, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
}