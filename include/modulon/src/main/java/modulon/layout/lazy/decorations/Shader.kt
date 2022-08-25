package modulon.layout.lazy.decorations

import android.graphics.*
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

fun RecyclerView.getDecoratedBoundsWithoutMargins(view: View, bounds: Rect) {
    val lp = view.layoutParams as RecyclerView.LayoutParams
    bounds.set(
        view.left,
        view.top,
        view.right,
        view.bottom
    )
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

// TODO: 2022/5/13 remove
fun drawAllShaders(canvas: Canvas, bounds: Rect, paint: Paint, radius: Float, shaderSize: Float, shaderCenter: Int, shaderEnd: Int, backgroundColor: Int){
    drawVerticalShader(canvas, bounds, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
    drawTopCorners(canvas, bounds, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
    drawBottomCorner(canvas, bounds, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
    drawTopShader(canvas, bounds, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
    drawBottomShader(canvas, bounds, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
}