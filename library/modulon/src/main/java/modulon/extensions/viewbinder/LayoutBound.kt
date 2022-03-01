package modulon.extensions.viewbinder

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.util.StateSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import modulon.R
import modulon.UI
import modulon.component.ComponentSpacerCell
import modulon.extensions.graphics.createRoundRectDrawable
import modulon.extensions.view.dp
import modulon.extensions.view.dpf

fun createRoundRectSelectorDrawable1(innerColor: Int, outerColor: Int, radLT: Float = 0f, radRT: Float = 0f, radLB: Float = 0f, radRB: Float = 0f): Drawable {
    val color = innerColor and 0x00ffffff xor 0x003a3a3a or 0xff000000.toInt()
    val colorStateList = ColorStateList(arrayOf(StateSet.WILD_CARD), intArrayOf(color))
    RippleDrawable(colorStateList, ColorDrawable(innerColor), ColorDrawable(-0x1))
    return LayerDrawable(
        arrayOf(
            ColorDrawable(outerColor),
            RippleDrawable(colorStateList, createRoundRectDrawable(innerColor, radRT, radRB, radLB, radLT), createRoundRectDrawable(-0x1, radRT, radRB, radLB, radLT)),
        )
    )
}


fun View.noPadding() = setPadding(0, 0, 0, 0)

fun ViewGroup.noClipping() {
    clipToOutline = false
    clipChildren = false
    clipToPadding = false
}

fun ViewGroup.clipping() {
    clipToOutline = true
    clipChildren = true
    clipToPadding = true
}

fun ViewGroup.noMotion() {
    layoutAnimation = null
    layoutTransition = null
}








