package modulon.component.cell

import android.content.Context
import androidx.core.view.updatePadding
import modulon.R
import modulon.extensions.view.backgroundSelectorColor
import modulon.extensions.view.dp
import modulon.extensions.view.isSmallScreenCompat

open class ComponentPaddingCell(context: Context) : BaseCell(context) {

    init {
        if (context.isSmallScreenCompat) {
            updatePadding(
                24.dp,
                context.resources.getDimensionPixelSize(R.dimen.cell_padding_top),
                24.dp,
                context.resources.getDimensionPixelSize(R.dimen.cell_padding_bottom),
            )
        } else {
            updatePadding(
                context.resources.getDimensionPixelSize(R.dimen.cell_padding_start),
                context.resources.getDimensionPixelSize(R.dimen.cell_padding_top),
                context.resources.getDimensionPixelSize(R.dimen.cell_padding_end),
                context.resources.getDimensionPixelSize(R.dimen.cell_padding_bottom),
            )
        }
    }

}
