package modulon.layout.actionbar

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isNotEmpty
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import modulon.R
import modulon.extensions.animation.visibilityLayoutTransition
import modulon.extensions.compat.activity
import modulon.extensions.compat.hideSoftKeyboard
import modulon.extensions.font.typefaceBold
import modulon.extensions.font.typefaceExtraBold
import modulon.extensions.graphics.createIconSelectorDrawable
import modulon.extensions.graphics.tint
import modulon.extensions.view.*
import modulon.extensions.viewbinder.*
import modulon.layout.coordinator.behavior.ActionBarBehavior
import modulon.layout.frame.FrameLayout
import modulon.widget.PlainTextView
import modulon.widget.TextViewSwitcher

class ActionBarLayout(context: Context) : FrameLayout(context) {

    class Item(context: Context) : FrameLayout(context) {

        val iconView: ImageView = create()

        // TODO: 2022/2/17
        var text: CharSequence = ""

        var icon: Drawable?
            get() = iconView.drawable
            // TODO: 2022/2/28 change color
            set(drawable) = iconView.setImageDrawable(drawable?.tint(R.color.cell_text_primary.contextColor()))

        init {
            background = createIconSelectorDrawable(context.getColor(R.color.background))
            isClickable = true
            addWrap(iconView, gravity = Gravity.CENTER)
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(
                MeasureSpec.makeMeasureSpec(48.dp, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(48.dp, MeasureSpec.EXACTLY)
            )
        }
    }

    companion object {
        // TODO: 2022/2/17 move to dimens.xml
        private const val TITLE_FONT_SCALED = 36f
        private const val TITLE_FONT_NORMAL = 20f
        private const val SUBTITLE_FONT_SCALED = 17.5f
        private const val SUBTITLE_FONT_NORMAL = 15.5f

        private const val TITLE_FONT_SCALE = TITLE_FONT_SCALED / TITLE_FONT_NORMAL
        private const val SUBTITLE_FONT_SCALE = SUBTITLE_FONT_SCALED / SUBTITLE_FONT_NORMAL

        private const val TITLES_Y_SCALE = 0.9f
        private val TITLE_Y_OFFSET = 64.dpf
        private val TITLE_X_OFFSET = 44.dpf

    }

    val actionButton: Item = create {
        icon = R.drawable.ic_cell_back_arrow.contextDrawable()
        doOnClick { if (isExpanded) collapseActionView() else activity.onBackPressed() }
    }

    var icon: Drawable?
        get() = iconView.drawable
        set(icon) {
            iconView.isVisible = icon != null
            iconView.setImageDrawable(icon)
        }

    val iconView: ImageView = create {
        isVisible = false
//        val backgroundColor = context.getColor(R.color.background_cover)
//        val backgroundRadius = 44.dpf / 10
//        background = createRoundRectDrawable(backgroundColor, backgroundRadius)
    }

    private val titleView: TextViewSwitcher = create {
        setFactory {
            create<PlainTextView> {
                textSize = TITLE_FONT_SCALED
                typeface = typefaceExtraBold
                startScrolling()
                textColor = context.getColor(R.color.title)
            }
        }
// TODO: 16/1/2022 animation removed
//        inAnimation = animationSet {
//            translate((-16).dpf, 0f)
//            alpha(0f, 1f)
//            startOffset = 120
//            duration = 180
//            interpolator = DecelerateInterpolator()
//        }
//        outAnimation = animationSet {
//            translate(0f, 16.dpf)
//            alpha(1f, 0f)
//            duration = 180
//            interpolator = DecelerateInterpolator()
//        }
    }

    private val subtitleView: TextViewSwitcher = create {
        isVisible = false
        setFactory {
            create<PlainTextView> {
                textSize = SUBTITLE_FONT_SCALED
                typeface = typefaceBold
                startScrolling()
                textColor = context.getColor(R.color.subtitle)
            }
        }
// TODO: 16/1/2022 animation removed
//        inAnimation = animationSet {
//            translate((-16).dpf, 0f)
//            alpha(0f, 1f)
//            startOffset = 120
//            duration = 180
//            interpolator = DecelerateInterpolator()
//        }
//        outAnimation = animationSet {
//            translate(0f, 16.dpf)
//            alpha(1f, 0f)
//            duration = 180
//            interpolator = DecelerateInterpolator()
//        }
    }

    var title: CharSequence
        get() = titleView.text
        set(text) {
            titleView.isVisible = true
            titleView.text = text
        }

    var subtitle: CharSequence
        get() = subtitleView.text
        set(text) {
            subtitleView.isVisible = text.isNotBlank()
            subtitleView.text = text
        }

    var actionView: View? = null
        set(value) {
            customSection.removeAllViews()
            if (value != null) customSection.addView(value)
            field = value
        }

    private val collapseListeners = mutableListOf<() -> Unit>()
    private val expandListeners = mutableListOf<() -> Unit>()

    var isCollapsed
        get() = !customSection.isVisible
        set(value) {
            if (value == isCollapsed) return
            val behavior = (layoutParams as? CoordinatorLayout.LayoutParams)?.behavior
            if (behavior is ActionBarBehavior) {
                behavior.animateToPositionLock(this, !value)
            }
            if (value) {
                customSection.isVisible = false
                titleSection.isVisible = true
                menuSection.isVisible = true
                collapseListeners.forEach { it.invoke() }
                //            avatarView.isVisible = toolbarAvatarSeed.isNotEmpty()
                // TODO: 10/10/2021 useless? test
                hideSoftKeyboard()
            } else {
                customSection.isVisible = true
                titleSection.isVisible = false
                menuSection.isVisible = false
                expandListeners.forEach { it.invoke() }
                //            avatarView.isVisible = false
            }
        }

