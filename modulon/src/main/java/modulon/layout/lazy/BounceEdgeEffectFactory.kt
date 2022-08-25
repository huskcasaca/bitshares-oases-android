package modulon.layout.lazy

import android.graphics.Canvas
import android.widget.EdgeEffect
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/** The magnitude of translation distance while the list is over-scrolled. */
private const val OVERSCROLL_TRANSLATION_MAGNITUDE = 0.5f

/** The magnitude of translation distance when the list reaches the edge on fling. */
private const val FLING_TRANSLATION_MAGNITUDE = 0.5f

class BounceEdgeEffectFactory(private val parentDirection: Int) : RecyclerView.EdgeEffectFactory() {

    override fun createEdgeEffect(recyclerView: RecyclerView, direction: Int): EdgeEffect {

        return object : EdgeEffect(recyclerView.context) {

            var translationY
                get() = recyclerView.translationY
                set(value) {
                    recyclerView.translationY = value
                }

            var translationX
                get() = recyclerView.translationX
                set(value) {
                    recyclerView.translationX = value
                }

            val height
                get() = recyclerView.height

            val width
                get() = recyclerView.width

            // A reference to the [SpringAnimation] for this RecyclerView used to bring the item back after the over-scroll effect.
            var animation: SpringAnimation? = null

            override fun onPull(deltaDistance: Float) {
                super.onPull(deltaDistance)
                handlePull(deltaDistance)
            }

            override fun onPull(deltaDistance: Float, displacement: Float) {
                super.onPull(deltaDistance, displacement)
                handlePull(deltaDistance)
            }

            private fun handlePull(deltaDistance: Float) {
                // This is called on every touch event while the list is scrolled with a finger.

                // Translate the recyclerView with the distance
                if (parentDirection == RecyclerView.VERTICAL) {
                    if (height == 0) {
                        translationY = 0f
                        return
                    }
                    val sign = if (direction == DIRECTION_BOTTOM) -1 else if (direction == DIRECTION_TOP) 1  else return
                    val percent = if (height == 0) 0f else 1 - abs(translationY / height).coerceIn(0f..1f)
                    val translationYDelta = percent * percent * sign * height * deltaDistance * OVERSCROLL_TRANSLATION_MAGNITUDE
                    translationY += translationYDelta
                } else {
                    if (width == 0) {
                        translationX = 0f
                        return
                    }
                    val sign = if (direction == DIRECTION_RIGHT) -1 else if (direction == DIRECTION_LEFT) 1 else return
                    val percent = if (width == 0) 0f else 1 - abs(translationX / width).coerceIn(0f..1f)
                    val translationYDelta = percent * percent * sign * width * deltaDistance * OVERSCROLL_TRANSLATION_MAGNITUDE
                    translationX += translationYDelta
                }

                animation?.cancel()
            }

            override fun onRelease() {
                super.onRelease()
                // The finger is lifted. Start the animation to bring translation back to the resting state.

                if (parentDirection == RecyclerView.VERTICAL) {
                    if (translationY != 0f) {
                        animation = createAnim().also { it.start() }
                    }
                } else {
                    if (translationX != 0f) {
                        animation = createAnim().also { it.start() }
                    }
                }
            }

            override fun onAbsorb(velocity: Int) {
                super.onAbsorb(velocity)

                // The list has reached the edge on fling.
                val sign = if (parentDirection == RecyclerView.VERTICAL) {
                    if (direction == DIRECTION_BOTTOM) -1 else 1
                } else {
                    if (direction == DIRECTION_RIGHT) -1 else 1
                }
                val translationVelocity = sign * velocity * FLING_TRANSLATION_MAGNITUDE
                animation?.cancel()
                animation = createAnim().setStartVelocity(translationVelocity)?.also { it.start() }
            }

            override fun draw(canvas: Canvas?): Boolean {
                // don't paint the usual edge effect
                return false
            }

            override fun isFinished(): Boolean {
                // Without this, will skip future calls to onAbsorb()
                return animation?.isRunning?.not() ?: true
            }

            private fun createAnim() = SpringAnimation(recyclerView, if (parentDirection == RecyclerView.VERTICAL) SpringAnimation.TRANSLATION_Y else SpringAnimation.TRANSLATION_X).apply {
                spring = SpringForce().apply {
                    finalPosition = 0f
                    dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
                    stiffness = SpringForce.STIFFNESS_MEDIUM
                }
            }

        }
    }
}