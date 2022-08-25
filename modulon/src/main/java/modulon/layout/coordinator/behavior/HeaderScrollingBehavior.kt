package modulon.layout.coordinator.behavior

import android.annotation.SuppressLint
import android.graphics.Rect
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

abstract class HeaderScrollingBehavior : OffsetBehavior<View>() {

    companion object {
        private fun resolveGravity(gravity: Int): Int {
            return if (gravity == Gravity.NO_GRAVITY) GravityCompat.START or Gravity.TOP else gravity
        }
    }

    protected val tempRect1 = Rect()
    protected val tempRect2 = Rect()

    /**
     * The gap between the top of the scrolling view and the bottom of the header layout in pixels.
     */
    var verticalLayoutGap = 0
        private set

    var overlayTop = 0

    @SuppressLint("RestrictedApi")
    override fun onMeasureChild(parent: CoordinatorLayout, child: View, parentWidthMeasureSpec: Int, widthUsed: Int, parentHeightMeasureSpec: Int, heightUsed: Int): Boolean {
        val childLpHeight = child.layoutParams.height
        if (childLpHeight == ViewGroup.LayoutParams.MATCH_PARENT || childLpHeight == ViewGroup.LayoutParams.WRAP_CONTENT ) {
            // If the menu's height is set to match_parent/wrap_content then measure it
            // with the maximum visible height
            val dependencies = parent.getDependencies(child)
            val header = findFirstDependency(dependencies)
            if (header != null) {
                var availableHeight = View.MeasureSpec.getSize(parentHeightMeasureSpec)
                if (availableHeight > 0) {
                    if (ViewCompat.getFitsSystemWindows(header)) {
                        val parentInsets = parent.lastWindowInsets
                        if (parentInsets != null) {
//                            availableHeight += (parentInsets.systemWindowInsetTop + parentInsets.systemWindowInsetBottom)
                            if (WindowInsetsCompat(parentInsets).isVisible(WindowInsetsCompat.Type.ime())) {
                                availableHeight += (parentInsets.systemWindowInsetTop)
                            } else {
                                availableHeight += parentInsets.systemWindowInsetTop + parentInsets.systemWindowInsetBottom
                            }
                        }
                    }
                } else {
                    // If the measure spec doesn't specify a size, use the current height
                    availableHeight = parent.height
                }

                var height = availableHeight + getScrollRange(header)
                val headerHeight = header.measuredHeight
                if (shouldHeaderOverlapScrollingChild()) {
                    child.translationY = -headerHeight.toFloat()
                } else {
                    height -= headerHeight
                }
                val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                    height,
                    if (childLpHeight == ViewGroup.LayoutParams.MATCH_PARENT) View.MeasureSpec.EXACTLY else View.MeasureSpec.AT_MOST
                )

                // Now measure the scrolling view with the correct height
                parent.onMeasureChild(
                    child, parentWidthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed
                )
                return true
            }
        }
        return false
    }

    @SuppressLint("RestrictedApi")
    override fun layoutChild(parent: CoordinatorLayout, child: View, layoutDirection: Int) {
        val header = findFirstDependency(parent.getDependencies(child))
        if (header != null) {
            val lp = child.layoutParams as CoordinatorLayout.LayoutParams
            val available = tempRect1
            available.set(
                parent.paddingLeft + lp.leftMargin,
                header.bottom + lp.topMargin,
                parent.width - parent.paddingRight - lp.rightMargin,
                parent.height + header.bottom - parent.paddingBottom - lp.bottomMargin
            )
            val parentInsets = parent.lastWindowInsets
            if (parentInsets != null && ViewCompat.getFitsSystemWindows(parent)
                && !ViewCompat.getFitsSystemWindows(child)
            ) {
                // If we're set to handle insets but this child isn't, then it has been measured as
                // if there are no insets. We need to lay it out to match horizontally.
                // Top and bottom and already handled in the logic above
                available.left += parentInsets.systemWindowInsetLeft
                available.right -= parentInsets.systemWindowInsetRight
            }
            val out = tempRect2
            Gravity.apply(resolveGravity(lp.gravity), child.measuredWidth, child.measuredHeight, available, out, layoutDirection)
            val overlap = getOverlapPixelsForOffset(header)
            child.layout(out.left, out.top - overlap, out.right, out.bottom - overlap)
            verticalLayoutGap = out.top - header.bottom
        } else {
            // If we don't have a dependency, let super handle it
            super.layoutChild(parent, child, layoutDirection)
            verticalLayoutGap = 0
        }
    }

    private fun shouldHeaderOverlapScrollingChild(): Boolean {
        return false
    }

    open fun getOverlapPixelsForOffset(header: View): Int {
        return overlayTop
    }

    abstract fun findFirstDependency(views: List<View>): View?
    open fun getScrollRange(v: View): Int {
        return v.measuredHeight
    }
}


