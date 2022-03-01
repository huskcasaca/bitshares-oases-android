package modulon.extensions.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData

fun <X> emptyLiveData(initValue: X): LiveData<X> = MutableLiveData(initValue)
fun <X> emptyLiveData(initValue: Nothing?): LiveData<X?> = MutableLiveData(initValue)

fun <X> emptyLiveData(): LiveData<X> = object : LiveData<X>() {}


class NonNullLiveData<T>(initValue: T) : LiveData<T>(initValue) {
    override fun getValue(): T = super.getValue() as T
}

class NonNullMutableLiveData<T>(initValue: T) : MutableLiveData<T>(initValue) {
    var distinctItem = false
    override fun getValue(): T = super.getValue() as T
    override fun setValue(value: T) {
        if (distinctItem) {
            if (this.value != value) super.setValue(value)
        } else {
            super.setValue(value)
        }
    }

    override fun postValue(value: T) {
        if (distinctItem) {
            if (this.value != value) super.postValue(value)
        } else {
            super.postValue(value)
        }
    }
}

//fun <X> LiveData<X>.distinctUntilChanged(): LiveData<X> = Transformations.distinctUntilChanged(this)
fun <T> NonNullMutableLiveData<T>.distinctUntilChanged(): NonNullMutableLiveData<T> = apply { distinctItem = true }

class NonNullMediatorLiveData<T>(private val initValue: T) : MediatorLiveData<T>() {
    init {
        postValue(initValue)
    }
    override fun getValue(): T = super.getValue() as T
}

class LiveDataWithDefault<T>(private val initValue: T) : MediatorLiveData<T>() {
    init {
        postValue(initValue)
    }
    override fun getValue(): T = super.getValue() as T
}


