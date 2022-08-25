package modulon.extensions.viewbinder

import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.viewpager2.widget.ViewPager2
import modulon.extensions.view.*
import modulon.layout.stack.StackView
import modulon.layout.linear.HorizontalView
import modulon.layout.linear.LinearView
import modulon.layout.stack.NestedHostView
import modulon.layout.linear.VerticalView
import modulon.layout.lazy.BounceEdgeEffectFactory
import modulon.layout.lazy.LazyListView
import modulon.component.tab.TabView
import modulon.union.UnionContext
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * DSL extensions for creating different type of layouts
 */

// ConstraintLayout
inline fun UnionContext.createConstraintLayout(block: ConstraintLayout.() -> Unit = {}) = ConstraintLayout(context).apply(block)
inline fun ViewGroup.constraintLayout(block: ConstraintLayout.() -> Unit = {}) = addDefaultRow(ConstraintLayout(context).apply(block))

// CoordinatorLayout
inline fun UnionContext.createCoordinatorLayout(block: CoordinatorLayout.() -> Unit = {}) = CoordinatorLayout(context).apply(block)
inline fun ViewGroup.coordinatorLayout(block: CoordinatorLayout.() -> Unit = {}) = addDefaultRow(CoordinatorLayout(context).apply(block))

// RelativeLayout
inline fun UnionContext.createRelativeLayout(block: RelativeLayout.() -> Unit = {}) = RelativeLayout(context).apply(block)
inline fun ViewGroup.relativeLayout(block: RelativeLayout.() -> Unit = {}) = addDefaultRow(RelativeLayout(context).apply(block))

// ToolbarLayout
inline fun UnionContext.createToolbarLayout(block: Toolbar.() -> Unit = {}) = Toolbar(context).apply(block)
inline fun ViewGroup.toolbarLayout(block: Toolbar.() -> Unit = {}) = addDefaultRow(Toolbar(context).apply(block))

// NestedScrollLayout
inline fun UnionContext.createNestedScrollLayout(block: NestedScrollView.() -> Unit = {}) = NestedScrollView(context).apply(block)
inline fun ViewGroup.nestedScrollLayout(block: NestedScrollView.() -> Unit = {}) = addDefaultRow(NestedScrollView(context).apply(block))

// ScrollLayout
inline fun UnionContext.createScrollLayout(block: ScrollView.() -> Unit = {}) = ScrollView(context).apply(block)
inline fun ViewGroup.scrollLayout(block: ScrollView.() -> Unit = {}) = addDefaultRow(ScrollView(context).apply(block))

// ScrollLayout
inline fun UnionContext.createHorizontalScrollLayout(block: HorizontalScrollView.() -> Unit = {}) = HorizontalScrollView(context).apply(block)
inline fun ViewGroup.horizontalScrollLayout(block: HorizontalScrollView.() -> Unit = {}) = addDefaultRow(HorizontalScrollView(context).apply(block))

// DrawerLayout
inline fun UnionContext.createDrawerLayout(block: DrawerLayout.() -> Unit = {}) = DrawerLayout(context).apply(block)
inline fun ViewGroup.drawerLayout(block: DrawerLayout.() -> Unit = {}) = addDefaultRow(DrawerLayout(context).apply(block))

// FrameLayout
inline fun UnionContext.createFrameLayout(block: StackView.() -> Unit = {}) = StackView(context)
    .apply(block)
@OptIn(ExperimentalContracts::class)
inline fun ViewGroup.frameLayout(block: StackView.() -> Unit = {}) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    addView(StackView(context).apply(block))
}

// TableLayout
inline fun ViewGroup.tableLayout(block: TableLayout.() -> Unit = {}) = addDefaultRow(TableLayout(context).apply(block))

// BaseVerticalLayout
inline fun UnionContext.createVerticalLayout(block: VerticalView.() -> Unit = {}) = VerticalView(context).apply(block)

// FIXME: 2022/5/4 addDefaultRow -> addView
@OptIn(ExperimentalContracts::class)
inline fun ViewGroup.verticalLayout(block: VerticalView.() -> Unit = {}) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    addView(VerticalView(context).apply(block))
}

// BaseHorizontalLayout
inline fun UnionContext.createHorizontalLayout(block: HorizontalView.() -> Unit = {}) = HorizontalView(context).apply(block)
@OptIn(ExperimentalContracts::class)
inline fun ViewGroup.horizontalLayout(block: HorizontalView.() -> Unit = {}) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    addView(HorizontalView(context).apply(block))
}

// BaseLinearLayout
@OptIn(ExperimentalContracts::class)
inline fun ViewGroup.linearLayout(block: LinearView.() -> Unit = {}) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    addView(LinearView(context).apply(block))
}

// TabLayout
@OptIn(ExperimentalContracts::class)
inline fun ViewGroup.tabLayout(block: TabView.() -> Unit = {}) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    addView(TabView(context).apply(block))
}

// BaseRecyclerView
inline fun UnionContext.createRecyclerLayout(block: LazyListView.() -> Unit = {}) = LazyListView(context).apply(block)
@OptIn(ExperimentalContracts::class)
inline fun ViewGroup.recyclerLayout(block: LazyListView.() -> Unit = {}) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    addDefaultFill(LazyListView(context).apply(block))
}

// NestedScrollableHost
inline fun UnionContext.createNestedScrollableHost(block: NestedHostView.() -> Unit = {}) = NestedHostView(context).apply(block)
inline fun ViewGroup.nestedScrollableHost(block: NestedHostView.() -> Unit = {}) = addDefaultRow(NestedHostView(context).apply(block))


// PagerLayout
inline fun UnionContext.createPagerLayout(block: ViewPager2.() -> Unit = {}) = ViewPager2(context).setup().apply(block)
inline fun ViewGroup.pagerLayout(block: ViewPager2.() -> Unit = {}) = addDefaultFill(ViewPager2(context).setup().apply(block))

fun ViewPager2.setup() = apply {
// TODO: 15/11/2021 move to child class

//    edgeEffectColor = context.getColor(R.color.component_dark_gray)
//    edgeEffectFactory = BounceEdgeEffectFactory(HORIZONTAL)
    doOnPageScrolled { position, positionOffset, positionOffsetPixels ->

    }
}
/**
 * DSL extensions for creating different type of cells
 */


// FragmentContainerView
inline fun UnionContext.createFragmentContainer(block: FragmentContainerView.() -> Unit = {}) = FragmentContainerView(context).apply(block)
inline fun ViewGroup.fragmentContainer(block: FragmentContainerView.() -> Unit = {}) = addDefaultFill(FragmentContainerView(context).apply(block))


