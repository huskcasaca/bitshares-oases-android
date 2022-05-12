package modulon.layout.lazy.containers

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import modulon.layout.lazy.LazyListView


class RawItemContainer(
    private val view: View,
    private val params: ViewGroup.LayoutParams? = null,
) : LazyListView.Container<View>() {

    init {
        if (params != null) {
            view.layoutParams = params
        }
    }
    override var creator: () -> View = { view }
    override val adapter =
        object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, dropped: MutableList<Any>) {}
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = ItemHolder(view)
            override fun getItemCount() = 1
        }
}