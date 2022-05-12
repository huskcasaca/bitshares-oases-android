package modulon.layout.lazy.containers

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import modulon.layout.lazy.LazyListView

class SectionListContainer<C : View, D>(override var creator: () -> C) : LazyListView.Container<C>() {

    private var viewBinder: (C) -> Unit = { }
    private var dataBinder: C.(D) -> Unit = { }
    private var payloadBinder: C.(data: D, payload: Any) -> Unit = { _, _ -> }

    private var itemsComparator: (D, D) -> Boolean = { d1, d2 -> d1 == d2 }
    private var contentsComparator: (D, D) -> Boolean = { d1, d2 -> false }

    private var currentPayload: Any? = null

    private val diffCallback = object : DiffUtil.ItemCallback<D>() {
        override fun areItemsTheSame(oldItem: D, newItem: D): Boolean = itemsComparator.invoke(oldItem, newItem)
        override fun areContentsTheSame(oldItem: D, newItem: D): Boolean = contentsComparator.invoke(oldItem, newItem)
    }

    inner class GroupItemMarginHolder<C : View>(val childView: C) : ItemSetMarginHolder(childView.context) {

        init {
            replace(childView)
        }
    }

    inner class PayloadsAdapter : ListAdapter<D, GroupItemMarginHolder<C>>(diffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GroupItemMarginHolder(creator.invoke().apply(viewBinder))
        @SuppressLint("PendingBindings")
        override fun onBindViewHolder(holder: GroupItemMarginHolder<C>, position: Int) {
            val data = currentList[position]
            val payload = currentPayload

            dataBinder.invoke(holder.childView, data)
            if (payload != null) payloadBinder.invoke(holder.childView, data, payload)
        }
        override fun onBindViewHolder(holder: GroupItemMarginHolder<C>, position: Int, payloads: MutableList<Any>) {
            if (payloads.isEmpty()) {
                onBindViewHolder(holder, position)
            } else {
                currentPayload?.let { payloadBinder.invoke(holder.childView, currentList[position], it) }
            }
        }

        fun submitPayload(payload: Any, notify: Boolean = true) {
            currentPayload = payload
            if (notify) notifyItemRangeChanged(0, currentList.size, LazyListView.EMPTY_PAYLOAD)
        }

        fun notifyPayloadChanged(item: D) {
            notifyItemChanged(currentList.indexOf(item), LazyListView.EMPTY_PAYLOAD)
        }
    }

    override val adapter = PayloadsAdapter()

    fun submitList(list: List<D>) {
        adapter.submitList(list)
    }
    fun submitPayload(payload: Any, notify: Boolean = true) {
        adapter.submitPayload(payload, notify)
    }

    fun setViewCreator(block: C.() -> Unit) {
        viewBinder = block
        adapter.notifyDataSetChanged()
    }

    fun setDataBinder(block: C.(D) -> Unit) {
        dataBinder = block
        adapter.notifyDataSetChanged()
    }

    fun setPayloadBinder(block: C.(data: D, payload: Any) -> Unit) {
        payloadBinder = block
        adapter.notifyDataSetChanged()
    }

    fun setContentComparator(comparator: Comparator<D>) {
        contentsComparator = comparator
    }

    fun setItemComparator(comparator: Comparator<D>) {
        itemsComparator = comparator
    }

}

typealias Comparator<D> = (old: D, new: D) -> Boolean


class StaticContainer<C : View, D>(private val data: List<D>, override var creator: () -> C, private val binder: C.(D) -> Unit) : LazyListView.Container<C>() {

    inner class GroupItemHolder<C : View>(val childView: C) : RecyclerView.ViewHolder(childView)

    inner class PayloadsAdapter : RecyclerView.Adapter<GroupItemHolder<C>>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GroupItemHolder(creator.invoke())
        override fun onBindViewHolder(holder: GroupItemHolder<C>, position: Int) {
            binder.invoke(holder.childView, data[position])
        }
        override fun onBindViewHolder(holder: GroupItemHolder<C>, position: Int, payloads: MutableList<Any>) {
            binder.invoke(holder.childView, data[position])
        }

        override fun getItemCount(): Int = data.size

    }

    override val adapter = PayloadsAdapter().apply {
        // TODO: 2022/2/7 remove
        notifyDataSetChanged()
    }

}