package modulon.extensions.view

import android.view.MotionEvent
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import modulon.layout.tab.TabLayout
import modulon.layout.tab.tab


typealias ItemClickListener = (View, Int) -> Unit
typealias ItemLongClickListener = (view: View, index: Int) -> Boolean
typealias ItemTouchListener = (view: View, index: Int, event: MotionEvent) -> Boolean


inline fun RecyclerView.doOnScrollEvent(
    crossinline onScrollStateChanged: (newState: Int) -> Unit = {},
    crossinline onScrolled: (dx: Int, dy: Int) -> Unit = { _, _ -> }
) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            onScrollStateChanged.invoke(newState)
            super.onScrollStateChanged(recyclerView, newState)
        }
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            onScrolled.invoke(dx, dy)
            super.onScrolled(recyclerView, dx, dy)
        }
    })
}
inline fun RecyclerView.doOnScrollStateChanged(crossinline listener: (newState: Int) -> Unit) = doOnScrollEvent(onScrollStateChanged = listener)
inline fun RecyclerView.doOnScrolled(crossinline listener: (dx: Int, dy: Int) -> Unit) = doOnScrollEvent(onScrolled = listener)


inline fun ViewPager2.doOnPageChanged(
    crossinline onPageScrolled: (position: Int, positionOffset: Float, positionOffsetPixels: Int) -> Unit = { _, _, _ -> },
    crossinline onPageSelected: (position: Int) -> Unit = { _ -> },
    crossinline onPageScrollStateChanged: (state: Int) -> Unit = { _ -> }
) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            onPageScrolled.invoke(position, positionOffset, positionOffsetPixels)
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            onPageSelected.invoke(position)
            super.onPageSelected(position)
        }

        override fun onPageScrollStateChanged(state: Int) {
            onPageScrollStateChanged.invoke(state)
            super.onPageScrollStateChanged(state)
        }
    })
}


inline fun ViewPager2.doOnPageScrolled(crossinline listener: (position: Int, positionOffset: Float, positionOffsetPixels: Int) -> Unit) = doOnPageChanged(onPageScrolled = listener)
inline fun ViewPager2.doOnPageSelected(crossinline listener: (position: Int) -> Unit) = doOnPageChanged(onPageSelected = listener)
inline fun ViewPager2.doOnPageScrollStateChanged(crossinline listener: (state: Int) -> Unit) = doOnPageChanged(onPageScrollStateChanged = listener)


inline fun DrawerLayout.doOnDrawerEvent(
    crossinline onDrawerSlide: (drawerView: View, slideOffset: Float) -> Unit = { _, _ -> },
    crossinline onDrawerOpened: (drawerView: View) -> Unit = { _ -> },
    crossinline onDrawerClosed: (drawerView: View) -> Unit = { _ -> },
    crossinline onDrawerStateChanged: (newState: Int) -> Unit = { _ -> }
) {
    val listener = object : DrawerLayout.DrawerListener {
        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            onDrawerSlide.invoke(drawerView, slideOffset)
        }
        override fun onDrawerOpened(drawerView: View) {
            onDrawerOpened.invoke(drawerView)
        }
        override fun onDrawerClosed(drawerView: View) {
            onDrawerClosed.invoke(drawerView)
        }
        override fun onDrawerStateChanged(newState: Int) {
            onDrawerStateChanged.invoke(newState)
        }
    }
    addDrawerListener(listener)
}


inline fun DrawerLayout.doOnDrawerSlide(crossinline listener: (drawerView: View, slideOffset: Float) -> Unit) = doOnDrawerEvent(onDrawerSlide = listener)
inline fun DrawerLayout.doOnDrawerOpened(crossinline listener: (drawerView: View) -> Unit) = doOnDrawerEvent(onDrawerOpened = listener)
inline fun DrawerLayout.doOnDrawerClosed(crossinline listener: (drawerView: View) -> Unit = { _ -> }) = doOnDrawerEvent(onDrawerClosed = listener)
inline fun DrawerLayout.doOnDrawerStateChanged(crossinline listener: (newState: Int) -> Unit = { _ -> }) = doOnDrawerEvent(onDrawerStateChanged = listener)




interface StringResTabs {
    val stringRes: Int
}

interface DrawableResTabs {
    val iconRes: Int
}


fun TabLayout.attachViewPager2(viewPager: ViewPager2) {
    scrollToChild(viewPager.currentItem, 0)
    viewPager.apply {
        doOnPageScrolled { position, positionOffset, positionOffsetPixels -> scrollToPositionOffset(position, positionOffset) }
//        doOnPageScrollStateChanged { if (it == ViewPager2.SCROLL_STATE_IDLE) scrollToChild(viewPager.currentItem, 0) }
        doOnPageSelected { selectTab(it) }
    }
    tabs.forEachIndexed { index, tab ->
        tab.doOnClick { viewPager.currentItem = index }
    }
    selectTab(viewPager.currentItem)
}

internal fun TabLayout.TabView.setup(value: Any) {
    if (value is StringResTabs) text = value.stringRes.contextString()
    if (value is DrawableResTabs) icon = value.iconRes.contextDrawable()
}

fun <T: Any> TabLayout.attachTabs(tabs: Collection<T>, block: TabLayout.TabView.(T) -> Unit = { setup(it) }) {
    removeAllTabs()
    tabs.forEach { tab { block.invoke(this, it) } }
}

// TODO: 2022/2/10 normalize
fun <T: Any> TabLayout.attachTabs(tabs: Array<T>, block: TabLayout.TabView.(T) -> Unit = { setup(it) }) {
    removeAllTabs()
    tabs.forEach { tab { block.invoke(this, it) } }
}

@Deprecated("Should be removed")
inline fun <reified T: Enum<T>> TabLayout.attachEnumsViewPager2(viewPager: ViewPager2) {
    val values = enumValues<T>()
    attachTabs(values.toList())
    attachViewPager2(viewPager)
}
