package modulon.extensions.view

import android.view.View
import androidx.annotation.Px
import androidx.core.view.updatePadding
import modulon.component.ComponentPaddingCell

//fun View.stripReset() {
//    updatePadding(top = 14.dp)
//    updatePadding(bottom = 14.dp)
//}

// TODO: 2022/2/28 add updatePadding and replace setPadding


fun View.updatePaddingStart(@Px start: Int = paddingStart) {
    setPadding(start, paddingTop, paddingEnd, paddingBottom)
}
fun View.updatePaddingTop(@Px top: Int = paddingTop) {
    setPadding(paddingStart, top, paddingEnd, paddingBottom)
}
fun View.updatePaddingEnd(@Px end: Int = paddingEnd) {
    setPadding(paddingStart, paddingTop, end, paddingBottom)
}
fun View.updatePaddingBottom(@Px bottom: Int = paddingBottom) {
    setPadding(paddingStart, paddingTop, paddingEnd, bottom)
}
// TODO: 25/1/2022 override inline fun updatePadding
//fun View.updatePadding(
//    @Px start: Int = paddingStart,
//    @Px top: Int = paddingTop,
//    @Px end: Int = paddingEnd,
//    @Px bottom: Int = paddingBottom
//) {
//    setPadding(start, top, end, bottom)
//}

// TODO: 2022/2/15 remove
fun ComponentPaddingCell.updatePaddingVerticalHalf() {
//    updatePadding(top = 10.dp)
//    updatePadding(bottom = 10.dp)
}
// TODO: 2022/2/15 remove
fun ComponentPaddingCell.updatePaddingVerticalV6() {
    updatePadding(top = 8.dp)
    updatePadding(bottom = 8.dp)
}
fun ComponentPaddingCell.updatePaddingVertical4() {
    updatePadding(top = 4.dp)
    updatePadding(bottom = 4.dp)
}