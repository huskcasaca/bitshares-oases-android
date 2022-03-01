package modulon.layout.recycler.manager

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FixedLinearLayoutManager(context: Context) : LinearLayoutManager(context) {
    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try { super.onLayoutChildren(recycler, state) } catch (e: Throwable) { }
    }

    override fun onAdapterChanged(oldAdapter: RecyclerView.Adapter<*>?, newAdapter: RecyclerView.Adapter<*>?) {
        super.onAdapterChanged(oldAdapter, newAdapter)
    }

}

//class CustomLayoutManager(context: Context) : LinearLayoutManager(context) {
//    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
//        try { super.onLayoutChildren(recycler, state) } catch (e: Throwable) { }
//    }
//
//    override fun getChildAt(index: Int): View? {
//        return super.getChildAt(index)
//    }
//
//}