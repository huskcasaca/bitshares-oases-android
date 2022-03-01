package modulon.layout.recycler.section

import modulon.layout.recycler.RecyclerLayout
import modulon.union.UnionContext

interface Section: UnionContext {
    fun addContainer(block: RecyclerLayout.Container<*>)
    fun addContainer(block: RecyclerLayout.Container<*>, index: Int)
}