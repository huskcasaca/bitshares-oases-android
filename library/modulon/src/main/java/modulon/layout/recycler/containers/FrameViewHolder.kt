package modulon.layout.recycler.containers

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import modulon.extensions.view.setParamsFill
import modulon.extensions.view.setParamsRow
import modulon.extensions.viewbinder.noClipping
import modulon.layout.frame.FrameLayout
import modulon.layout.recycler.ViewSize

class FrameHolderLayout(context: Context) : FrameLayout(context) {

    init {
        noClipping()
    }

    val child: View? get() = getChildAt(0)

    fun replace(view: View) {
        removeAllViews()
        addView(view)
    }
}

class FrameViewHolder(context: Context, fill: ViewSize = ViewSize.ROW) : RecyclerView.ViewHolder(
    FrameHolderLayout(context).apply {

        when (fill) {
            ViewSize.ROW -> setParamsRow()
            ViewSize.FILL -> setParamsFill()
        }
    }
) {
    val container get() = itemView as FrameHolderLayout
}