    var isExpanded
        get() = !isCollapsed
        set(value) { isCollapsed = !value }

    private val titleSection = createVerticalLayout {
        noClipping()
        layoutAnimation = null
        layoutTransition = visibilityLayoutTransition
        addWrap(titleView)
        addWrap(subtitleView)
    }

    private val menuSection = createHorizontalLayout {
        noClipping()
        noMotion()
    }

    private val customSection = createFrameLayout {
        isVisible = false
    }

    private val components = createFrameLayout {
        noClipping()
        noMotion()
        addRow(titleSection, gravity = Gravity.CENTER_VERTICAL or Gravity.START)
        addWrap(iconView, width = 40.dp, height = 40.dp, gravity = Gravity.CENTER_VERTICAL or Gravity.START)
        addDefaultFill(customSection)
    }

    private val titleScaleInterpolator = DecelerateInterpolator(1.5f)

    val isOnBottom get() = translation == 0 && translationY == 0f

    val isOnTop get() = translation == 64.dp && translationY == -64.dp.toFloat()

    var translation = -1
        set(value) {
            val scroll = value.coerceIn(0, 64.dp)
//            if (field == value) return
            val scrollY = scroll.toFloat()
            val factor = (1f - scrollY / TITLE_Y_OFFSET).coerceIn(0f..1f)
            translationY = -scrollY
            actionButton.translationY = scrollY
            menuSection.translationY = scrollY
            components.translationY = scrollY
            titleSection.apply {
                translationX = if (actionButton.isVisible) TITLE_X_OFFSET * (titleScaleInterpolator.getInterpolation(scrollY / TITLE_Y_OFFSET) - 1) else 0f
                translationY = TITLES_Y_SCALE * TITLE_Y_OFFSET * factor
            }
            titleView.forEach<PlainTextView> {
                it.marqueeRepeatLimit = if (factor != 0f && factor != 1f) 0 else -1
//                it.scaleX = ((TITLE_FONT_SCALE - 1) * factor + 1)
//                it.scaleY = ((TITLE_FONT_SCALE - 1) * factor + 1)
                it.textSize = TITLE_FONT_NORMAL * ((TITLE_FONT_SCALE - 1) * factor + 1)
            }
            subtitleView.forEach<PlainTextView> {
                it.marqueeRepeatLimit = if (factor != 0f && factor != 1f) 0 else -1
                it.textSize = SUBTITLE_FONT_NORMAL * ((SUBTITLE_FONT_SCALE - 1) * factor + 1)
            }
//            titleView.translationY = 2.dpf
            subtitleView.translationY = (-4).dpf * factor
            iconView.apply {
                translationY = TITLES_Y_SCALE * TITLE_Y_OFFSET * factor
                translationX = if (actionButton.isVisible) 40.dpf * (titleScaleInterpolator.getInterpolation(scrollY / TITLE_Y_OFFSET) - 1) else 0f
                scaleX = (1.2f - 1) * factor + 1
                scaleY = (1.2f - 1) * factor + 1
            }
            // TODO: 22/12/2021 after 64.dpf return
            field = scroll
        }


    init {
        setPadding(12.dp, 8.dp, 8.dp, 12.dp)
        clipChildren = false
        clipToPadding = false
        layoutTransition = null
        addWrap(actionButton, start = -6.dp, gravity = Gravity.TOP or Gravity.START)
        addWrap(components, width = MATCH_PARENT, height = 48.dp, gravity = Gravity.TOP or Gravity.START)
        addWrap(menuSection, gravity = Gravity.TOP or Gravity.END)
        // TODO: 1/2/2022 backgroundColor as var
        backgroundTintColor = context.getColor(R.color.background)
        translation = 0
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(128.dp, MeasureSpec.EXACTLY)
        )
        components.updatePadding(if (actionButton.isVisible) actionButton.measuredWidth + 8.dp else 12.dp)
        menuSection.measure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST)
        )
        titleSection.updatePadding(if (iconView.isVisible) (52.dp * iconView.scaleX).toInt() else 0)
        val menuWidth = if (menuSection.isNotEmpty()) menuSection.measuredWidth + 8.dp else 12.dp
        val a = titleScaleInterpolator.getInterpolation(1f - titleSection.translationY / TITLES_Y_SCALE / TITLE_Y_OFFSET)
        val toMinus = ((actionButton.measuredWidth + menuWidth) * a).toInt() + 12.dp
        titleSection.measure(
            MeasureSpec.makeMeasureSpec(components.measuredWidth - toMinus , MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST)
        )
    }

    fun setActionBarView(view: View, params: ViewGroup.LayoutParams? = null) {
        customSection.removeAllViews()
        customSection.addView(view, params)
    }

    @Deprecated("")
    fun collapseActionView() {
        isCollapsed = true
    }

    @Deprecated("")
    fun expandActionView() {
        isExpanded = true
    }

    // listeners
    fun addOnCollapseListener(listener: () -> Unit) {
        collapseListeners.add(listener)
    }

    fun clearOnCollapseListeners() {
        collapseListeners.clear()
    }

    fun addOnExpandListener(listener: () -> Unit) {
        expandListeners.add(listener)
    }

    fun clearOnExpandListeners() {
        expandListeners.clear()
    }

    fun addMenu(item: Item) {
        menuSection.addView(item)
    }

    fun removeMenuItem(item: Item) {
        menuSection.addView(item)
    }

}


