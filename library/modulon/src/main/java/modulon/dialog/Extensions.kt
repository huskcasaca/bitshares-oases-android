package modulon.dialog

import kotlinx.coroutines.launch
import modulon.R
import modulon.extensions.viewbinder.createRecyclerLayout
import modulon.extensions.view.doOnClick
import modulon.layout.recycler.RecyclerLayout
import modulon.layout.recycler.section
import modulon.layout.recycler.section.HeaderSectionDelegate

fun BottomDialogFragment.doOnViewCreated(block: OnViewCreatedListener) = setOnViewCreatedListener(block)
fun BottomDialogFragment.doOnDismiss(block: OnViewCreatedListener) = setOnDismissListener(block)
fun BottomDialogFragment.doOnShow(block: OnShowListener) = setOnShowListener(block)

fun BottomDialogFragment.button(block: BottomDialogFragment.TextButton.() -> Unit) {
    addButton(TextButton(context).apply(block))
}

fun BottomDialogFragment.button(index: Int, block: BottomDialogFragment.TextButton.() -> Unit) {
    addButton(TextButton(context).apply(block), index)
}
fun BottomDialogFragment.buttonCancel() = button {
    text = context.getString(R.string.button_cancel)
    doOnClick { dismiss() }
}

fun BottomDialogFragment.updateButton(index: Int, block: BottomDialogFragment.TextButton.() -> Unit) {
    setButton(block, index)
}

fun BottomDialogFragment.waitFor(block: suspend BottomDialogFragment.() -> Unit) {
    lifecycleScope.launch {
        hide()
        block.invoke(this@waitFor)
        show()
    }
}

// TODO: 27/1/2022 normalize
fun BottomDialogFragment.section(block: HeaderSectionDelegate.() -> Unit = {}) {
    customView = createRecyclerLayout {
//        edgeEffectFactory = EmptyEdgeEffectFactory()
        // FIXME: 2022/2/22 remove
        section(block)
        section()
    }
}
fun BottomDialogFragment.setupRecyclerLayout(block: RecyclerLayout.() -> Unit = {}) {
    customView = createRecyclerLayout(block)
}
