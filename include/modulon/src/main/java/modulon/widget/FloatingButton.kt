package modulon.widget

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Rect
import android.view.Gravity
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.isVisible
import modulon.R
import modulon.component.cell.AbstractCell
import modulon.component.cell.IconSize
import modulon.extensions.animation.animatorSet
import modulon.extensions.compat.isForceDarkAllowedCompat
import modulon.extensions.graphics.createRoundSelectorDrawable
import modulon.extensions.graphics.outlineProvider
import modulon.extensions.view.*
import modulon.interpolator.CubicBezierInterpolator
import modulon.layout.lazy.decorations.drawAllShaders

class FloatingButton(context: Context) : AbstractCell(context) {

    override var iconSize: IconSize = IconSize.TINY
        set(value) {
            iconView.layoutWidth = resources.getDimensionPixelSize(value.size)
            iconView.layoutHeight = resources.getDimensionPixelSize(value.size)
            requestLayout()
            field = value
        }
    
    companion object {
        private val SIZE = 72
    }

    private val hideAnimation = animatorSet {
        playTogether(
            ObjectAnimator.ofFloat(this@FloatingButton, SCALE_X, 0f),
            ObjectAnimator.ofFloat(this@FloatingButton, SCALE_Y, 0f),
            ObjectAnimator.ofFloat(this@FloatingButton, ALPHA, 0f),
        )
        duration = 200
        startDelay = 20
        interpolator = CubicBezierInterpolator.EASE_OUT_QUINT
        doOnEnd { isVisible = false }
    }

    private val showAnimation = animatorSet {
        playTogether(
            ObjectAnimator.ofFloat(this@FloatingButton, SCALE_X, 1f),
            ObjectAnimator.ofFloat(this@FloatingButton, SCALE_Y, 1f),
            ObjectAnimator.ofFloat(this@FloatingButton, ALPHA, 1f),
        )
        duration = 200
        startDelay = 20
        interpolator = CubicBezierInterpolator.EASE_OUT_QUINT
        doOnStart { isVisible = true }
    }

    fun hide() = hideAnimation.start()

    fun show() = showAnimation.start()


    init {
        background = createRoundSelectorDrawable(56.dp, context.getColor(R.color.background_component))
        view(iconView) {
            layoutGravityFrame = Gravity.CENTER
        }
//        iconView.scaleType = ImageView.ScaleType.CENTER
//        stateListAnimator = StateListAnimator().apply {
//            addState(intArrayOf(android.R.attr.state_pressed), ObjectAnimator.ofFloat(this, "translationZ", 2.dpf, 4.dpf).setDuration(200))
//            addState(intArrayOf(), ObjectAnimator.ofFloat(this, "translationZ", 4.dpf, 2.dpf).setDuration(200))
//        }
        outlineProvider = outlineProvider { _: View, outline: Outline ->
            outline.setOval(0, 0, SIZE.dp, SIZE.dp)
        }

        isForceDarkAllowedCompat = false


    }
    private val bounds = Rect()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val shaderCenter = R.color.shader_center_floating.contextColor()
    private val shaderEnd = R.color.shader_end.contextColor()
    private val backgroundC = R.color.background.contextColor()
    private val shaderSize = (SIZE / 2).dpf

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        getFocusedRect(bounds)
        drawAllShaders(canvas, bounds, paint, (bounds.bottom - bounds.top) / 2f, shaderSize, shaderCenter, shaderEnd, backgroundC)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(SIZE.dp, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(SIZE.dp, MeasureSpec.EXACTLY)
        )

    }

}