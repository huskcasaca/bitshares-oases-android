package modulon.extensions.livedata


fun <T> mutableLiveDataOf(value: T) = NonNullMutableLiveData(value)