package modulon.component.cell

import android.content.Context
import modulon.R
import modulon.extensions.view.backgroundSelectorColor
import modulon.extensions.view.backgroundTintColor
import modulon.extensions.view.dp

class ComponentSpacerCell(context: Context) : ComponentPaddingCell(context) {

    var height = modulon.UI.SPACING.dp
        @JvmName("getHeightKt") get
        set(value) {
            field = value
            requestLayout()
        }

    init {
        setPadding(0, 0, 0, 0)
        // TODO: 2022/2/20 replace with local transparent color
        background = null
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
        setMeasuredDimension(measuredWidth, height)
    }

}
