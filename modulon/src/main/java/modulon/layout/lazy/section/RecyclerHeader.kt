package modulon.layout.lazy.section

import android.content.Context
import android.view.Gravity
import modulon.component.cell.ComponentPaddingCell
import modulon.extensions.view.*
import modulon.extensions.viewbinder.*
import modulon.layout.stack.StackView


class RecyclerHeader(context: Context) : ComponentPaddingCell(context) {

    private val containerHeader: StackView by lazyView {
        noMotion()
        viewRow(titleView)
        viewRow(subtitleView) {
            layoutGravityFrame = Gravity.END or Gravity.CENTER_VERTICAL
        }
    }

    private val container = createHorizontalLayout {
        noClipping()
        noMotion()
        view(containerHeader) {
            layoutWidth = 0
            layoutWeightLinear = 1f
        }
    }

    init {
        noClipping()
        noMotion()
        viewRow(container)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
    }


}