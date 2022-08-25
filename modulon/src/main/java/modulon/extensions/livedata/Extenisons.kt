package modulon.extensions.livedata

import androidx.lifecycle.MutableLiveData


fun <T> mutableLiveDataOf(value: T) = NonNullMutableLiveData(value)
fun <T> mutableLiveDataOf() = MutableLiveData<T>()