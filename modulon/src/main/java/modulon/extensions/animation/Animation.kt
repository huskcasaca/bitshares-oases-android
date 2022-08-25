package modulon.extensions.animation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.*
import android.view.animation.Animation
import modulon.extensions.view.*

val fadeLayoutAnimation = LayoutAnimationController(AnimationSet(false).apply {
    addAnimation(TranslateAnimation(0f, 0f, (-4).dpf, 0f).apply {
        duration = 240
        fillAfter = true
        interpolator = DecelerateInterpolator()
    })
    addAnimation(AlphaAnimation(0.6f, 1f).apply {
        duration = 240
    })
}, 0.05f)


fun AnimationSet.translate(fromXDelta: Float = 0f, toXDelta: Float = 0f, fromYDelta: Float = 0f, toYDelta: Float = 0f, block: TranslateAnimation.() -> Unit = {}) {
    addAnimation(TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta).apply(block))
}

fun AnimationSet.translate(
    fromXType: Int = Animation.ABSOLUTE, fromXValue: Float = 0f,
    toXType: Int = Animation.ABSOLUTE, toXValue: Float = 0f,
    fromYType: Int = Animation.ABSOLUTE, fromYValue: Float = 0f,
    toYType: Int = Animation.ABSOLUTE, toYValue: Float = 0f,
    block: TranslateAnimation.() -> Unit = {}) {
    addAnimation(TranslateAnimation(fromXType, fromXValue, toXType, toXValue, fromYType, fromYValue, toYType, toYValue).apply(block))
}

fun AnimationSet.alpha(fromAlpha: Float = 0f, toAlpha: Float = 0f, block: AlphaAnimation.() -> Unit = {}) {
    addAnimation(AlphaAnimation(fromAlpha, toAlpha).apply(block))
}

fun animatorSet(block: AnimatorSet.() -> Unit) = AnimatorSet().apply(block)
fun animationSet(shareInterpolator: Boolean = true, block: AnimationSet.() -> Unit) = AnimationSet(shareInterpolator).apply(block)

fun View.rotation45(block: ObjectAnimator.() -> Unit) {
    ObjectAnimator.ofFloat(this@rotation45,  "rotation", 0f, 45f).apply {
        duration = 1000
        interpolator = OvershootInterpolator()
        block()
    }
}

