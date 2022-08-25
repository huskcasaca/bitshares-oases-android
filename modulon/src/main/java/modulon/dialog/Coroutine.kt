package modulon.dialog

import modulon.extensions.coroutine.resumeSafe
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext

class SuspendedBottomDialogFragment<T> : BottomDialogFragment() {

    var localContinuation: Continuation<T> = object : Continuation<T> {
        override val context: CoroutineContext get() = TODO()
        override fun resumeWith(result: Result<T>) = Unit
    }

    fun setContinuation(cont: Continuation<T>) {
        localContinuation = cont
    }

}

// TODO: 2022/2/19 rename
fun <T> SuspendedBottomDialogFragment<T>.dismissWith(value: T) {
    localContinuation.resumeSafe(value)
    dismiss()
}

// TODO: 2022/2/19 rename
fun <T> SuspendedBottomDialogFragment<T>.resumeWith(value: T) {
    localContinuation.resumeSafe(value)
}