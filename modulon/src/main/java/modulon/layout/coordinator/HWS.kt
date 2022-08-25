package modulon.layout.coordinator

import android.annotation.SuppressLint
import android.graphics.Rect
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.math.MathUtils
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat


//open class BaseBehavior<T : AppBarLayout?> : com.google.android.material.appbar.HeaderBehavior<T> {
//    /** Callback to allow control over any [AppBarLayout] dragging.  */ // TODO(b/76413401): remove this base class and generic type after the widget migration
//    abstract class BaseDragCallback<T : AppBarLayout?>() {
//        /**
//         * Allows control over whether the given [AppBarLayout] can be dragged or not.
//         *
//         *
//         * Dragging is defined as a direct touch on the AppBarLayout with movement. This call does
//         * not affect any nested scrolling.
//         *
//         * @return true if we are in a position to scroll the AppBarLayout via a drag, false if not.
//         */
//        abstract fun canDrag(appBarLayout: T): Boolean
//    }
//
//    private var offsetDelta = 0
//
//    @ViewCompat.NestedScrollType
//    private var lastStartedType = 0
//    private var offsetAnimator: ValueAnimator? = null
//    private var savedState: SavedState? = null
//    private var lastNestedScrollingChildRef: WeakReference<View>? = null
//    private var onDragCallback: BaseDragCallback<*>? = null
//
//    constructor() {}
//    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
//
//    override fun onStartNestedScroll(
//        parent: CoordinatorLayout,
//        child: T,
//        directTargetChild: View,
//        target: View,
//        nestedScrollAxes: Int,
//        type: Int,
//    ): Boolean {
//        // Return true if we're nested scrolling vertically, and we either have lift on scroll enabled
//        // or we can scroll the children.
//        val started = (nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
//                && (child.isLiftOnScroll() || canScrollChildren(parent, child, directTargetChild)))
//        if (started && offsetAnimator != null) {
//            // Cancel any offset animation
//            offsetAnimator!!.cancel()
//        }
//
//        // A new nested scroll has started so clear out the previous ref
//        lastNestedScrollingChildRef = null
//
//        // Track the last started type so we know if a fling is about to happen once scrolling ends
//        lastStartedType = type
//        return started
//    }
//
//    // Return true if there are scrollable children and the scrolling view is big enough to scroll.
//    private fun canScrollChildren(
//        parent: CoordinatorLayout, child: T, directTargetChild: View,
//    ): Boolean {
//        return (child.hasScrollableChildren()
//                && parent.height - directTargetChild.height <= child.getHeight())
//    }
//
//    override fun onNestedPreScroll(
//        coordinatorLayout: CoordinatorLayout,
//        child: T,
//        target: View,
//        dx: Int,
//        dy: Int,
//        consumed: IntArray,
//        type: Int,
//    ) {
//        if (dy != 0) {
//            val min: Int
//            val max: Int
//            if (dy < 0) {
//                // We're scrolling down
//                min = -child.getTotalScrollRange()
//                max = min + child.getDownNestedPreScrollRange()
//            } else {
//                // We're scrolling up
//                min = -child.getUpNestedPreScrollRange()
//                max = 0
//            }
//            if (min != max) {
//                consumed[1] = scroll(coordinatorLayout, child, dy, min, max)
//            }
//        }
//        if (child.isLiftOnScroll()) {
//            child.setLiftedState(child.shouldLift(target))
//        }
//    }
//
//    override fun onNestedScroll(
//        coordinatorLayout: CoordinatorLayout,
//        child: T,
//        target: View,
//        dxConsumed: Int,
//        dyConsumed: Int,
//        dxUnconsumed: Int,
//        dyUnconsumed: Int,
//        type: Int,
//        consumed: IntArray,
//    ) {
//        if (dyUnconsumed < 0) {
//            // If the scrolling view is scrolling down but not consuming, it's probably be at
//            // the top of it's content
//            consumed[1] = scroll(coordinatorLayout, child, dyUnconsumed, -child.getDownNestedScrollRange(), 0)
//        }
//        if (dyUnconsumed == 0) {
//            // The scrolling view may scroll to the top of its content without updating the actions, so
//            // update here.
//            updateAccessibilityActions(coordinatorLayout, child)
//        }
//    }
//
//    override fun onStopNestedScroll(
//        coordinatorLayout: CoordinatorLayout, abl: T, target: View, type: Int,
//    ) {
//        // onStartNestedScroll for a fling will happen before onStopNestedScroll for the scroll. This
//        // isn't necessarily guaranteed yet, but it should be in the future. We use this to our
//        // advantage to check if a fling (ViewCompat.TYPE_NON_TOUCH) will start after the touch scroll
//        // (ViewCompat.TYPE_TOUCH) ends
//        if (lastStartedType == ViewCompat.TYPE_TOUCH || type == ViewCompat.TYPE_NON_TOUCH) {
//            // If we haven't been flung, or a fling is ending
//            snapToChildIfNeeded(coordinatorLayout, abl)
//            if (abl.isLiftOnScroll()) {
//                abl.setLiftedState(abl.shouldLift(target))
//            }
//        }
//
//        // Keep a reference to the previous nested scrolling child
//        lastNestedScrollingChildRef = WeakReference(target)
//    }
//
//    /**
//     * Set a callback to control any [AppBarLayout] dragging.
//     *
//     * @param callback the callback to use, or `null` to use the default behavior.
//     */
//    fun setDragCallback(callback: BaseDragCallback<*>?) {
//        onDragCallback = callback
//    }
//
//    private fun animateOffsetTo(
//        coordinatorLayout: CoordinatorLayout,
//        child: T,
//        offset: Int,
//        velocity: Float,
//    ) {
//        var velocity = velocity
//        val distance = Math.abs(topBottomOffsetForScrollingSibling - offset)
//        val duration: Int
//        velocity = Math.abs(velocity)
//        if (velocity > 0) {
//            duration = 3 * Math.round(1000 * (distance / velocity))
//        } else {
//            val distanceRatio: Float = distance.toFloat() / child.getHeight()
//            duration = ((distanceRatio + 1) * 150).toInt()
//        }
//        animateOffsetWithDuration(coordinatorLayout, child, offset, duration)
//    }
//
//    private fun animateOffsetWithDuration(
//        coordinatorLayout: CoordinatorLayout,
//        child: T,
//        offset: Int,
//        duration: Int,
//    ) {
//        val currentOffset = topBottomOffsetForScrollingSibling
//        if (currentOffset == offset) {
//            if (offsetAnimator != null && offsetAnimator!!.isRunning) {
//                offsetAnimator!!.cancel()
//            }
//            return
//        }
//        if (offsetAnimator == null) {
//            offsetAnimator = ValueAnimator()
//            offsetAnimator!!.interpolator = com.google.android.material.animation.AnimationUtils.DECELERATE_INTERPOLATOR
//            offsetAnimator!!.addUpdateListener { animator ->
//                setHeaderTopBottomOffset(
//                    coordinatorLayout, child, animator.animatedValue as Int)
//            }
//        } else {
//            offsetAnimator!!.cancel()
//        }
//        offsetAnimator!!.duration = Math.min(duration, MAX_OFFSET_ANIMATION_DURATION).toLong()
//        offsetAnimator!!.setIntValues(currentOffset, offset)
//        offsetAnimator!!.start()
//    }
//
//    private fun getChildIndexOnOffset(abl: T, offset: Int): Int {
//        var i = 0
//        val count: Int = abl.getChildCount()
//        while (i < count) {
//            val child: View = abl.getChildAt(i)
//            var top = child.top
//            var bottom = child.bottom
//            val lp: com.google.android.material.appbar.AppBarLayout.LayoutParams =
//                child.layoutParams as com.google.android.material.appbar.AppBarLayout.LayoutParams
//            if (checkFlag(lp.getScrollFlags(), AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP_MARGINS)) {
//                // Update top and bottom to include margins
//                top -= lp.topMargin
//                bottom += lp.bottomMargin
//            }
//            if (top <= -offset && bottom >= -offset) {
//                return i
//            }
//            i++
//        }
//        return -1
//    }
//
//    private fun snapToChildIfNeeded(coordinatorLayout: CoordinatorLayout, abl: T) {
//        val topInset: Int = abl.getTopInset() + abl.getPaddingTop()
//        // The "baseline" of scrolling is the top of the first child. We "add" insets and paddings
//        // to the scrolling amount to align offsets and views with the same y-coordinate. (The origin
//        // is at the top of the AppBarLayout, so all the coordinates are with negative values.)
//        val offset = topBottomOffsetForScrollingSibling - topInset
//        val offsetChildIndex = getChildIndexOnOffset(abl, offset)
//        if (offsetChildIndex >= 0) {
//            val offsetChild: View = abl.getChildAt(offsetChildIndex)
//            val lp: com.google.android.material.appbar.AppBarLayout.LayoutParams =
//                offsetChild.layoutParams as com.google.android.material.appbar.AppBarLayout.LayoutParams
//            val flags: Int = lp.getScrollFlags()
//            if (flags and AppBarLayout.LayoutParams.FLAG_SNAP == AppBarLayout.LayoutParams.FLAG_SNAP) {
//                // We're set the snap, so animate the offset to the nearest edge
//                var snapTop = -offsetChild.top
//                var snapBottom = -offsetChild.bottom
//
//                // If the child is set to fit system windows, its top will include the inset area, we need
//                // to minus the inset from snapTop to make the calculation consistent.
//                if (offsetChildIndex == 0 && ViewCompat.getFitsSystemWindows(abl)
//                    && ViewCompat.getFitsSystemWindows(offsetChild)
//                ) {
//                    snapTop -= abl.getTopInset()
//                }
//                if (checkFlag(flags, AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED)) {
//                    // If the view is set only exit until it is collapsed, we'll abide by that
//                    snapBottom += ViewCompat.getMinimumHeight(offsetChild)
//                } else if (checkFlag(
//                        flags, AppBarLayout.LayoutParams.FLAG_QUICK_RETURN or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)
//                ) {
//                    // If it's set to always enter collapsed, it actually has two states. We
//                    // select the state and then snap within the state
//                    val seam = snapBottom + ViewCompat.getMinimumHeight(offsetChild)
//                    if (offset < seam) {
//                        snapTop = seam
//                    } else {
//                        snapBottom = seam
//                    }
//                }
//                if (checkFlag(flags, AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP_MARGINS)) {
//                    // Update snap destinations to include margins
//                    snapTop += lp.topMargin
//                    snapBottom -= lp.bottomMargin
//                }
//
//                // Excludes insets and paddings from the offset. (Offsets use the top of child views as
//                // the origin.)
//                val newOffset = calculateSnapOffset(offset, snapBottom, snapTop) + topInset
//                animateOffsetTo(
//                    coordinatorLayout, abl, MathUtils.clamp(newOffset, -abl.getTotalScrollRange(), 0), 0f)
//            }
//        }
//    }
//
//    private fun calculateSnapOffset(value: Int, bottom: Int, top: Int): Int {
//        return if (value < (bottom + top) / 2) bottom else top
//    }
//
//    override fun onMeasureChild(
//        parent: CoordinatorLayout,
//        child: T,
//        parentWidthMeasureSpec: Int,
//        widthUsed: Int,
//        parentHeightMeasureSpec: Int,
//        heightUsed: Int,
//    ): Boolean {
//        val lp = child.getLayoutParams() as CoordinatorLayout.LayoutParams
//        if (lp.height == CoordinatorLayout.LayoutParams.WRAP_CONTENT) {
//            // If the view is set to wrap on it's height, CoordinatorLayout by default will
//            // cap the view at the CoL's height. Since the AppBarLayout can scroll, this isn't
//            // what we actually want, so we measure it ourselves with an unspecified spec to
//            // allow the child to be larger than it's parent
//            parent.onMeasureChild(
//                child,
//                parentWidthMeasureSpec,
//                widthUsed,
//                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
//                heightUsed)
//            return true
//        }
//
//        // Let the parent handle it as normal
//        return super.onMeasureChild(
//            parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed)
//    }
//
//    override fun onLayoutChild(
//        parent: CoordinatorLayout, abl: T, layoutDirection: Int,
//    ): Boolean {
//        val handled: Boolean = super.onLayoutChild(parent, abl, layoutDirection)
//
//        // The priority for actions here is (first which is true wins):
//        // 1. forced pending actions
//        // 2. offsets for restorations
//        // 3. non-forced pending actions
//        val pendingAction: Int = abl.getPendingAction()
//        if (savedState != null && pendingAction and AppBarLayout.PENDING_ACTION_FORCE == 0) {
//            if (savedState!!.fullyScrolled) {
//                // Keep fully scrolled.
//                setHeaderTopBottomOffset(parent, abl, -abl.getTotalScrollRange())
//            } else if (savedState!!.fullyExpanded) {
//                // Keep fully expanded.
//                setHeaderTopBottomOffset(parent, abl, 0)
//            } else {
//                // Not fully scrolled, restore the visible percetage of child layout.
//                val child: View = abl.getChildAt(savedState!!.firstVisibleChildIndex)
//                var offset = -child.bottom
//                if (savedState!!.firstVisibleChildAtMinimumHeight) {
//                    offset += ViewCompat.getMinimumHeight(child) + abl.getTopInset()
//                } else {
//                    offset += Math.round(child.height * savedState!!.firstVisibleChildPercentageShown)
//                }
//                setHeaderTopBottomOffset(parent, abl, offset)
//            }
//        } else if (pendingAction != AppBarLayout.PENDING_ACTION_NONE) {
//            val animate = pendingAction and AppBarLayout.PENDING_ACTION_ANIMATE_ENABLED != 0
//            if (pendingAction and AppBarLayout.PENDING_ACTION_COLLAPSED != 0) {
//                val offset: Int = -abl.getUpNestedPreScrollRange()
//                if (animate) {
//                    animateOffsetTo(parent, abl, offset, 0f)
//                } else {
//                    setHeaderTopBottomOffset(parent, abl, offset)
//                }
//            } else if (pendingAction and AppBarLayout.PENDING_ACTION_EXPANDED != 0) {
//                if (animate) {
//                    animateOffsetTo(parent, abl, 0, 0f)
//                } else {
//                    setHeaderTopBottomOffset(parent, abl, 0)
//                }
//            }
//        }
//
//        // Finally reset any pending states
//        abl.resetPendingAction()
//        savedState = null
//
//        // We may have changed size, so let's constrain the top and bottom offset correctly,
//        // just in case we're out of the bounds
//        setTopAndBottomOffset(
//            MathUtils.clamp(getTopAndBottomOffset(), -abl.getTotalScrollRange(), 0))
//
//        // Update the AppBarLayout's drawable state for any elevation changes. This is needed so that
//        // the elevation is set in the first layout, so that we don't get a visual jump pre-N (due to
//        // the draw dispatch skip)
//        updateAppBarLayoutDrawableState(
//            parent, abl, getTopAndBottomOffset(), 0 /* direction */, true /* forceJump */)
//
//        // Make sure we dispatch the offset update
//        abl.onOffsetChanged(getTopAndBottomOffset())
//        updateAccessibilityActions(parent, abl)
//        return handled
//    }
//
//    private fun updateAccessibilityActions(
//        coordinatorLayout: CoordinatorLayout, appBarLayout: T,
//    ) {
//        ViewCompat.removeAccessibilityAction(coordinatorLayout,
//            AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_FORWARD.id)
//        ViewCompat.removeAccessibilityAction(coordinatorLayout,
//            AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_BACKWARD.id)
//        val scrollingView = findFirstScrollingChild(coordinatorLayout)
//        // Don't add a11y actions if there is no scrolling view that the abl depends on for scrolling
//        // or the abl has no scroll range.
//        if (scrollingView == null || appBarLayout.getTotalScrollRange() == 0) {
//            return
//        }
//        // Don't add actions if the scrolling view doesn't have the behavior that will cause the abl
//        // to scroll.
//        val lp = scrollingView.layoutParams as CoordinatorLayout.LayoutParams
//        if (lp.behavior !is com.google.android.material.appbar.AppBarLayout.ScrollingViewBehavior) {
//            return
//        }
//        addAccessibilityScrollActions(coordinatorLayout, appBarLayout, scrollingView)
//    }
//
//    private fun addAccessibilityScrollActions(
//        coordinatorLayout: CoordinatorLayout,
//        appBarLayout: T,
//        scrollingView: View,
//    ) {
//        if (topBottomOffsetForScrollingSibling != -appBarLayout.getTotalScrollRange()
//            && scrollingView.canScrollVertically(1)
//        ) {
//            // Add a collapsing action if the view can scroll up and the offset isn't the abl scroll
//            // range. (This offset means the view is completely collapsed). Collapse to minimum height.
//            addActionToExpand(coordinatorLayout,
//                appBarLayout,
//                AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_FORWARD,
//                false)
//        }
//        // Don't add an expanding action if the sibling offset is 0, which would mean the abl is
//        // completely expanded.
//        if (topBottomOffsetForScrollingSibling != 0) {
//            if (scrollingView.canScrollVertically(-1)) {
//                // Expanding action. If the view can scroll down, expand the app bar reflecting the logic
//                // in onNestedPreScroll.
//                val dy: Int = -appBarLayout.getDownNestedPreScrollRange()
//                // Offset by non-zero.
//                if (dy != 0) {
//                    ViewCompat.replaceAccessibilityAction(
//                        coordinatorLayout,
//                        AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_BACKWARD,
//                        null
//                    ) { view, arguments ->
//                        onNestedPreScroll(
//                            coordinatorLayout,
//                            appBarLayout,
//                            scrollingView,
//                            0,
//                            dy, intArrayOf(0, 0),
//                            ViewCompat.TYPE_NON_TOUCH)
//                        true
//                    }
//                }
//            } else {
//                // If the view can't scroll down, we are probably at the top of the scrolling content so
//                // expand completely.
//                addActionToExpand(coordinatorLayout,
//                    appBarLayout,
//                    AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_BACKWARD,
//                    true)
//            }
//        }
//    }
//
//    private fun addActionToExpand(
//        parent: CoordinatorLayout,
//        appBarLayout: T,
//        action: AccessibilityNodeInfoCompat.AccessibilityActionCompat,
//        expand: Boolean,
//    ) {
//        ViewCompat.replaceAccessibilityAction(
//            parent,
//            action,
//            null
//        ) { view, arguments ->
//            appBarLayout.setExpanded(expand)
//            true
//        }
//    }
//
//    override fun canDragView(view: T): Boolean {
//        if (onDragCallback != null) {
//            // If there is a drag callback set, it's in control
//            return onDragCallback!!.canDrag(view)
//        }
//
//        // Else we'll use the default behaviour of seeing if it can scroll down
//        if (lastNestedScrollingChildRef != null) {
//            // If we have a reference to a scrolling view, check it
//            val scrollingView = lastNestedScrollingChildRef!!.get()
//            return (scrollingView != null && scrollingView.isShown
//                    && !scrollingView.canScrollVertically(-1))
//        } else {
//            // Otherwise we assume that the scrolling view hasn't been scrolled and can drag.
//            return true
//        }
//    }
//
//    override fun onFlingFinished(parent: CoordinatorLayout, layout: T) {
//        // At the end of a manual fling, check to see if we need to snap to the edge-child
//        snapToChildIfNeeded(parent, layout)
//        if (layout.isLiftOnScroll()) {
//            layout.setLiftedState(layout.shouldLift(findFirstScrollingChild(parent)))
//        }
//    }
//
//    override fun getMaxDragOffset(view: T): Int {
//        return -view.getDownNestedScrollRange()
//    }
//
//    override fun getScrollRangeForDragFling(view: T): Int {
//        return view.getTotalScrollRange()
//    }
//
//    override fun setHeaderTopBottomOffset(
//        coordinatorLayout: CoordinatorLayout,
//        appBarLayout: T,
//        newOffset: Int,
//        minOffset: Int,
//        maxOffset: Int,
//    ): Int {
//        var newOffset = newOffset
//        val curOffset = topBottomOffsetForScrollingSibling
//        var consumed = 0
//        if (minOffset != 0 && curOffset >= minOffset && curOffset <= maxOffset) {
//            // If we have some scrolling range, and we're currently within the min and max
//            // offsets, calculate a new offset
//            newOffset = MathUtils.clamp(newOffset, minOffset, maxOffset)
//            if (curOffset != newOffset) {
//                val interpolatedOffset =
//                    if (appBarLayout.hasChildWithInterpolator()) interpolateOffset(appBarLayout, newOffset) else newOffset
//                val offsetChanged: Boolean = setTopAndBottomOffset(interpolatedOffset)
//
//                // Update how much dy we have consumed
//                consumed = curOffset - newOffset
//                // Update the stored sibling offset
//                offsetDelta = newOffset - interpolatedOffset
//                if (offsetChanged) {
//                    // If the offset has changed, pass the change to any child scroll effect.
//                    for (i in 0 until appBarLayout.getChildCount()) {
//                        val params: com.google.android.material.appbar.AppBarLayout.LayoutParams =
//                            appBarLayout.getChildAt(i).getLayoutParams() as com.google.android.material.appbar.AppBarLayout.LayoutParams
//                        val scrollEffect: ChildScrollEffect = params.getScrollEffect()
//                        if (scrollEffect != null
//                            && params.getScrollFlags() and AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL != 0
//                        ) {
//                            scrollEffect.onOffsetChanged(
//                                appBarLayout, appBarLayout.getChildAt(i), getTopAndBottomOffset().toFloat())
//                        }
//                    }
//                }
//                if (!offsetChanged && appBarLayout.hasChildWithInterpolator()) {
//                    // If the offset hasn't changed and we're using an interpolated scroll
//                    // then we need to keep any dependent views updated. CoL will do this for
//                    // us when we move, but we need to do it manually when we don't (as an
//                    // interpolated scroll may finish early).
//                    coordinatorLayout.dispatchDependentViewsChanged(appBarLayout)
//                }
//
//                // Dispatch the updates to any listeners
//                appBarLayout.onOffsetChanged(getTopAndBottomOffset())
//
//                // Update the AppBarLayout's drawable state (for any elevation changes)
//                updateAppBarLayoutDrawableState(
//                    coordinatorLayout,
//                    appBarLayout,
//                    newOffset,
//                    if (newOffset < curOffset) -1 else 1,
//                    false /* forceJump */)
//            }
//        } else {
//            // Reset the offset delta
//            offsetDelta = 0
//        }
//        updateAccessibilityActions(coordinatorLayout, appBarLayout)
//        return consumed
//    }
//
//    @get:VisibleForTesting
//    val isOffsetAnimatorRunning: Boolean
//        get() = offsetAnimator != null && offsetAnimator!!.isRunning
//
//    private fun interpolateOffset(layout: T, offset: Int): Int {
//        val absOffset = Math.abs(offset)
//        var i = 0
//        val z: Int = layout.getChildCount()
//        while (i < z) {
//            val child: View = layout.getChildAt(i)
//            val childLp: com.google.android.material.appbar.AppBarLayout.LayoutParams =
//                child.layoutParams as com.google.android.material.appbar.AppBarLayout.LayoutParams
//            val interpolator: Interpolator = childLp.getScrollInterpolator()
//            if (absOffset >= child.top && absOffset <= child.bottom) {
//                if (interpolator != null) {
//                    var childScrollableHeight = 0
//                    val flags: Int = childLp.getScrollFlags()
//                    if (flags and AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL != 0) {
//                        // We're set to scroll so add the child's height plus margin
//                        childScrollableHeight += child.height + childLp.topMargin + childLp.bottomMargin
//                        if (flags and AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED != 0) {
//                            // For a collapsing scroll, we to take the collapsed height
//                            // into account.
//                            childScrollableHeight -= ViewCompat.getMinimumHeight(child)
//                        }
//                    }
//                    if (ViewCompat.getFitsSystemWindows(child)) {
//                        childScrollableHeight -= layout.getTopInset()
//                    }
//                    if (childScrollableHeight > 0) {
//                        val offsetForView = absOffset - child.top
//                        val interpolatedDiff = Math.round(childScrollableHeight
//                                * interpolator.getInterpolation(
//                            offsetForView / childScrollableHeight.toFloat()))
//                        return Integer.signum(offset) * (child.top + interpolatedDiff)
//                    }
//                }
//
//                // If we get to here then the view on the offset isn't suitable for interpolated
//                // scrolling. So break out of the loop
//                break
//            }
//            i++
//        }
//        return offset
//    }
//
//    private fun updateAppBarLayoutDrawableState(
//        parent: CoordinatorLayout,
//        layout: T,
//        offset: Int,
//        direction: Int,
//        forceJump: Boolean,
//    ) {
//        val child = getAppBarChildOnOffset(layout, offset)
//        var lifted = false
//        if (child != null) {
//            val childLp: com.google.android.material.appbar.AppBarLayout.LayoutParams =
//                child.layoutParams as com.google.android.material.appbar.AppBarLayout.LayoutParams
//            val flags: Int = childLp.getScrollFlags()
//            if ((flags and AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL) != 0) {
//                val minHeight = ViewCompat.getMinimumHeight(child)
//                if ((direction > 0
//                            && (flags
//                            and (AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
//                            or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED))
//                            != 0)
//                ) {
//                    // We're set to enter always collapsed so we are only collapsed when
//                    // being scrolled down, and in a collapsed offset
//                    lifted = -offset >= child.bottom - minHeight - layout.getTopInset()
//                } else if ((flags and AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED) != 0) {
//                    // We're set to exit until collapsed, so any offset which results in
//                    // the minimum height (or less) being shown is collapsed
//                    lifted = -offset >= child.bottom - minHeight - layout.getTopInset()
//                }
//            }
//        }
//        if (layout.isLiftOnScroll()) {
//            // Use first scrolling child as default scrolling view for updating lifted state because
//            // it represents the content that would be scrolled beneath the app bar.
//            lifted = layout.shouldLift(findFirstScrollingChild(parent))
//        }
//        val changed: Boolean = layout.setLiftedState(lifted)
//        if (forceJump || (changed && shouldJumpElevationState(parent, layout))) {
//            // If the collapsed state changed, we may need to
//            // jump to the current state if we have an overlapping view
//            layout.jumpDrawablesToCurrentState()
//        }
//    }
//
//    private fun shouldJumpElevationState(parent: CoordinatorLayout, layout: T): Boolean {
//        // We should jump the elevated state if we have a dependent scrolling view which has
//        // an overlapping top (i.e. overlaps us)
//        val dependencies = parent.getDependents(layout)
//        var i = 0
//        val size = dependencies.size
//        while (i < size) {
//            val dependency = dependencies[i]
//            val lp = dependency.layoutParams as CoordinatorLayout.LayoutParams
//            val behavior = lp.behavior
//            if (behavior is com.google.android.material.appbar.AppBarLayout.ScrollingViewBehavior) {
//                return (behavior as com.google.android.material.appbar.AppBarLayout.ScrollingViewBehavior?).getOverlayTop() != 0
//            }
//            i++
//        }
//        return false
//    }
//
//    private fun findFirstScrollingChild(parent: CoordinatorLayout): View? {
//        var i = 0
//        val z = parent.childCount
//        while (i < z) {
//            val child = parent.getChildAt(i)
//            if ((child is NestedScrollingChild
//                        || child is ListView
//                        || child is ScrollView)
//            ) {
//                return child
//            }
//            i++
//        }
//        return null
//    }
//
//    val topBottomOffsetForScrollingSibling: Int
//        get() = getTopAndBottomOffset() + offsetDelta
//
//    override fun onSaveInstanceState(parent: CoordinatorLayout, abl: T): Parcelable? {
//        val superState: Parcelable = super.onSaveInstanceState(parent, abl)
//        val scrollState = saveScrollState(superState, abl)
//        return scrollState ?: superState
//    }
//
//    override fun onRestoreInstanceState(
//        parent: CoordinatorLayout, appBarLayout: T, state: Parcelable,
//    ) {
//        if (state is SavedState) {
//            restoreScrollState(state, true)
//            super.onRestoreInstanceState(parent, appBarLayout, savedState!!.superState)
//        } else {
//            super.onRestoreInstanceState(parent, appBarLayout, state)
//            savedState = null
//        }
//    }
//
//    fun saveScrollState(superState: Parcelable?, abl: T): SavedState? {
//        val offset: Int = getTopAndBottomOffset()
//
//        // Try and find the first visible child...
//        var i = 0
//        val count: Int = abl.getChildCount()
//        while (i < count) {
//            val child: View = abl.getChildAt(i)
//            val visBottom = child.bottom + offset
//            if (child.top + offset <= 0 && visBottom >= 0) {
//                val ss = SavedState(superState ?: AbsSavedState.EMPTY_STATE)
//                ss.fullyExpanded = offset == 0
//                ss.fullyScrolled = !ss.fullyExpanded && -offset >= abl.getTotalScrollRange()
//                ss.firstVisibleChildIndex = i
//                ss.firstVisibleChildAtMinimumHeight = visBottom == (ViewCompat.getMinimumHeight(child) + abl.getTopInset())
//                ss.firstVisibleChildPercentageShown = visBottom / child.height.toFloat()
//                return ss
//            }
//            i++
//        }
//        return null
//    }
//
//    fun restoreScrollState(state: SavedState?, force: Boolean) {
//        if (savedState == null || force) {
//            savedState = state
//        }
//    }
//
//    /** A [Parcelable] implementation for [AppBarLayout].  */
//    class SavedState : AbsSavedState {
//        var fullyScrolled = false
//        var fullyExpanded = false
//        var firstVisibleChildIndex = 0
//        var firstVisibleChildPercentageShown = 0f
//        var firstVisibleChildAtMinimumHeight = false
//
//        constructor(source: Parcel, loader: ClassLoader?) : super(source, loader) {
//            fullyScrolled = source.readByte().toInt() != 0
//            fullyExpanded = source.readByte().toInt() != 0
//            firstVisibleChildIndex = source.readInt()
//            firstVisibleChildPercentageShown = source.readFloat()
//            firstVisibleChildAtMinimumHeight = source.readByte().toInt() != 0
//        }
//
//        constructor(superState: Parcelable?) : super((superState)!!) {}
//
//        override fun writeToParcel(dest: Parcel, flags: Int) {
//            super.writeToParcel(dest, flags)
//            dest.writeByte((if (fullyScrolled) 1 else 0).toByte())
//            dest.writeByte((if (fullyExpanded) 1 else 0).toByte())
//            dest.writeInt(firstVisibleChildIndex)
//            dest.writeFloat(firstVisibleChildPercentageShown)
//            dest.writeByte((if (firstVisibleChildAtMinimumHeight) 1 else 0).toByte())
//        }
//
//        companion object {
//            val CREATOR: Parcelable.Creator<SavedState> = object : ClassLoaderCreator<SavedState> {
//                override fun createFromParcel(source: Parcel, loader: ClassLoader): SavedState {
//                    return SavedState(source, loader)
//                }
//
//                override fun createFromParcel(source: Parcel): SavedState? {
//                    return SavedState(source, null)
//                }
//
//                override fun newArray(size: Int): Array<SavedState> {
//                    return arrayOfNulls(size)
//                }
//            }
//        }
//    }
//
//    companion object {
//        private val MAX_OFFSET_ANIMATION_DURATION = 600 // ms
//        private fun checkFlag(flags: Int, check: Int): Boolean {
//            return (flags and check) == check
//        }
//
//        private fun getAppBarChildOnOffset(
//            layout: AppBarLayout, offset: Int,
//        ): View? {
//            val absOffset = Math.abs(offset)
//            var i = 0
//            val z: Int = layout.getChildCount()
//            while (i < z) {
//                val child: View = layout.getChildAt(i)
//                if (absOffset >= child.top && absOffset <= child.bottom) {
//                    return child
//                }
//                i++
//            }
//            return null
//        }
//    }
//}
//
//
//
///**
// * Behavior which should be used by [View]s which can scroll vertically and support nested
// * scrolling to automatically scroll any [AppBarLayout] siblings.
// */
//class ScrollingViewBehavior : HeaderScrollingViewBehavior() {
//
//
//    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
//        // We depend on any AppBarLayouts
//        return dependency is ActionBarLayout
//    }
//
//    override fun onDependentViewChanged(
//        parent: CoordinatorLayout, child: View, dependency: View,
//    ): Boolean {
//        offsetChildAsNeeded(child, dependency)
//        updateLiftedStateIfNeeded(child, dependency)
//        return false
//    }
//
//    override fun onDependentViewRemoved(
//        parent: CoordinatorLayout, child: View, dependency: View,
//    ) {
//        if (dependency is ActionBarLayout) {
//            ViewCompat.removeAccessibilityAction(parent, AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_FORWARD.id)
//            ViewCompat.removeAccessibilityAction(parent, AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_BACKWARD.id)
//        }
//    }
//
//    override fun onRequestChildRectangleOnScreen(
//        parent: CoordinatorLayout,
//        child: View,
//        rectangle: Rect,
//        immediate: Boolean,
//    ): Boolean {
//        val header: ActionBarLayout? = findFirstDependency(parent.getDependencies(child))
//        if (header != null) {
//            // Offset the rect by the child's left/top
//            rectangle.offset(child.left, child.top)
//            val parentRect: Rect = tempRect1
//            parentRect[0, 0, parent.width] = parent.height
//            if (!parentRect.contains(rectangle)) {
//                // If the rectangle can not be fully seen the visible bounds, collapse
//                // the AppBarLayout
//                header.setExpanded(false, !immediate)
//                return true
//            }
//        }
//        return false
//    }
//
//    private fun offsetChildAsNeeded(child: View, dependency: View) {
//        val behavior = (dependency.layoutParams as CoordinatorLayout.LayoutParams).behavior
//        if (behavior is BaseBehavior) {
//            // Offset the child, pinning it to the bottom the header-dependency, maintaining
//            // any vertical gap and overlap
//            val ablBehavior: BaseBehavior? =
//                behavior as BaseBehavior?
//            ViewCompat.offsetTopAndBottom(
//                child, (dependency.bottom - child.top
//                        + ablBehavior.offsetDelta
//                        + getVerticalLayoutGap())
//                        - getOverlapPixelsForOffset(dependency))
//        }
//    }
//
//    override fun getOverlapRatioForOffset(header: View): Float {
//        if (header is ActionBarLayout) {
//            val abl: ActionBarLayout = header as ActionBarLayout
//            val totalScrollRange: Int = abl.getTotalScrollRange()
//            val preScrollDown: Int = abl.getDownNestedPreScrollRange()
//            val offset = getAppBarLayoutOffset(abl)
//            if (preScrollDown != 0 && totalScrollRange + offset <= preScrollDown) {
//                // If we're in a pre-scroll down. Don't use the offset at all.
//                return 0
//            } else {
//                val availScrollRange = totalScrollRange - preScrollDown
//                if (availScrollRange != 0) {
//                    // Else we'll use a interpolated ratio of the overlap, depending on offset
//                    return 1f + offset / availScrollRange.toFloat()
//                }
//            }
//        }
//        return 0f
//    }
//
//    override fun findFirstDependency(views: List<View>): ActionBarLayout? {
//        var i = 0
//        val z = views.size
//        while (i < z) {
//            val view = views[i]
//            if (view is ActionBarLayout) {
//                return view as ActionBarLayout
//            }
//            i++
//        }
//        return null
//    }
//
//    override fun getScrollRange(v: View): Int {
//        return if (v is ActionBarLayout) {
//            (v as ActionBarLayout).getTotalScrollRange()
//        } else {
//            super.getScrollRange(v)
//        }
//    }
//
//    private fun updateLiftedStateIfNeeded(child: View, dependency: View) {
//        if (dependency is ActionBarLayout) {
//            val appBarLayout: ActionBarLayout = dependency as ActionBarLayout
//            if (appBarLayout.isLiftOnScroll()) {
//                appBarLayout.setLiftedState(appBarLayout.shouldLift(child))
//            }
//        }
//    }
//
//    companion object {
//        private fun getAppBarLayoutOffset(abl: ActionBarLayout): Int {
//            val behavior = (abl.getLayoutParams() as CoordinatorLayout.LayoutParams).behavior
//            return if (behavior is BaseBehavior) {
//                (behavior as BaseBehavior?).getTopBottomOffsetForScrollingSibling()
//            } else 0
//        }
//    }
//}


