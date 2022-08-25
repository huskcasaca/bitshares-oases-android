package modulon.extensions.animation

import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable


inline fun AnimatedVectorDrawable.doOnAnimationEvent(
    crossinline onAnimationStart: (drawable: Drawable) -> Unit = { _ -> },
    crossinline onAnimationEnd: (drawable: Drawable) -> Unit = { _ -> }
) {
    registerAnimationCallback(object : Animatable2.AnimationCallback() {
        override fun onAnimationStart(drawable: Drawable) {
            onAnimationStart.invoke(drawable)
            super.onAnimationStart(drawable)
        }
        override fun onAnimationEnd(drawable: Drawable) {
            onAnimationEnd.invoke(drawable)
            super.onAnimationEnd(drawable)
        }
    })
}

inline fun AnimatedVectorDrawable.doOnAnimationStart(crossinline listener: (drawable: Drawable) -> Unit = { _ -> }) = doOnAnimationEvent(onAnimationStart = listener)
inline fun AnimatedVectorDrawable.doOnAnimationEnd(crossinline listener: (drawable: Drawable) -> Unit = { _ -> }) = doOnAnimationEvent(onAnimationEnd = listener)