package modulon.component

import android.content.Context
import androidx.core.view.updatePadding
import modulon.R
import modulon.extensions.view.backgroundSelectorColor

open class ComponentPaddingCell(context: Context) : BaseCell(context) {

    init {
        updatePadding(
            context.resources.getDimensionPixelSize(R.dimen.cell_padding_start),
            context.resources.getDimensionPixelSize(R.dimen.cell_padding_top),
            context.resources.getDimensionPixelSize(R.dimen.cell_padding_end),
            context.resources.getDimensionPixelSize(R.dimen.cell_padding_bottom),
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
    }


}
