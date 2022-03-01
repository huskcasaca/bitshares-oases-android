package modulon.extensions.coroutine

import kotlin.coroutines.Continuation

// TODO: 2022/2/14 remove
fun <T> Continuation<T>.resumeSafe(value: T) {
    runCatching { resumeWith(Result.success(value)) }
}

fun <T> Continuation<T>.resumeWithExceptionSafe(exception: Throwable) {
    runCatching { resumeWith(Result.failure(exception)) }
}