package modulon.layout.recycler.section

import android.content.Context
import android.view.View
import modulon.extensions.view.dp

class RecyclerContentLocator(context: Context) : View(context) {

    enum class SpacerType {
        TOP, BOTTOM
    }

    var height = 0.dp
        @JvmName("getHeightKt") get
        set(value) {
            field = value
            requestLayout()
        }

    var type = SpacerType.TOP

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
    }
}