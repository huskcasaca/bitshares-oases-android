package modulon.component

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableLayout
import androidx.core.view.*
import modulon.R
import modulon.extensions.view.*
import modulon.extensions.viewbinder.*
import modulon.layout.frame.FrameLayout
import modulon.layout.linear.HorizontalLayout
import modulon.layout.linear.VerticalLayout
import modulon.union.Union
import modulon.widget.*

class ComponentCell(context: Context) : ComponentPaddingCell(context) {

    override var text: CharSequence
        get() = textView.text
        set(text) {
            textView.textWithVisibility = text
        }

    override val iconView = create<ImageView> {
        isVisible = false
    }

    override var iconSize: IconSize = IconSize.TINY
        set(value) {
            iconView.layoutWidth = resources.getDimensionPixelSize(value.size)
            iconView.layoutHeight = resources.getDimensionPixelSize(value.size)
            requestLayout()
            field = value
        }

    private val containerHeader: FrameLayout by lazyView {
        noMotion()
        addRow(titleView)
        addRow(subtitleView, gravity = Gravity.END or Gravity.CENTER_VERTICAL)
    }

    private val containerBody = create<VerticalLayout> {
        noClipping()
        isVisible = false
    }

    private val containerText = createFrameLayout {
        addRow(textView)
    }

    private val containerForm = createVerticalLayout {
        noMotion()
        noClipping()
        addRow(containerHeader)
        addRow(containerText)
        addRow(containerBody)
        addRow(subtextView)
        addRow(containerSubviews)
    }

    private val containerStart = createFrameLayout {
        isVisible = false
    }

    private val containerEnd = createFrameLayout {
        isVisible = false
    }

    private val container = createHorizontalLayout {
        noClipping()
        noMotion()
        addWrap(
            containerStart,
            end = componentOffset,
            gravity = Gravity.START or Gravity.CENTER_VERTICAL
        )
        addWrap(
            iconView,
            height = resources.getDimensionPixelSize(iconSize.size),
            width = resources.getDimensionPixelSize(iconSize.size),
            end = componentOffset,
            gravity = Gravity.START or Gravity.CENTER_VERTICAL
        )
        // TODO: 23/1/2022 consider ui time consumption
        addWrap(
            containerForm,
            weight = 1f,
            width = 0,
            height = ViewGroup.LayoutParams.WRAP_CONTENT,
            gravity = Gravity.START or Gravity.CENTER_VERTICAL
        )
        addWrap(containerEnd,
            start = componentOffset,
            gravity = Gravity.END or Gravity.CENTER_VERTICAL
        )
    }

    // TODO: 22/1/2022 remove
    open var isChecked = false
        set(value) {
            checkView.isVisible = value
            field = value
        }

    var allowMultiLine = false
        set(value) {
            field = value
            requestLayout()
        }

    // TODO: 21/1/2022 avoid conflicts
    var customView: View? = null
        set(value) {
            field = value
            containerForm.removeView(field)
            if (value != null) containerForm.addRow(value)
        }

    var customViewStart: View? = null
        set(value) {
            field = value
            containerStart.removeView(field)
            containerStart.isVisible = value != null
            if (value != null) containerStart.addDefault(value)
        }

    var customViewEnd: View? = null
        set(value) {
            field = value
            containerEnd.removeView(field)
            containerEnd.isVisible = value != null
            if (value != null) containerEnd.addDefault(value)
        }

    init {
        addRow(container)
        addWrap(checkView, 6.dp, ViewGroup.LayoutParams.MATCH_PARENT, -paddingStart, -paddingTop, -paddingEnd, -paddingBottom, Gravity.CENTER_VERTICAL)
        backgroundSelectorColor = R.color.background_component.contextColor()
    }

