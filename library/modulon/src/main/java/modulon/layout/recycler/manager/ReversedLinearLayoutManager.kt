package modulon.layout.recycler.manager

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: 14/12/2021 extract to extension
class ReversedLinearLayoutManager(context: Context) : LinearLayoutManager(context) {
    init {
        stackFromEnd = true
        reverseLayout = true
    }
    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try { super.onLayoutChildren(recycler, state) } catch (e: Throwable) { }
    }
}