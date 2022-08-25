package modulon.extensions.coroutine

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

private const val DEFAULT_TIMEOUT = 500L

// FIXME: 3/11/2021 fix throttleLatest and throttleLast

fun <T> throttleFirst(coroutineScope: CoroutineScope, skip: Long = DEFAULT_TIMEOUT,  destinationFunction: suspend (T) -> Unit): (T) -> Unit {
    var throttleJob: Job? = null
    return { param: T ->
        if (throttleJob?.isCompleted != false) {
            throttleJob = coroutineScope.launch {
                destinationFunction(param)
                delay(skip)
            }
        }
    }
}

fun throttleFirst(coroutineScope: CoroutineScope, skip: Long = DEFAULT_TIMEOUT, destinationFunction: () -> Unit): () -> Unit {
    var throttleJob: Job? = null
    return {
        if (throttleJob?.isCompleted != false) {
            throttleJob = coroutineScope.launch {
                destinationFunction()
                delay(skip)
            }
        }
    }
}

//fun <T> throttleLast(coroutineScope: CoroutineScope, skip: Long = DEFAULT_TIMEOUT, destinationFunction: (T) -> Unit): (T) -> Unit {
//    var throttleJob: Job? = null
//    return { param: T ->
//        if (throttleJob?.isCompleted != false) {
//            throttleJob = coroutineScope.launch {
//                delay(skip)
//                destinationFunction(param)
//            }
//        }
//    }
//}
//
//fun throttleLast(coroutineScope: CoroutineScope, skip: Long = DEFAULT_TIMEOUT, destinationFunction: () -> Unit): () -> Unit {
//    var throttleJob: Job? = null
//    return {
//        if (throttleJob?.isCompleted != false) {
//            throttleJob = coroutineScope.launch {
//                delay(skip)
//                destinationFunction()
//            }
//        }
//    }
//}

fun <T> throttleLatest(coroutineScope: CoroutineScope, interval: Long = DEFAULT_TIMEOUT, destinationFunction: (T) -> Unit): (T) -> Unit {
    var throttleJob: Job? = null
    var latestParam: T
    return { param: T ->
        latestParam = param
        if (throttleJob?.isCompleted != false) {
            throttleJob = coroutineScope.launch {
                delay(interval)
                latestParam.let(destinationFunction)
            }
        }
    }
}


fun throttleLatest(coroutineScope: CoroutineScope, interval: Long = DEFAULT_TIMEOUT, destinationFunction: () -> Unit): () -> Unit {
    var throttleJob: Job? = null
    return {
        if (throttleJob?.isCompleted != false) {
            throttleJob = coroutineScope.launch {
                delay(interval)
                destinationFunction()
            }
        }
    }
}


fun <T> debounce(coroutineScope: CoroutineScope, wait: Long = DEFAULT_TIMEOUT, destinationFunction: (T) -> Unit): (T) -> Unit {
    var debounceJob: Job? = null
    return { param: T ->
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            withContext(Dispatchers.Default) {
                delay(wait)
                coroutineScope.launch {
                    destinationFunction(param)
                }
            }
        }
    }
}


//fun <T, E> debounce(coroutineScope: CoroutineScope, wait: Long = DEFAULT_TIMEOUT, destinationFunction: (T, E) -> Unit): (T, E) -> Unit {
//    var debounceJob: Job? = null
//    return { param1: T, param2: E ->
//        debounceJob?.cancel()
//        debounceJob = coroutineScope.launch {
//            withContext(Dispatchers.Default) {
//                delay(wait)
//                coroutineScope.launch {
//                    destinationFunction(param1, param2)
//                }
//            }
//        }
//    }
//}


fun debounce(coroutineScope: CoroutineScope, wait: Long = DEFAULT_TIMEOUT, destinationFunction: () -> Unit): () -> Unit {
    var debounceJob: Job? = null
    return {
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            withContext(Dispatchers.Default) {
                delay(wait)
                coroutineScope.launch {
                    destinationFunction()
                }
            }
        }
    }
}


suspend inline fun <T, R> Iterable<T>.mapParallel(context: CoroutineContext = Dispatchers.IO, crossinline transform: suspend (T) -> R): List<R> = coroutineScope {
    map { async(context) { transform(it) } }.awaitAll()
}




