package modulon.layout.lazy.section

import android.content.Context
import modulon.extensions.view.dp
import modulon.component.cell.ComponentPaddingCell

// TODO: 2022/2/7 remove
class RecyclerHeaderSpacer(context: Context) : ComponentPaddingCell(context) {

    var height = modulon.UI.SPACING.dp
        @JvmName("getHeightKt") get
        set(value) {
            field = value
            requestLayout()
        }

    init {
        setPadding(0, 0, 0, 0)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
        setMeasuredDimension(measuredWidth, height)
    }

}