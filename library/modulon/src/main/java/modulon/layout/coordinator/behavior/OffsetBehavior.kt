package modulon.layout.coordinator.behavior

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat

open class OffsetBehavior<V : View> : CoordinatorLayout.Behavior<V>() {

    val topAndBottomOffset: Int
        get() = offsetHelper?.topAndBottomOffset ?: 0

    val leftAndRightOffset: Int
        get() = offsetHelper?.leftAndRightOffset ?: 0

    var isVerticalOffsetEnabled: Boolean
        get() = offsetHelper?.isVerticalOffsetEnabled ?: false
        set(verticalOffsetEnabled) {
            offsetHelper?.isVerticalOffsetEnabled = verticalOffsetEnabled
        }
    var isHorizontalOffsetEnabled: Boolean
        get() = offsetHelper?.isHorizontalOffsetEnabled ?: false
        set(horizontalOffsetEnabled) {
            offsetHelper?.isHorizontalOffsetEnabled = horizontalOffsetEnabled
        }

    private var offsetHelper: OffsetHelper? = null
    private var tempTopBottomOffset = 0
    private var tempLeftRightOffset = 0

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        // First let lay the child out
        layoutChild(parent, child, layoutDirection)
        if (offsetHelper == null) offsetHelper = OffsetHelper(child)
        offsetHelper?.onViewLayout()
        offsetHelper?.applyOffsets()
        if (tempTopBottomOffset != 0) {
            offsetHelper?.setTopAndBottomOffset(tempTopBottomOffset)
            tempTopBottomOffset = 0
        }
        if (tempLeftRightOffset != 0) {
            offsetHelper?.setLeftAndRightOffset(tempLeftRightOffset)
            tempLeftRightOffset = 0
        }
        return true
    }

    protected open fun layoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int) {
        // Let the parent lay it out by default
        parent.onLayoutChild(child, layoutDirection)
    }

    fun setTopAndBottomOffset(offset: Int): Boolean {
        // TODO: 27/12/2021 equals?
//        tempTopBottomOffset = if (offsetHelper != null) {
//            return offsetHelper!!.setTopAndBottomOffset(offset)
//        } else {
//            offset
//        }
//        return false
        return offsetHelper?.setTopAndBottomOffset(offset) ?: false.also { tempTopBottomOffset = offset }
    }

    fun setLeftAndRightOffset(offset: Int): Boolean {
//        tempLeftRightOffset = if (offsetHelper != null) {
//            return offsetHelper!!.setLeftAndRightOffset(offset)
//        } else {
//            offset
//        }
//        return false
        return offsetHelper?.setLeftAndRightOffset(offset) ?: false.also { tempTopBottomOffset = offset }
    }


    internal class OffsetHelper(private val view: View) {

        var layoutTop = 0
            private set
        var layoutLeft = 0
            private set
        var topAndBottomOffset = 0
            private set
        var leftAndRightOffset = 0
            private set
        var isVerticalOffsetEnabled = true
        var isHorizontalOffsetEnabled = true

        fun onViewLayout() {
            // Grab the original top and left
            layoutTop = view.top
            layoutLeft = view.left
        }

        fun applyOffsets() {
            ViewCompat.offsetTopAndBottom(view, topAndBottomOffset - (view.top - layoutTop))
            ViewCompat.offsetLeftAndRight(view, leftAndRightOffset - (view.left - layoutLeft))
        }

        /**
         * Set the top and bottom offset for this [OffsetHelper]'s view.
         *
         * @param offset the offset in px.
         * @return true if the offset has changed
         */
        fun setTopAndBottomOffset(offset: Int): Boolean {
            if (isVerticalOffsetEnabled && topAndBottomOffset != offset) {
                topAndBottomOffset = offset
                applyOffsets()
                return true
            }
            return false
        }

        /**
         * Set the left and right offset for this [OffsetHelper]'s view.
         *
         * @param offset the offset in px.
         * @return true if the offset has changed
         */
        fun setLeftAndRightOffset(offset: Int): Boolean {
            if (isHorizontalOffsetEnabled && leftAndRightOffset != offset) {
                leftAndRightOffset = offset
                applyOffsets()
                return true
            }
            return false
        }

    }

}
