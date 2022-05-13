package modulon.layout.lazy.containers

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import modulon.layout.lazy.LazyListView

//class ExpandableContainer<C : View>(override var creator: () -> C) : LazyListView.Container<C>() {
//
//    private val view: C = creator.invoke()
//
//    override val adapter = object : RecyclerView.Adapter<ItemSetMarginHolder>() {
//        override fun onBindViewHolder(holder: ItemSetMarginHolder, position: Int) {}
//        override fun onBindViewHolder(holder: ItemSetMarginHolder, position: Int, dropped: MutableList<Any>) {}
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSetMarginHolder {
//            return ItemSetMarginHolder(parent.context).apply {
//                (view.parent as? ViewGroup)?.removeView(view)
//                replace(view)
//            }
//        }
//        override fun getItemCount() = if (isExpanded) 1 else 0
//    }
//
//    // TODO: 2022/2/11 throttleFirst
//    var isExpanded = true
//        set(value) {
//            if (value) {
//                if (adapter.itemCount == 0) adapter.notifyItemInserted(0)
//            } else {
//                if (adapter.itemCount == 1) adapter.notifyItemRemoved(0)
//            }
//            field = value
//        }
//
//    fun setViewCreator(block: C.() -> Unit) {
//        view.apply(block)
//        adapter.notifyDataSetChanged()
//    }
//}
