package modulon.extensions.livedata

import androidx.lifecycle.LiveData

inline fun <X, Y> LiveData<X>.distinctUntilChangedBy(crossinline transformation: (X) -> Y): LiveData<X> = mediatorLiveData<X> {
    var isFirstTime = true
    addSource(this@distinctUntilChangedBy) { currentValue ->
        val previousValue = value
        if (isFirstTime
            || previousValue == null && currentValue != null
            || previousValue != null && transformation(previousValue) != transformation(currentValue)
        ) {
            isFirstTime = false
            value = currentValue
        }
    }
}

//fun <X> LiveData<X>.distinctUntilChanged(): LiveData<X> = distinctUntilChangedBy { it }

inline fun <I : Iterable<T>, T, R> LiveData<out I>.distinctChildUntilChangedBy(crossinline transformation: (T) -> R): LiveData<I> = mediatorLiveData<I> {
    var isFirstTime = true
    addSource(this@distinctChildUntilChangedBy) { currentValue ->
        val previousValue = value
        if (isFirstTime
            || previousValue == null && currentValue != null
            || previousValue != null && previousValue.map(transformation) != currentValue.map(transformation)
        ) {
            isFirstTime = false
            value = currentValue
        }
    }
}