@SuppressLint("RestrictedApi")
abstract class HeaderScrollingViewBehavior : ViewOffsetBehavior<View>() {

    companion object {
        private fun resolveGravity(gravity: Int): Int {
            return if (gravity == Gravity.NO_GRAVITY) GravityCompat.START or Gravity.TOP else gravity
        }
    }

    val tempRect1 = Rect()
    val tempRect2 = Rect()

    /**
     * The gap between the top of the scrolling view and the bottom of the header layout in pixels.
     */
    var verticalLayoutGap = 0
        private set
    /**
     * Returns the distance that this view should overlap any [ ].
     */
    /**
     * Set the distance that this view should overlap any [ ].
     *
     * @param overlayTop the distance in px
     */
    var overlayTop = 0

    override fun onMeasureChild(
        parent: CoordinatorLayout,
        child: View,
        parentWidthMeasureSpec: Int,
        widthUsed: Int,
        parentHeightMeasureSpec: Int,
        heightUsed: Int,
    ): Boolean {
        val childLpHeight = child.layoutParams.height
        if (childLpHeight == ViewGroup.LayoutParams.MATCH_PARENT
            || childLpHeight == ViewGroup.LayoutParams.WRAP_CONTENT
        ) {
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
                            availableHeight += (parentInsets.systemWindowInsetTop
                                    + parentInsets.systemWindowInsetBottom)
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
                    if (childLpHeight == ViewGroup.LayoutParams.MATCH_PARENT) View.MeasureSpec.EXACTLY else View.MeasureSpec.AT_MOST)

                // Now measure the scrolling view with the correct height
                parent.onMeasureChild(
                    child, parentWidthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed)
                return true
            }
        }
        return false
    }

    protected override fun layoutChild(
        parent: CoordinatorLayout,
        child: View,
        layoutDirection: Int,
    ) {
        val dependencies = parent.getDependencies(child)
        val header = findFirstDependency(dependencies)
        if (header != null) {
            val lp = child.layoutParams as CoordinatorLayout.LayoutParams
            val available = tempRect1
            available[parent.paddingLeft + lp.leftMargin, header.bottom + lp.topMargin, parent.width - parent.paddingRight - lp.rightMargin] =
                parent.height + header.bottom - parent.paddingBottom - lp.bottomMargin
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
            GravityCompat.apply(
                resolveGravity(lp.gravity),
                child.measuredWidth,
                child.measuredHeight,
                available,
                out,
                layoutDirection)
            val overlap = getOverlapPixelsForOffset(header)
            child.layout(out.left, out.top - overlap, out.right, out.bottom - overlap)
            verticalLayoutGap = out.top - header.bottom
        } else {
            // If we don't have a dependency, let super handle it
            super.layoutChild(parent, child, layoutDirection)
            verticalLayoutGap = 0
        }
    }

    protected fun shouldHeaderOverlapScrollingChild(): Boolean {
        return false
    }

    open fun getOverlapRatioForOffset(header: View): Float {
        return 1f
    }

    fun getOverlapPixelsForOffset(header: View): Int {
        return if (overlayTop == 0) 0 else MathUtils.clamp((getOverlapRatioForOffset(header) * overlayTop).toInt(), 0, overlayTop)
    }

    abstract fun findFirstDependency(views: List<View>): View?
    open fun getScrollRange(v: View): Int {
        return v.measuredHeight
    }

}


