package modulon.layout.coordinator.behavior

import android.animation.ValueAnimator
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import modulon.extensions.view.*
import modulon.extensions.view.isOnTop
import modulon.layout.actionbar.ActionBarLayout
import modulon.layout.recycler.RecyclerLayout
import kotlin.math.abs

class ActionBarBehavior(var scrollEnabled: Boolean = true) : HeaderBehavior<ActionBarLayout>() {

    private var lastStartedType = MotionEvent.ACTION_DOWN
    private var offsetAnimator: ValueAnimator? = null
    private val interpolator = DecelerateInterpolator()
    private val interpolatorCustom = AccelerateInterpolator()

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: ActionBarLayout, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        val started = scrollEnabled && (axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0)
        if (directTargetChild.translationY != child.translationY) directTargetChild.translationY = child.translationY
        if (started) offsetAnimator?.cancel()
        lastStartedType = type
        return started
    }

    private var lastDirection = 0

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: ActionBarLayout, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
//        if (!scrollEnabled || isLocked) return super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)

        if (dy == 0 && dx == 0) {
            animateToPosition(child, lastDirection >= 0)
            return super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        }
        lastDirection = dy
        if (target.translationY < 0f && dy < 0) {
            val old = target.translationY
            target.translationY = (target.translationY - dy).coerceAtMost(0f)
            consumed[1] = (old - target.translationY).toInt()
            return super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        }
        if ((dy > 0 && child.isOnTop) || (dy < 0 && (child.isOnBottom || !(target is RecyclerLayout && target.isOnTop)))) {
            return super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        }
        // TODO: 2022/2/16 move to recycler behavior
        if (target.translationY > 0f && dy > 0) {
            val old = target.translationY
            target.translationY = (target.translationY - dy).coerceAtLeast(0f)
            consumed[1] = (old - target.translationY).toInt()
            return super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        }

        val old = child.translation
        val added = old + dy
        moveActionBar(child, added)

        consumed[1] = added.coerceIn(0..64.dp) - old

        return super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: ActionBarLayout, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int, consumed: IntArray) {
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: ActionBarLayout, target: View, type: Int) {
        if (type == MotionEvent.ACTION_UP || (lastStartedType == MotionEvent.ACTION_DOWN && type == MotionEvent.ACTION_DOWN)) animateToPosition(child, lastDirection >= 0)
        super.onStopNestedScroll(coordinatorLayout, child, target, type)
    }

    private fun animateToPosition(actionBar: ActionBarLayout, up: Boolean) {
        if (actionBar.translation != 0 && actionBar.translation != 64.dp) {
            offsetAnimator?.cancel()
            val targetValue = if (up) 64.dp else 0
            val span = abs(targetValue - actionBar.translation) / 64.dpf
            ValueAnimator.ofInt(actionBar.translation, targetValue).apply {
                duration = (300f * span).toLong()
                interpolator = this@ActionBarBehavior.interpolator
                addUpdateListener {
                    val value = it.animatedValue as Int
                    if (offsetAnimator !== this) {
                        cancel()
                        return@addUpdateListener
                    }
                    moveActionBar(actionBar, value)
                }
                offsetAnimator = this
                start()
            }
        }
    }


    fun animateToPositionLock(actionBar: ActionBarLayout, up: Boolean) {
        scrollEnabled = !up
        offsetAnimator?.cancel()
        val targetValue = if (up) 64.dp else 0
        val span = abs(targetValue - actionBar.translation) / 64.dpf
        ValueAnimator.ofInt(actionBar.translation, targetValue).apply {
            duration = (300f * span).toLong()
            interpolator = this@ActionBarBehavior.interpolatorCustom

            addUpdateListener {
                val value = it.animatedValue as Int
                if (offsetAnimator !== this) {
                    cancel()
                    return@addUpdateListener
                }
                moveActionBar(actionBar, value)
            }
            offsetAnimator = this
            start()
        }
    }

    private fun moveActionBar(actionBar: ActionBarLayout, translation: Int) {
        actionBar.translation = translation
    }

}