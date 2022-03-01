package modulon.extensions.compat

import modulon.dialog.BottomDialogFragment
import modulon.dialog.SuspendedBottomDialogFragment
import modulon.dialog.doOnViewCreated
import modulon.union.Union
import kotlin.coroutines.suspendCoroutine

inline fun Union.buildBottomDialog(crossinline block: BottomDialogFragment.() -> Unit): BottomDialogFragment {
    return BottomDialogFragment().apply {
        doOnViewCreated { block.invoke(this) }
    }
}

inline fun Union.showBottomDialog(crossinline block: BottomDialogFragment.() -> Unit): BottomDialogFragment {
    return buildBottomDialog(block).apply {
        runCatching { show(this@showBottomDialog.parentFragmentManager) }
    }
}


// TODO: 11/12/2021 make show() suspend
suspend inline fun <T> Union.showSuspendedBottomDialog(crossinline block: SuspendedBottomDialogFragment<T>.() -> Unit): T {
    return suspendCoroutine {
        SuspendedBottomDialogFragment<T>().apply {
            setContinuation(it)
            doOnViewCreated { block.invoke(this) }
            runCatching { show(this@showSuspendedBottomDialog.parentFragmentManager) }
        }
    }
}

suspend inline fun Union.showBooleanSuspendedBottomDialog(crossinline block: SuspendedBottomDialogFragment<Boolean>.() -> Unit): Boolean = showSuspendedBottomDialog(block)



