package modulon.layout.recycler.containers

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import modulon.layout.recycler.RecyclerLayout
import modulon.layout.recycler.section.RecyclerContentLocator

class SpacerContainer(val type: RecyclerContentLocator.SpacerType) : RecyclerLayout.Container<View>() {
    override val adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, dropped: MutableList<Any>) {}
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(
                RecyclerContentLocator(parent.context).apply {
                    type = this@SpacerContainer.type
                }
            ) {  }
        }
        override fun getItemCount() = 1
    }
}