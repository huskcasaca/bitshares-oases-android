package modulon.layout.recycler.section

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import modulon.R
import modulon.component.ComponentPaddingCell
import modulon.component.IconSize
import modulon.extensions.view.*
import modulon.extensions.viewbinder.*
import modulon.layout.frame.FrameLayout
import modulon.layout.linear.HorizontalLayout
import modulon.layout.linear.VerticalLayout
import modulon.widget.FieldTextView
import modulon.widget.PlainTextView
import modulon.widget.SliderView
import modulon.widget.ToggleView


class RecyclerHeader(context: Context) : ComponentPaddingCell(context) {

    private val containerHeader: FrameLayout by lazyView {
        noMotion()
        addRow(titleView)
        addRow(subtitleView, gravity = Gravity.END or Gravity.CENTER_VERTICAL)
    }

    private val container = createHorizontalLayout {
        noClipping()
        noMotion()
        addWrap(
            containerHeader,
            weight = 1f,
            width = 0,
            height = ViewGroup.LayoutParams.WRAP_CONTENT,
        )
    }

    init {
        noClipping()
        noMotion()
        addRow(container)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
    }


}