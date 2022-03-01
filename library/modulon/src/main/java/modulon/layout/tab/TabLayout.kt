package modulon.layout.tab

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.util.StateSet
import android.view.Gravity
import android.view.animation.DecelerateInterpolator
import android.widget.HorizontalScrollView
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import modulon.R
import modulon.UI
import modulon.component.BaseCell
import modulon.extensions.font.typefaceBold
import modulon.extensions.graphics.blendColorARGB
import modulon.extensions.graphics.createSelectorDrawable
import modulon.extensions.temp.drawShaders
import modulon.extensions.view.*
import modulon.extensions.viewbinder.createHorizontalLayout
import modulon.extensions.viewbinder.noClipping
import modulon.interpolator.CubicBezierInterpolator
import modulon.layout.linear.HorizontalLayout
import modulon.union.UnionContext
import modulon.union.toUnion
import kotlin.math.abs

class TabLayout(context: Context) : HorizontalScrollView(context), UnionContext by context.toUnion() {

    class TabView(context: Context) : BaseCell(context) {

        private val container = createHorizontalLayout {
            addWrap(iconView, width = 24.dp, height = 24.dp, gravity = Gravity.CENTER, end = 8.dp)
            addWrap(textView, gravity = Gravity.CENTER)
            setFrameParams(gravity = Gravity.CENTER)
            updatePadding((12 + UI.SPACING).dp, 18.dp, (12 + UI.SPACING).dp, 18.dp)

        }

        private var increased = false

        internal var progress = 0f
            set(value) {
                if (increased != value < 0.5f) {
                    increased = value < 0.5f
                    animate(value, value < 0.5f)
                    field = value
                }
            }

        private val activeComponentColor = R.color.text_primary_inverted.contextColor()
        private val inactiveComponentColor = R.color.component_dark_gray.contextColor()

        private val activeBackgroundColor = R.color.component.contextColor()
        private val inactiveBackgroundColor = R.color.background.contextColor()

        private val componentColorInterpolator = CubicBezierInterpolator(1f, 0f, 0.7f, 0.7f)

        var lastAnimator = ValueAnimator()
        private fun animate(from: Float, increased: Boolean) {
            lastAnimator.cancel()
            lastAnimator = ValueAnimator.ofFloat(from, if (increased) 0f else 1f).apply {
                interpolator = DecelerateInterpolator()
                duration = 240L
                addUpdateListener {
                    val value = it.animatedValue as Float
                    val blended = blendColorARGB(activeComponentColor, inactiveComponentColor, componentColorInterpolator.getInterpolation(abs(value)))
                    iconView.filterColor = blended
                    textView.textSolidColor = blended
                    val blendedBg = blendColorARGB(activeBackgroundColor, inactiveBackgroundColor, abs(value))
                    (background as RippleDrawable).apply {
                        val selectorColor = blendedBg and 0x00ffffff xor 0x003a3a3a or 0xff000000.toInt()
                        val backgroundColor = blendedBg
                        setColor(ColorStateList(arrayOf(StateSet.WILD_CARD), intArrayOf(selectorColor)))
                        (getDrawable(0) as ShapeDrawable).paint.color = backgroundColor
                    }
                }
                start()
            }
        }

        override var icon: Drawable?
            get() = super.icon
            set(value) {
                super.icon = value
                iconView.isVisible = value != null
            }

        var isChecked = false
            set(value) {
                field = value
                iconView.filterColor = if (value) activeComponentColor else inactiveComponentColor
                textView.textSolidColor = if (value) activeComponentColor else inactiveComponentColor
            }

        init {
            minimumWidth = 42.dp
            iconView.isVisible = false
            textView.apply {
                textSize = 15.5f
                typeface = typefaceBold
                isAllCaps = true
            }
            background = createSelectorDrawable(inactiveBackgroundColor, Float.MAX_VALUE)
            layoutGravityLinear = Gravity.CENTER
            addNoParams(container)
            drawShaders()
        }


    }

    companion object {

        const val GRAVITY_FILL = 0
        const val GRAVITY_CENTER = 1
        const val GRAVITY_START = 1 shl 1
    }

//    @Deprecated("No use")
//    var tabGravity = GRAVITY_START
//        set(value) {
//            field = value
//            val gravity = when (value) {
//                GRAVITY_FILL -> Gravity.FILL
//                GRAVITY_CENTER -> Gravity.CENTER_HORIZONTAL
//                else -> Gravity.START
//            }
//            tabsContainer.layoutGravityFrame = gravity
//
//        }
//
//    @Deprecated("No use")
//    var tabMode = 0 // TabLayout.MODE_SCROLLABLE
//    @Deprecated("No use")
//    var isInlineLabel = false

    private var currentPosition = 0
    private var currentPositionOffset = 0f

    var scrollOffset = 64.dp
        set(value) {
            field = value
            postInvalidate()
        }

    var indicatorHeight = 4.dp
        set(value) {
            field = value
            postInvalidate()
        }

    var indicatorColor = context.getColor(R.color.component)
        set(value) {
            field = value
            postInvalidate()
        }

    private var lastScrollX = 0

    private val rectPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = indicatorColor
    }

    private val tabCount get() = tabsContainer.childCount

    val tabs get() = tabsContainer.children as Sequence<TabView>

    private val tabsContainer = create<HorizontalLayout> {
        noClipping()
        updatePadding(left = 8.dp, right = 8.dp)
    }

    fun scrollToChild(position: Int, offset: Int = 0) {
        if (tabCount == 0 || position >= tabCount) return
        var newScrollX = tabsContainer.getChildAt(position).left + offset
        if (position > 0 || offset >= 0) {
            newScrollX -= scrollOffset
        }
        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX
            post { smoothScrollTo(newScrollX, 0) }
        }
    }

    fun scrollToPositionOffset(position: Int, positionOffset: Float) {
        if (tabCount == 0 || position >= tabCount) return
        currentPosition = position
        currentPositionOffset = positionOffset
        scrollToChild(position, (positionOffset * tabsContainer.getChildAt(position).width).toInt())
        invalidateTabs()
    }

    fun selectTab(position: Int) {
        tabs.forEachIndexed { index, tab ->
            tab.isChecked = index == position
            tab.isSelected = index == position
        }
    }

    private fun invalidateTabs() {
        tabs.forEachIndexed { index, tab ->
            val offset = abs(currentPosition + currentPositionOffset - index).coerceIn(0f..1f)
            if (offset != tab.progress || offset == 0f) tab.progress = offset
        }
    }

    init {
        noClipping()
//        isFillViewport = true
//        setWillNotDraw(false)
        overScrollMode = OVER_SCROLL_NEVER
        isScrollBarEnabled = false
        addDefault(tabsContainer)
        setParamsRow()
    }

    fun removeAllTabs() {
        tabsContainer.removeAllViews()
    }

    fun addTab(tab: TabView) {
        tabsContainer.addViewIndexed(tab)
        if (tabCount == 1) {
            // FIXME: 2022/2/22
            tab.progress = 0f
            selectTab(0)
        }
    }

}
