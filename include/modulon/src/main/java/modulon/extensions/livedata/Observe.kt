package modulon.extensions.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

//fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
//    observe(lifecycleOwner, object : Observer<T> {
//        override fun onChanged(t: T?) {
//            observer.onChanged(t)
//            removeObserver(this)
//        }
//    })
//}
//
//fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, unit: (T) -> Unit) {
//    val observer: Observer<T> = Observer {
//        unit.invoke(it)
//    }
//    observeOnce(lifecycleOwner, observer)
//}

// TODO: 2022/2/15 remove
fun <T> LiveData<out T?>.observeNonNull(lifecycleOwner: LifecycleOwner, unit: (T) -> Unit) {
    observe(lifecycleOwner, Observer<T?> { if (it != null) unit.invoke(it) })
}