/** Behavior will automatically sets up a [ViewOffsetHelper] on a [View].  */
open class ViewOffsetBehavior<V : View> : CoordinatorLayout.Behavior<V>() {
    private var viewOffsetHelper: ViewOffsetHelper? = null
    private var tempTopBottomOffset = 0
    private var tempLeftRightOffset = 0

    override fun onLayoutChild(
        parent: CoordinatorLayout, child: V, layoutDirection: Int,
    ): Boolean {
        // First let lay the child out
        layoutChild(parent, child, layoutDirection)
        if (viewOffsetHelper == null) {
            viewOffsetHelper = ViewOffsetHelper(child)
        }
        viewOffsetHelper?.onViewLayout()
        viewOffsetHelper?.applyOffsets()
        if (tempTopBottomOffset != 0) {
            viewOffsetHelper?.setTopAndBottomOffset(tempTopBottomOffset)
            tempTopBottomOffset = 0
        }
        if (tempLeftRightOffset != 0) {
            viewOffsetHelper?.setLeftAndRightOffset(tempLeftRightOffset)
            tempLeftRightOffset = 0
        }
        return true
    }

    protected open fun layoutChild(
        parent: CoordinatorLayout, child: V, layoutDirection: Int,
    ) {
        // Let the parent lay it out by default
        parent.onLayoutChild(child, layoutDirection)
    }

