package modulon.extensions.livedata

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import modulon.extensions.coroutine.debounce
import modulon.extensions.coroutine.throttleFirst
import modulon.extensions.coroutine.throttleLatest


private const val DEFAULT_TIMEOUT = 500L

fun <X> LiveData<out X>.debounce(scope: CoroutineScope, wait: Long = DEFAULT_TIMEOUT): LiveData<X> = mediatorLiveData {
    val debounce = debounce<X>(scope, wait) { scope.launch(Dispatchers.Main) { value = it } }
    addSource(this@debounce, debounce::invoke)
}

fun <X> LiveData<out X>.throttleFirst(scope: CoroutineScope, wait: Long = DEFAULT_TIMEOUT): LiveData<X> = mediatorLiveData {
    val throttleFirst = throttleFirst<X>(scope, wait) { scope.launch(Dispatchers.Main) { value = it } }
    addSource(this@throttleFirst, throttleFirst::invoke)
}

fun <X> LiveData<out X>.throttleLatest(scope: CoroutineScope, wait: Long = DEFAULT_TIMEOUT): LiveData<X> = mediatorLiveData {
    val throttleFirst = throttleLatest<X>(scope, wait) { scope.launch(Dispatchers.Main) { value = it } }
    addSource(this@throttleLatest, throttleFirst::invoke)
}

fun <X> LiveData<out X>.skip(skip: Int): LiveData<X> = mediatorLiveData {
    var skipped = 0
    addSource(this@skip) {
        if (skipped >= skip) emit(it)
        skipped += 1
    }
}

fun <X> LiveData<out X>.skipFirst(): LiveData<X> = skip(1)



