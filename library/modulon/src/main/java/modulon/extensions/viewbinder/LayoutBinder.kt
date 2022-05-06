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
import modulon.layout.actionbar.ActionBarLayout
import modulon.layout.coordinator.behavior.ActionBarBehavior
import modulon.layout.frame.FrameLayout
import modulon.layout.linear.HorizontalLayout
import modulon.layout.linear.LinearLayout
import modulon.layout.frame.NestedHostLayout
import modulon.layout.linear.VerticalLayout
import modulon.layout.recycler.BounceEdgeEffectFactory
import modulon.layout.recycler.RecyclerLayout
import modulon.layout.tab.TabLayout
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
inline fun UnionContext.createFrameLayout(block: FrameLayout.() -> Unit = {}) = FrameLayout(context).apply(block)
@OptIn(ExperimentalContracts::class)
inline fun ViewGroup.frameLayout(block: FrameLayout.() -> Unit = {}) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    addView(FrameLayout(context).apply(block))
}

// TableLayout
inline fun ViewGroup.tableLayout(block: TableLayout.() -> Unit = {}) = addDefaultRow(TableLayout(context).apply(block))

// BaseVerticalLayout
inline fun UnionContext.createVerticalLayout(block: VerticalLayout.() -> Unit = {}) = VerticalLayout(context).apply(block)

// FIXME: 2022/5/4 addDefaultRow -> addView
@OptIn(ExperimentalContracts::class)
inline fun ViewGroup.verticalLayout(block: VerticalLayout.() -> Unit = {}) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    addView(VerticalLayout(context).apply(block))
}

// BaseHorizontalLayout
inline fun UnionContext.createHorizontalLayout(block: HorizontalLayout.() -> Unit = {}) = HorizontalLayout(context).apply(block)
@OptIn(ExperimentalContracts::class)
inline fun ViewGroup.horizontalLayout(block: HorizontalLayout.() -> Unit = {}) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    addView(HorizontalLayout(context).apply(block))
}

// BaseLinearLayout
@OptIn(ExperimentalContracts::class)
inline fun ViewGroup.linearLayout(block: LinearLayout.() -> Unit = {}) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    addView(LinearLayout(context).apply(block))
}

// TabLayout
@OptIn(ExperimentalContracts::class)
inline fun ViewGroup.tabLayout(block: TabLayout.() -> Unit = {}) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    addView(TabLayout(context).apply(block))
}

// BaseRecyclerView
inline fun UnionContext.createRecyclerLayout(block: RecyclerLayout.() -> Unit = {}) = RecyclerLayout(context).apply(block)
@OptIn(ExperimentalContracts::class)
inline fun ViewGroup.recyclerLayout(block: RecyclerLayout.() -> Unit = {}) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    addDefaultFill(RecyclerLayout(context).apply(block))
}

// NestedScrollableHost
inline fun UnionContext.createNestedScrollableHost(block: NestedHostLayout.() -> Unit = {}) = NestedHostLayout(context).apply(block)
inline fun ViewGroup.nestedScrollableHost(block: NestedHostLayout.() -> Unit = {}) = addDefaultRow(NestedHostLayout(context).apply(block))


// PagerLayout
inline fun UnionContext.createPagerLayout(block: ViewPager2.() -> Unit = {}) = ViewPager2(context).setup().apply(block)
inline fun ViewGroup.pagerLayout(block: ViewPager2.() -> Unit = {}) = addDefaultFill(ViewPager2(context).setup().apply(block))

fun ViewPager2.setup() = apply {
// TODO: 15/11/2021 move to child class

//    edgeEffectColor = context.getColor(R.color.component_dark_gray)
    edgeEffectFactory = BounceEdgeEffectFactory(HORIZONTAL)
    doOnPageScrolled { position, positionOffset, positionOffsetPixels ->

    }
}
/**
 * DSL extensions for creating different type of cells
 */


// FragmentContainerView
inline fun UnionContext.createFragmentContainer(block: FragmentContainerView.() -> Unit = {}) = FragmentContainerView(context).apply(block)
inline fun ViewGroup.fragmentContainer(block: FragmentContainerView.() -> Unit = {}) = addDefaultFill(FragmentContainerView(context).apply(block))


