package modulon.dialog

import kotlinx.coroutines.launch
import modulon.R
import modulon.extensions.viewbinder.createRecyclerLayout
import modulon.extensions.view.doOnClick
import modulon.layout.lazy.LazyListView
import modulon.layout.lazy.section
import modulon.layout.lazy.section.HeaderSectionImpl
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

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

// TODO: 2022/4/26 remove
fun BottomDialogFragment.updateButton(index: Int, block: BottomDialogFragment.TextButton.() -> Unit) {
    (buttons[index] as BottomDialogFragment.TextButton).apply(block)
}

fun BottomDialogFragment.waitFor(block: suspend BottomDialogFragment.() -> Unit) {
    lifecycleScope.launch {
        hide()
        block.invoke(this@waitFor)
        show()
    }
}

// TODO: 27/1/2022 normalize
@OptIn(ExperimentalContracts::class)
fun BottomDialogFragment.section(block: HeaderSectionImpl.() -> Unit = {}) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    customView = createRecyclerLayout {
//        edgeEffectFactory = EmptyEdgeEffectFactory()
        // FIXME: 2022/2/22 remove
        section(block)
        section()
    }
}
fun BottomDialogFragment.setupRecyclerLayout(block: LazyListView.() -> Unit = {}) {
    customView = createRecyclerLayout(block)
}
