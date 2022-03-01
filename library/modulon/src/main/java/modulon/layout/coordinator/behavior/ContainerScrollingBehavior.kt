package modulon.layout.coordinator.behavior

import android.graphics.Rect
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import modulon.extensions.view.dp
import modulon.layout.actionbar.ActionBarLayout

class ContainerScrollingBehavior() : HeaderScrollingBehavior() {

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return dependency is ActionBarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        val new = (dependency as ActionBarLayout).translationY
        if (child.translationY != new) {
            child.translationY = new
            return true
        } else {
            return false
        }
    }

    override fun onDependentViewRemoved(parent: CoordinatorLayout, child: View, dependency: View) {
        child.translationY = 0f
    }

    override fun onRequestChildRectangleOnScreen(parent: CoordinatorLayout, child: View, rectangle: Rect, immediate: Boolean): Boolean {
        val header = findFirstDependency(parent.getDependencies(child))
//        if (header != null) {
//            // Offset the rect by the child's left/top
//            rectangle.offset(child.left, child.top)
//            val parentRect = tempRect1
//            parentRect.set(0, 0, parent.width, parent.height)
//            if (!parentRect.contains(rectangle)) {
//                // If the rectangle can not be fully seen the visible bounds, collapse
//                // the AppBarLayout
//                // TODO: 24/12/2021
////                header.setExpanded(false, !immediate)
//                return true
//            }
//        }
        return false
    }

    override fun getOverlapPixelsForOffset(header: View): Int {
        return 0
    }

    override fun findFirstDependency(views: List<View>): View? {
        return views.firstOrNull { it is ActionBarLayout } as ActionBarLayout?
    }

    override fun getScrollRange(v: View): Int {
        return if (v is ActionBarLayout) 64.dp else super.getScrollRange(v)
    }


}
