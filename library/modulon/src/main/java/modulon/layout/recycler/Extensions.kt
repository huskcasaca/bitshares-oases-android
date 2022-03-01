package modulon.layout.recycler

import android.view.View
import androidx.recyclerview.widget.ConcatAdapter
import modulon.component.ComponentCell
import modulon.extensions.view.create
import modulon.layout.recycler.containers.Comparator
import modulon.layout.recycler.containers.ExpandableContainer
import modulon.layout.recycler.containers.ListContainer
import modulon.layout.recycler.containers.SpacerContainer
import modulon.layout.recycler.section.HeaderSectionDelegate
import modulon.layout.recycler.section.RecyclerContentLocator
import modulon.layout.recycler.section.Section

// Expandable Container
// FIXME: 20/1/2022 may disappear
inline fun <reified C : View> Section.expandable(block: ExpandableContainer<C>.() -> Unit = {}) {
    addContainer(ExpandableContainer<C> { create() }.apply(block))
}
fun <C : View> ExpandableContainer<C>.construct(block: C.() -> Unit = {}) {
    setViewCreator(block)
}

// List Container
// TODO: 2022/2/28 replace with Section.cells()
inline fun <reified C : View, D : Any> Section.list(block: ListContainer<C, D>.() -> Unit = {}) {
    addContainer(ListContainer<C, D> { create() }.apply(block))
}


fun <C : View, D> ListContainer<C, D>.construct(block: C.() -> Unit = {}) {
    setViewCreator(block)
}
fun <C : View, D> ListContainer<C, D>.data(block: C.(D) -> Unit = {}) {
    setDataBinder(block)
}
fun <C : View, D> ListContainer<C, D>.payload(block: C.(data: D, payload: Any) -> Unit) {
    setPayloadBinder(block)
}
fun <C : View, D, R> ListContainer<C, D>.distinctContentBy(transform: (D) -> R) {
    setContentComparator { d1, d2 ->
        transform(d1) == transform(d2)
    }
}
fun <C : View, D, R> ListContainer<C, D>.distinctItemsBy(transform: (D) -> R) {
    setItemComparator { d1, d2 ->
        transform(d1) == transform(d2)
    }
}


inline fun Section.cells(block: ListContainer<ComponentCell, Any>.() -> Unit = {}) {
    addContainer(ListContainer<ComponentCell, Any> { create() }.apply(block))
}


// TODO: 5/2/2022 rename
fun <C : View, D, R> ListContainer<C, D>.compareContent(transform: (D) -> R) {
    setContentComparator { d1, d2 ->
        transform(d1) == transform(d2)
    }
}
fun <C : View, D, R> ListContainer<C, D>.compareItems(transform: (D) -> R) {
    setItemComparator { d1, d2 ->
        transform(d1) == transform(d2)
    }
}
fun <C : View, D> ListContainer<C, D>.areContentsSame(comparator: Comparator<D>) {
    setContentComparator(comparator)
}

fun <C : View, D> ListContainer<C, D>.areItemsSame(comparator: Comparator<D>) {
    setItemComparator(comparator)
}

// Spacer Container
internal fun Section.locator(type: RecyclerContentLocator.SpacerType) {
    addContainer(SpacerContainer(type))
}

// Single Container


// Section
fun RecyclerLayout.section(block: HeaderSectionDelegate.() -> Unit = {}) {
    val section = HeaderSectionDelegate(context).apply(block)
    (adapter as ConcatAdapter).addAdapter(section.adapter)
}