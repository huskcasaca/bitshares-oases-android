package modulon.layout.recycler.containers

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import modulon.extensions.view.addDefaultRow
import modulon.extensions.view.parentViewGroupOrNull
import modulon.layout.recycler.ViewSize
import modulon.layout.recycler.RecyclerLayout
import modulon.extensions.coroutine.*

class ExpandableContainer<C : View>(override var creator: () -> C) : RecyclerLayout.Container<C>() {

    private val view: C = creator.invoke()

    override val adapter = object : RecyclerView.Adapter<FrameViewHolder>() {
        override fun onBindViewHolder(holder: FrameViewHolder, position: Int) {}
        override fun onBindViewHolder(holder: FrameViewHolder, position: Int, dropped: MutableList<Any>) {}
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FrameViewHolder {
            return FrameViewHolder(parent.context, ViewSize.ROW).apply {
                view.parentViewGroupOrNull?.removeView(view)
                container.addDefaultRow(view)
            }
        }
        override fun getItemCount() = if (isExpanded) 1 else 0
    }

    // TODO: 2022/2/11 throttleFirst
    var isExpanded = true
        set(value) {
            if (value) {
                if (adapter.itemCount == 0) adapter.notifyItemInserted(0)
            } else {
                if (adapter.itemCount == 1) adapter.notifyItemRemoved(0)
            }
            field = value
        }

    private val setter = throttleFirst(CoroutineScope(Dispatchers.Main)) { it: Boolean ->
        _isExpanded = it
    }

    private var _isExpanded = true
        set(value) {
            field = value
            setter.invoke(value)
        }

    fun setViewCreator(block: C.() -> Unit) {
        view.apply(block)
        adapter.notifyDataSetChanged()
    }

    fun expandView() {
        isExpanded = true
    }

    fun collapseView() {
        isExpanded = false
    }

}
