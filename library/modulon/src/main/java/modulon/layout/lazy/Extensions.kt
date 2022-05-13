package modulon.layout.lazy

import android.view.View
import modulon.component.cell.ComponentCell
import modulon.extensions.view.create
import modulon.layout.lazy.containers.Comparator
import modulon.layout.lazy.containers.SectionListContainer
import modulon.layout.lazy.section.HeaderSectionImpl
import modulon.layout.lazy.section.Section

// List Container
// TODO: 2022/2/28 replace with Section.cells()
inline fun <reified C : View, D : Any> Section.list(block: SectionListContainer<C, D>.() -> Unit = {}) {
    addContainer(SectionListContainer<C, D> { create() }.apply(block))
}


fun <C : View, D> SectionListContainer<C, D>.construct(block: C.() -> Unit = {}) {
    setViewCreator(block)
}
fun <C : View, D> SectionListContainer<C, D>.data(block: C.(D) -> Unit = {}) {
    setDataBinder(block)
}
fun <C : View, D> SectionListContainer<C, D>.payload(block: C.(data: D, payload: Any) -> Unit) {
    setPayloadBinder(block)
}
fun <C : View, D, R> SectionListContainer<C, D>.distinctContentBy(transform: (D) -> R) {
    setContentComparator { d1, d2 ->
        transform(d1) == transform(d2)
    }
}
fun <C : View, D, R> SectionListContainer<C, D>.distinctItemsBy(transform: (D) -> R) {
    setItemComparator { d1, d2 ->
        transform(d1) == transform(d2)
    }
}


inline fun Section.cells(block: SectionListContainer<ComponentCell, Any>.() -> Unit = {}) {
    addContainer(SectionListContainer<ComponentCell, Any> { ComponentCell(context) }.apply(block))
}


// TODO: 5/2/2022 rename
fun <C : View, D, R> SectionListContainer<C, D>.compareContent(transform: (D) -> R) {
    setContentComparator { d1, d2 ->
        transform(d1) == transform(d2)
    }
}
fun <C : View, D, R> SectionListContainer<C, D>.compareItems(transform: (D) -> R) {
    setItemComparator { d1, d2 ->
        transform(d1) == transform(d2)
    }
}
fun <C : View, D> SectionListContainer<C, D>.areContentsSame(comparator: Comparator<D>) {
    setContentComparator(comparator)
}

fun <C : View, D> SectionListContainer<C, D>.areItemsSame(comparator: Comparator<D>) {
    setItemComparator(comparator)
}

// Section
inline fun LazyListView.section(block: HeaderSectionImpl.() -> Unit = {}) {
    val section = HeaderSectionImpl(context).apply(block)
    adapter.addAdapter(section.adapter)
}