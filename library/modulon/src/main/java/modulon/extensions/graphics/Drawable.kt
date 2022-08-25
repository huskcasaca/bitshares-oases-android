package modulon.extensions.graphics

import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RoundRectShape
import android.util.StateSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.core.graphics.toRectF
import modulon.extensions.view.dpf

// TODO: 2022/2/28  
fun Drawable.tint(color: Int) = apply {
    mutate()
    setTint(color)
}

// FIXME: 2022/2/24 radius order for radRT: Float, radRB: Float, radLB: Float, radLT: Float
// TODO: 2022/2/21 rename all
fun createRoundRectDrawable(color: Int, rad: Float): ShapeDrawable = createRoundRectDrawable(color, rad, rad, rad, rad)
fun createRoundRectDrawable(color: Int, radRT: Float, radRB: Float, radLB: Float, radLT: Float): ShapeDrawable {
    val defaultDrawable = ShapeDrawable(RoundRectShape(floatArrayOf(radLT, radLT, radRT, radRT, radRB, radRB, radLB, radLB), null, null))
    defaultDrawable.paint.color = color
    return defaultDrawable
}


fun createRoundRectRipple(selectorColor: Int, backgroundColor: Int, maskColor: Int = -0x01, radius: Float = 0f): RippleDrawable {
    val colorStateList = ColorStateList(arrayOf(StateSet.WILD_CARD), intArrayOf(selectorColor))
    // TODO: 2022/2/20 remove if
    val content = if (radius == 0f) ColorDrawable(backgroundColor) else createRoundRectDrawable(backgroundColor, radius)
    val maskDrawable = if (radius == 0f) ColorDrawable(maskColor) else createRoundRectDrawable(maskColor, radius)
    return RippleDrawable(colorStateList, content, maskDrawable)
}
fun createSelectorDrawable(backgroundColor: Int, radius: Float = 0f): RippleDrawable = createRoundRectRipple(backgroundColor and 0x00ffffff xor 0x003a3a3a or 0xff000000.toInt(), backgroundColor,  -0x01, radius)


// TODO: 2022/2/24 remove
fun createRoundRectRipple(selectorColor: Int, backgroundColor: Int, maskColor: Int = -0x01, radius: FloatArray): RippleDrawable {
    val colorStateList = ColorStateList(arrayOf(StateSet.WILD_CARD), intArrayOf(selectorColor))
    // TODO: 2022/2/20 remove if
    val content = createRoundRectDrawable(backgroundColor, radius[0], radius[1], radius[2], radius[3])
    val maskDrawable = createRoundRectDrawable(maskColor, radius[0], radius[1], radius[2], radius[3])
    return RippleDrawable(colorStateList, content, maskDrawable)
}
fun createSelectorDrawable(backgroundColor: Int, radius: FloatArray): RippleDrawable = createRoundRectRipple(backgroundColor and 0x00ffffff xor 0x003a3a3a or 0xff000000.toInt(), backgroundColor,  -0x01, radius)




fun createRoundRectSelectorDrawable(selectorColor: Int, backgroundColor: Int, radius: Float): Drawable = createRoundRectRipple(selectorColor, backgroundColor, radius = radius)

fun createIconSelectorDrawable(backgroundColor: Int): Drawable {
    val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val color = backgroundColor and 0x00ffffff xor 0x003a3a3a or 0xff000000.toInt()
    val colorStateList = ColorStateList(arrayOf(StateSet.WILD_CARD), intArrayOf(color))
    val mask = object : Drawable() {
        override fun draw(canvas: Canvas) {
            val bounds = bounds.toRectF()
            val rad = 20.dpf
            canvas.drawCircle(bounds.centerX(), bounds.centerY(), rad, maskPaint)
        }
        override fun setAlpha(alpha: Int) {}
        override fun setColorFilter(colorFilter: ColorFilter?) {}
        override fun getOpacity(): Int = PixelFormat.UNKNOWN
    }
    maskPaint.color = -0x1
    return RippleDrawable(colorStateList, null, mask)
}

fun createRoundSelectorDrawable(size: Int, defaultColor: Int): Drawable {
    val ovalShape = OvalShape().apply { resize(size.toFloat(), size.toFloat()) }
    val defaultDrawable = ShapeDrawable(ovalShape).apply { paint.color = defaultColor }
    val pressedDrawable = ShapeDrawable(ovalShape).apply { paint.color = -0x1 }
    val colorStateList = ColorStateList(arrayOf(StateSet.WILD_CARD), intArrayOf(defaultColor and 0x00ffffff xor 0x003a3a3a or 0xff000000.toInt()))
    return RippleDrawable(colorStateList, defaultDrawable, pressedDrawable)
}

fun createOutBoundsSelectorDrawable(backgroundColor: Int, radius: Int): Drawable {
    // TODO: 2022/2/21 replace with color blender
    val color = backgroundColor and 0x00FFFFFF or 0x28000000
    val colorStateList = ColorStateList(arrayOf(StateSet.WILD_CARD), intArrayOf(color))
    return RippleDrawable(colorStateList, null, null).apply { this.radius = radius }
}

fun createOutBoundsSelectorDrawableNoAlpha(backgroundColor: Int, radius: Int): Drawable {
    // TODO: 2022/2/21 replace with color blender
    val colorStateList = ColorStateList(arrayOf(StateSet.WILD_CARD), intArrayOf(backgroundColor))
    return RippleDrawable(colorStateList, null, null).apply { this.radius = radius }
}

var RippleDrawable.wildColor: Int
    get() = TODO()
    set(value) {
        val colorStateList = ColorStateList(arrayOf(StateSet.WILD_CARD), intArrayOf(value))
        setColor(colorStateList)
    }

fun outlineProvider(block: (view: View, outline: Outline) -> Unit) = object : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
        block.invoke(view, outline)
    }
}


