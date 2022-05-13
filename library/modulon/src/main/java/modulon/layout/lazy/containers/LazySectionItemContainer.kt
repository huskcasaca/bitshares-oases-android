package modulon.layout.lazy.containers

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import modulon.extensions.stdlib.logcat
import modulon.layout.lazy.LazyListView


class LazySectionItemContainer(
    private val viewCreator: () -> View,
    private val isGrouped: Boolean = false
) : LazyListView.Container<View>() {

    override var creator: () -> View = viewCreator
    private val view by lazy(viewCreator)
    var isCreated = false

    override val adapter =
        object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                if (isCreated) return
                isCreated
                when (holder) {
                    is ItemSetMarginHolder -> {
                        (view.parent as? ViewGroup)?.removeView(view)
                        holder.replace(view).logcat()
                    }
                    is ItemSigMarginHolder -> {
                        (view.parent as? ViewGroup)?.removeView(view)
                        holder.replace(view).logcat()
                    }

                }

            }
//            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, dropped: MutableList<Any>) {}
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
                if (isGrouped)
                    ItemSetMarginHolder(parent.context)
                else
                    ItemSigMarginHolder(parent.context)
            override fun getItemCount() = 1
        }
}