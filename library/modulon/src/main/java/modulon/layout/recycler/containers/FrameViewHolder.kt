package modulon.layout.recycler.containers

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import modulon.extensions.view.*
import modulon.extensions.viewbinder.noClipping
import modulon.layout.frame.FrameLayout
import modulon.layout.recycler.ViewSize

class HolderView(context: Context) : FrameLayout(context) {
    init {
        noClipping()
        layoutParams = RecyclerView.LayoutParams(
            WRAP_CONTENT, WRAP_CONTENT
        )
    }
    val child: View? get() = getChildAt(0)

    fun replace(view: View) {
        removeAllViews()
        addView(view)
    }
}

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

// TODO: 2022/4/26
class FrameViewHolder(context: Context) : RecyclerView.ViewHolder(
    FrameHolderLayout(context).apply {
//        when (fill) {
//            ViewSize.ROW -> {
//                layoutParams = viewParams(MATCH_PARENT, WRAP_CONTENT)
//            }
//            ViewSize.FILL -> {
//                layoutParams = viewParams(MATCH_PARENT, MATCH_PARENT)
//            }
//        }
    }
) {

    val container get() = itemView as FrameHolderLayout


}