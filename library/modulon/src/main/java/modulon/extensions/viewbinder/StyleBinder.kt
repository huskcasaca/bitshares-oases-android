package modulon.extensions.viewbinder

import android.graphics.drawable.GradientDrawable
import modulon.R
import modulon.component.ComponentPaddingCell
import modulon.extensions.font.typefaceBold
import modulon.extensions.view.dp
import modulon.extensions.view.updatePaddingBottom
import modulon.extensions.view.updatePaddingTop

fun ComponentPaddingCell.headerCellStyle() {
    updatePaddingTop(8.dp)
    updatePaddingBottom(6.dp)
    titleView.apply {
        textColor = R.color.cell_text_secondary.contextColor()
        textSize = 14.5f
        typeface = typefaceBold
        isAllCaps = true
    }
    background = GradientDrawable(GradientDrawable.Orientation.TR_BL, intArrayOf(R.color.background_component.contextColor(), R.color.background_dark.contextColor()))
}