    fun setTopAndBottomOffset(offset: Int): Boolean {
        if (viewOffsetHelper != null) {
            return viewOffsetHelper!!.setTopAndBottomOffset(offset)
        }
        tempTopBottomOffset = offset
        return false
    }

    fun setLeftAndRightOffset(offset: Int): Boolean {
        if (viewOffsetHelper != null) {
            return viewOffsetHelper!!.setLeftAndRightOffset(offset)
        }
        tempLeftRightOffset = offset
        return false
    }

    val topAndBottomOffset: Int
        get() = viewOffsetHelper?.topAndBottomOffset ?: 0
    val leftAndRightOffset: Int
        get() = viewOffsetHelper?.leftAndRightOffset ?: 0
    var isVerticalOffsetEnabled: Boolean
        get() = viewOffsetHelper?.isVerticalOffsetEnabled == true
        set(verticalOffsetEnabled) {
            viewOffsetHelper?.isVerticalOffsetEnabled = verticalOffsetEnabled
        }
    var isHorizontalOffsetEnabled: Boolean
        get() = viewOffsetHelper?.isHorizontalOffsetEnabled == true
        set(horizontalOffsetEnabled) {
            viewOffsetHelper?.isHorizontalOffsetEnabled = horizontalOffsetEnabled
        }
}


/**
 * Utility helper for moving a [View] around using [View.offsetLeftAndRight] and
 * [View.offsetTopAndBottom].
 *
 *
 * Also the setting of absolute offsets (similar to translationX/Y), rather than additive
 * offsets.
 */
internal class ViewOffsetHelper(private val view: View) {
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
     * Set the top and bottom offset for this [ViewOffsetHelper]'s view.
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
     * Set the left and right offset for this [ViewOffsetHelper]'s view.
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
