package modulon.extensions.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import modulon.extensions.coroutine.mapParallel
import modulon.extensions.coroutine.throttleLatest
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass


fun <T> LiveData<out T?>.filterNotNull(): LiveData<T> = mediatorLiveData {
    addSource(this@filterNotNull) { emitNonNull(it) }
}

fun <T> MutableLiveData<out T?>.filterNotNull(): MutableLiveData<T> = mediatorLiveData {
    addSource(this@filterNotNull) { emitNonNull(it) }
}

inline fun <T> LiveData<T>.filter(crossinline predicate: (T) -> Boolean): LiveData<T> = mediatorLiveData<T> {
    addSource(this@filter) { if (predicate(it)) emit(it) }
}

inline fun <T> LiveData<T>.filterNot(crossinline predicate: (T) -> Boolean): LiveData<T> = mediatorLiveData<T> {
    addSource(this@filterNot) { if (!predicate(it)) emit(it) }
}

inline fun <reified T> LiveData<*>.filterIsInstance(): LiveData<T> = mediatorLiveData {
    addSource(this@filterIsInstance) { if (it is T) emit(it) }
}

inline fun <reified T : Any> LiveData<*>.filterIsInstance(kClass: KClass<T>): LiveData<T> = mediatorLiveData {
    addSource(this@filterIsInstance) { if (it is T) emit(it) }
}


// Iterable
inline fun <T> LiveData<out Iterable<T>>.filterChild(crossinline predicate: (T) -> Boolean): LiveData<List<T>> = mediatorLiveData {
    addSource(this@filterChild) { emit(it?.filter(predicate)) }
}

inline fun <T> LiveData<out Iterable<T>>.filterChildNot(crossinline predicate: (T) -> Boolean): LiveData<List<T>> = mediatorLiveData {
    addSource(this@filterChildNot) { emit(it?.filterNot(predicate)) }
}

fun <T> LiveData<out Iterable<T?>>.filterChildNotNull(): LiveData<List<T>> = mediatorLiveData<List<T>> {
    addSource(this@filterChildNotNull) { emit(it?.filterNotNull()) }
}

inline fun <reified R : Any, E> LiveData<Iterable<E>>.filterChildIsInstance(): LiveData<Iterable<R>> = mediatorLiveData<Iterable<R>> {
    addSource(this@filterChildIsInstance) { emit(it.filterIsInstance<R>()) }
}

inline fun <reified R : Any, E> LiveData<Iterable<E>>.filterChildIsInstance(kClass: KClass<R>): LiveData<Iterable<R>> = mediatorLiveData<Iterable<R>> {
    addSource(this@filterChildIsInstance) { emit(it.filterIsInstance<R>()) }
}


inline fun <I : Iterable<T>, T, R> LiveData<I>.mapChild(crossinline transformation: (T) -> R): LiveData<List<R>> = mediatorLiveData<List<R>> {
    addSource(this@mapChild) { emit(it?.map(transformation)) }
}

inline fun <I : Iterable<T>, T, R> LiveData<I>.mapChildParallel(scope: CoroutineScope, runContext: CoroutineContext = Dispatchers.IO, crossinline transformation: suspend (T) -> R): LiveData<List<R>> = mediatorLiveData<List<R>> {
    addSource(this@mapChildParallel) {
        scope.launch { withContext(Dispatchers.Main) { emit(withContext(runContext) { it?.mapParallel(runContext, transformation) }) } }
    }
}

fun <I : Iterable<T>, T> LiveData<I>.onEachChildThrottled(scope: CoroutineScope, runContext: CoroutineContext = Dispatchers.IO, transformation: suspend (T) -> Unit): LiveData<List<T>> = mediatorLiveData<List<T>> {
    var currentJob = Job()
    val emitter = throttleLatest(scope) { it: List<T> ->
        value = it
    }
    addSource(this@onEachChildThrottled) {
        val list = it.toList()
        currentJob.cancel()
        currentJob = Job()
        scope.launch(runContext + currentJob) {
            list.map { async(runContext + currentJob) { transformation.invoke(it); emitter(list) } }.awaitAll()
            emitNow(list)
        }
    }
}

fun <I : Iterable<T>, T> LiveData<I>.applyEachChildThrottled(scope: CoroutineScope, runContext: CoroutineContext = Dispatchers.IO, transformation: suspend T.() -> Unit): LiveData<List<T>> {
    val a: suspend T.() -> Unit = {
        transformation.invoke(this)
    }
    return onEachChildThrottled(scope, runContext, a)
}


fun <I : Iterable<T>, T> LiveData<I>.replaceChildAsync(scope: CoroutineScope, debounce: Long = 500L, runContext: CoroutineContext = Dispatchers.IO, transform: suspend (value: T) -> T): LiveData<List<T>> = mediatorLiveData<List<T>> {
    var currentJob = Job()
    val emitter = throttleLatest(scope, debounce) { it: List<T> ->
        value = it
    }
    addSource(this@replaceChildAsync) {
        val list = it.toMutableList()
        currentJob.cancel()
        currentJob = Job()
        scope.launch(runContext + currentJob) {
            list.mapIndexed { index, it -> async(runContext + currentJob) { list[index] = transform.invoke(it); emitter(list) } }.awaitAll()
//            emitNow(list)
        }
    }
}

//fun <T> Iterable<T>.collectParallel(transform: suspend (value: T) -> R)