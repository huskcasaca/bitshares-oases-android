package modulon.extensions.livedata

import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

inline fun <T> mediatorLiveData(block: MediatorLiveData<T>.() -> Unit) = MediatorLiveData<T>().apply(block)
inline fun <T> MutableLiveData<out T>.emitNonNull(value: T?) { if (value != null) this.value = value }
inline fun <T> MutableLiveData<out T>.emit(value: T?) { this.value = value }
suspend inline fun <T> MutableLiveData<out T>.emitNow(value: T?) { withContext(Dispatchers.Main.immediate) { this@emitNow.value = value } }

fun <X> LiveData<X>.sources(vararg liveData: LiveData<*>): LiveData<X> {
    return MediatorLiveData<X>().apply {
        liveData.forEach {
            addSource(it) { }
        }
        addSource(this@sources) { value = it }
    }
}

fun <X> LiveData<X>.sources(liveData: () -> LiveData<*>): LiveData<X> = sources(liveData.invoke())

inline fun <T> LiveData<T>.afterEmit(crossinline afterEmit: (T) -> Unit): LiveData<T> = mediatorLiveData {
    addSource(this@afterEmit) {
        emit(it)
        afterEmit(it)
    }
}
inline fun <T> LiveData<T>.beforeEmit(crossinline beforeEmit: (T) -> Unit): LiveData<T> = mediatorLiveData {
    addSource(this@beforeEmit) {
        beforeEmit(it)
        emit(it)
    }
}

fun <T> LiveData<T>.toMutableLiveData(): MutableLiveData<T> = mediatorLiveData { addSource(this@toMutableLiveData){ emit(it) } }

fun <T, R> LiveData<out T>.map(scope: CoroutineScope, runContext: CoroutineContext = Dispatchers.IO, transformation: suspend (T) -> R): LiveData<R> = mediatorLiveData {
    var last: Any?
    addSource(this@map) {
        last = it
        scope.launch(runContext) {
            val transformed = transformation(it)
            withContext(Dispatchers.Main) { if (last == it) value = transformed }
        }
    }
}


fun <X, R> LiveData<out X>.switchMap(scope: CoroutineScope, runContext: CoroutineContext = Dispatchers.IO, transformation: suspend (X) -> LiveData<R>): LiveData<R> {
    return switchMap {
        MediatorLiveData<R>().apply {
            scope.launch(runContext) {
                val transformed = transformation(it)
                withContext(Dispatchers.Main) { addSource(transformed) { value = it } }
            }
        }
    }
}



fun <X> LiveData<out X?>.withDefault(block: () -> X): LiveDataWithDefault<X> {
    return LiveDataWithDefault(block.invoke()).apply {
        addSource(this@withDefault) { if (it != null) value = it }
    }
}



private const val DEFAULT_TIMEOUT = 5000L

fun <X, R> LiveData<out X>.mapSuspend(context: CoroutineContext = Dispatchers.IO, timeoutInMs: Long = DEFAULT_TIMEOUT, transformation: suspend (X) -> R): LiveData<R> {
    return switchMap {
        liveData<R>(context, timeoutInMs) {
            emit(transformation.invoke(it))
        }
    }
}

fun <X> LiveData<X>.transform(scope: CoroutineScope, runContext: CoroutineContext = Dispatchers.IO, transformation: suspend X.() -> Unit): LiveData<X> {
    return MediatorLiveData<X>().apply {
        addSource(this@transform) {
            scope.launch(runContext) {
                val transformed = it.apply { transformation.invoke(this) }
                withContext(Dispatchers.Main) { value = transformed }
            }
        }
    }
}


fun <X> LiveData<X?>.transform(transformation: X.() -> Unit): LiveData<X?> {
    return MediatorLiveData<X>().apply {
        addSource(this@transform) {
            value = it?.apply(transformation)
        }
    }
}


suspend inline fun <T> Iterable<T>.onEachParallel(context: CoroutineContext = Dispatchers.IO, crossinline transform: suspend T.() -> Unit): List<T> = coroutineScope {
    map { async(context) { it.apply { transform.invoke(it) } } }.awaitAll()
}


fun <A : Iterable<X>, X> LiveData<A>.onEachChildParallel(scope: CoroutineScope, runContext: CoroutineContext = Dispatchers.IO, transformation: suspend X.() -> Unit): LiveData<List<X>> {
    return MediatorLiveData<List<X>>().apply {
        addSource(this@onEachChildParallel) {
            scope.launch(runContext) {
                val transformed = it?.onEachParallel(runContext, transformation).orEmpty()
                emitNow(transformed)
            }
        }
    }
}


fun MutableLiveData<Boolean>.invert() {
    value?.let { value = !it }
}



