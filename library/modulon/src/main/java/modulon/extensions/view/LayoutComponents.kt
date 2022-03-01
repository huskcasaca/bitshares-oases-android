package modulon.extensions.view

import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.ViewGroup
import modulon.R
import modulon.extensions.viewbinder.frameLayout
import modulon.layout.frame.FrameLayout

// TODO: 2022/2/19 remove
fun ViewGroup.shader(gravity: Int, block: FrameLayout.() -> Unit = {}) {
    frameLayout {
        background = GradientDrawable(when (gravity) {
            Gravity.TOP -> GradientDrawable.Orientation.TOP_BOTTOM
            Gravity.BOTTOM -> GradientDrawable.Orientation.BOTTOM_TOP
            else -> throw IllegalArgumentException()
        }, intArrayOf(R.color.shader_start_alpha.contextColor(), R.color.shader_end_alpha.contextColor()))
        setFrameParamsRow(height = 4.dp, gravity = gravity)
        apply(block)
    }
}

