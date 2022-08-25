package modulon.layout.lazy.section

import modulon.layout.lazy.LazyListView
import modulon.union.UnionContext

interface Section: UnionContext {
    fun addContainer(block: LazyListView.Container<*>)
    fun addContainer(block: LazyListView.Container<*>, index: Int)
}