    override fun setPadding(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPadding(start, top, end, bottom)
        checkView.setFrameParams(6.dp, MATCH_PARENT, -start, -top, -end, -bottom, Gravity.CENTER_VERTICAL)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        val available = MeasureSpec.getSize(widthMeasureSpec) - paddingStart - paddingEnd
        subtitleView.measure(MeasureSpec.makeMeasureSpec(available, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
        titleView.measure(MeasureSpec.makeMeasureSpec(available, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))

        if (subtitleView.measuredWidth + titleView.measuredWidth + componentOffsetHalf > available) {
            if (allowMultiLine) {
                subtitleView.updatePadding(top = titleView.measuredHeight)
                subtitleView.layoutGravityFrame = Gravity.START or Gravity.CENTER_VERTICAL
                subtitleView.gravity = Gravity.START
                titleView.measure(MeasureSpec.makeMeasureSpec(available, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
                subtitleView.measure(MeasureSpec.makeMeasureSpec(available, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
//                containerHeader.updateViewLayout(subtitleView, subtitleView.layoutParams)
                super.onMeasure(
                    MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                )
            } else {
                subtitleView.updatePadding(top = 0)
                subtitleView.layoutGravityFrame = Gravity.END or Gravity.CENTER_VERTICAL
                subtitleView.gravity = Gravity.END
                titleView.measure(MeasureSpec.makeMeasureSpec(available * 4 / 5, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
                subtitleView.measure(MeasureSpec.makeMeasureSpec(available - titleView.measuredWidth - componentOffsetHalf, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))

            }
        } else {
            subtitleView.updatePadding(top = 0)
            subtitleView.layoutGravityFrame = Gravity.END or Gravity.CENTER_VERTICAL
            subtitleView.gravity = Gravity.END
            super.onMeasure(
                MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
        }
    }

    fun ComponentCell.custom(block: ComponentCustomHorizontalLayout.() -> Unit = {}) {
        containerBody.isVisible = true
        containerBody.addRow(create(block).apply {
            layoutTransition = null
            noClipping()
            updatePaddingTop(2.dp)
        })
    }

    fun ComponentCell.field(block: FieldTextView.() -> Unit = {}) {
        custom { field(block) }
    }

    fun ComponentCell.slider(block: SliderView.() -> Unit = {}) {
        custom { slider(block) }
    }
    fun ComponentCell.table(block: TableLayout.() -> Unit = {}) {
        custom { table(block) }
    }
//    fun ComponentCell.text(block: PlainTextView.() -> Unit = {}) {
//        custom { text(block) }
//    }

    fun ComponentCustomHorizontalLayout.field(block: FieldTextView.() -> Unit = {}) {
        addWrap(create(block), weight = 1f, width = 0, start = (-4).dp, end = (-4).dp, gravity = Gravity.CENTER_VERTICAL)
    }

    fun ComponentCustomHorizontalLayout.slider(block: SliderView.() -> Unit = {}) {
        addWrap(create(block), weight = 1f, width = 0, start = (-12).dp, end = (-12).dp, height = 32.dp, gravity = Gravity.CENTER_VERTICAL)
    }

    fun ComponentCustomHorizontalLayout.text(block: PlainTextView.() -> Unit = {}) {
        addWrap(create(block), start = componentOffset, gravity = Gravity.CENTER_VERTICAL)
    }

    fun ComponentCustomHorizontalLayout.toggle(block: ToggleView.() -> Unit) {
        val toggle = create<ToggleView>().apply {
            setColors(context.getColor(R.color.component), context.getColor(R.color.component_inactive))
            expandTouchArea(32.dp, 32.dp, 32.dp, 32.dp)
        }
        addWrap(toggle.apply(block), start = componentOffset, gravity = Gravity.CENTER_VERTICAL)
    }

    fun ComponentCustomHorizontalLayout.table(block: TableLayout.() -> Unit = {}) {
        addRow(create(block), gravity = Gravity.CENTER_VERTICAL)
    }

    class ComponentCustomHorizontalLayout(context: Context) : HorizontalLayout(context)

    val ComponentCell.tableView: TableLayout
        get() {
            containerBody.forEach<ComponentCustomHorizontalLayout> { it.forEach<View> { if (it is TableLayout) return it } }
            table()
            containerBody.forEach<ComponentCustomHorizontalLayout> { it.forEach<View> { if (it is TableLayout) return it } }
            throw NullPointerException("Tableview Notfound")
        }

}