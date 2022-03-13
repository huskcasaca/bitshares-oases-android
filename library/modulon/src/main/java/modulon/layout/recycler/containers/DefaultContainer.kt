package modulon.layout.recycler.containers

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import modulon.extensions.view.addDefaultFill
import modulon.extensions.view.addDefaultRow
import modulon.extensions.view.create
import modulon.extensions.view.parentViewGroupOrNull
import modulon.layout.recycler.ViewSize
import modulon.layout.recycler.RecyclerLayout

class DefaultContainer(private val view: View, private val fill: ViewSize, private val params: ViewGroup.LayoutParams? = null) : RecyclerLayout.Container<View>() {

    init {
        if (params != null) view.layoutParams = params
    }

    override var creator: () -> View = { view }

    override val adapter = object : RecyclerView.Adapter<FrameViewHolder>() {
        override fun onBindViewHolder(holder: FrameViewHolder, position: Int) {}
        override fun onBindViewHolder(holder: FrameViewHolder, position: Int, dropped: MutableList<Any>) {}
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FrameViewHolder {
            return FrameViewHolder(parent.context, fill).apply {
                view.parentViewGroupOrNull?.removeView(view)
                when (fill) {
                    ViewSize.ROW -> container.addDefaultRow(view)
                    ViewSize.FILL -> container.addDefaultFill(view)
                }
            }
        }
        override fun getItemCount() = 1
    }
}


inline fun <reified V: View> RecyclerLayout.addViewDec(block: V.() -> Unit = {} ) {
    addContainer(DefaultContainer(create(block), ViewSize.ROW))
}
