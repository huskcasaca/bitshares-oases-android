package modulon.extensions.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

fun <T> combineFirst(sources: List<LiveData<out T>>): LiveData<out List<T?>> {

    val livedata = MediatorLiveData<List<T?>>()
    val data: List<Emit<T>> = List(sources.size) { Emit() }

    @Synchronized
    fun combine() {
        livedata.value = data.map { it.value }
    }
    sources.forEachIndexed { index, source ->
        livedata.addSource(source) {
            data[index].value = it
            combine()
        }
    }
    return livedata
}


fun <T> combineLatest(sources: List<LiveData<out T>>): LiveData<out List<T?>> {

    val livedata = MediatorLiveData<List<T?>>()
    val data: List<Emit<T>> = List(sources.size) { Emit() }

    @Synchronized
    fun combine() {
        if (data.all { it.emitted }) livedata.value = data.map { it.value }
    }
    sources.forEachIndexed { index, source ->
        livedata.addSource(source) {
            data[index].value = it
            combine()
        }
    }
    return livedata
}

/**
 * zips both of the LiveData and emits a value after both of them have emitted their values,
 * after that, emits values whenever both of them emit another value.
 *
 * The difference between combineLatest and zip is that the zip only emits after all LiveData
 * objects have a new value, but combineLatest will emit after any of them has a new value.
 */
fun <T1, T2> combine(source1: LiveData<T1>, source2: LiveData<T2>): LiveData<Pair<T1?, T2?>> = combine(source1, source2) { x, y -> Pair(x, y) }

fun <T1, T2, R> combine(source1: LiveData<T1>, source2: LiveData<T2>, combineFunction: (T1?, T2?) -> R): LiveData<R> {
    val livedata: MediatorLiveData<R> = MediatorLiveData()

    val emit1: Emit<T1?> = Emit()
    val emit2: Emit<T2?> = Emit()

    val combine: () -> Unit = {
        if (emit1.emitted && emit2.emitted) {
            val combined = combineFunction(emit1.value, emit2.value)
            emit1.reset()
            emit2.reset()
            livedata.value = combined
        }
    }

    livedata.addSource(source1) { value ->
        emit1.value = value
        combine()
    }
    livedata.addSource(source2) { value ->
        emit2.value = value
        combine()
    }
    return livedata
}

fun <T1, T2, T3> combine(source1: LiveData<T1>, source2: LiveData<T2>, source3: LiveData<T3>): LiveData<Triple<T1?, T2?, T3?>> = combine(source1, source2, source3) { x, y, z -> Triple(x, y, z) }

fun <T1, T2, T3, R> combine(source1: LiveData<T1>, source2: LiveData<T2>, source3: LiveData<T3>, combineFunction: (T1?, T2?, T3?) -> R): LiveData<R> {
    val livedata: MediatorLiveData<R> = MediatorLiveData()

    val emit1: Emit<T1?> = Emit()
    val emit2: Emit<T2?> = Emit()
    val emit3: Emit<T3?> = Emit()

    val combine: () -> Unit = {
        if (emit1.emitted && emit2.emitted && emit3.emitted) {
            val combined = combineFunction(emit1.value, emit2.value, emit3.value)
            emit1.reset()
            emit2.reset()
            emit3.reset()
            livedata.value = combined
        }
    }

    livedata.addSource(source1) { value ->
        emit1.value = value
        combine()
    }
    livedata.addSource(source2) { value ->
        emit2.value = value
        combine()
    }
    livedata.addSource(source3) { value ->
        emit3.value = value
        combine()
    }
    return livedata
}


/**
 * Combines the latest values from multiple LiveData objects.
 * First emits after all LiveData objects have emitted a value, and will emit afterwards after any
 * of them emits a new value.
 *
 * The difference between combineLatest and zip is that the zip only emits after all LiveData
 * objects have a new value, but combineLatest will emit after any of them has a new value.
 */
fun <T1, T2> combineLatest(source1: LiveData<T1>, source2: LiveData<T2>): LiveData<Pair<T1?, T2?>> = combineLatest(source1, source2) { x, y -> Pair(x, y) }

fun <T1, T2, R> combineLatest(source1: LiveData<T1>, source2: LiveData<T2>, combineFunction: (T1?, T2?) -> R): LiveData<R> {
    val livedata: MediatorLiveData<R> = MediatorLiveData()

    val emit1: Emit<T1?> = Emit()
    val emit2: Emit<T2?> = Emit()

    val combine: () -> Unit = {
        if (emit1.emitted && emit2.emitted) {
            val combined = combineFunction(emit1.value, emit2.value)
            livedata.value = combined
        }
    }

    livedata.addSource(source1) { value ->
        emit1.value = value
        combine()
    }
    livedata.addSource(source2) { value ->
        emit2.value = value
        combine()
    }
    return livedata
}

fun <T1, T2, T3> combineLatest(source1: LiveData<T1>, source2: LiveData<T2>, source3: LiveData<T3>): LiveData<Triple<T1?, T2?, T3?>> = combineLatest(source1, source2, source3) { x, y, z -> Triple(x, y, z) }

fun <T1, T2, T3, R> combineLatest(source1: LiveData<out T1>, source2: LiveData<out T2>, source3: LiveData<out T3>, combineFunction: (T1?, T2?, T3?) -> R): LiveData<R> {
    val livedata: MediatorLiveData<R> = MediatorLiveData()

    val emit1: Emit<T1?> = Emit()
    val emit2: Emit<T2?> = Emit()
    val emit3: Emit<T3?> = Emit()

    val combine: () -> Unit = {
        if (emit1.emitted && emit2.emitted && emit3.emitted) {
            val combined = combineFunction(emit1.value, emit2.value, emit3.value)
            livedata.value = combined
        }
    }

    livedata.addSource(source1) { value ->
        emit1.value = value
        combine()
    }
    livedata.addSource(source2) { value ->
        emit2.value = value
        combine()
    }
    livedata.addSource(source3) { value ->
        emit3.value = value
        combine()
    }
    return livedata
}

fun <T1, T2, T3, T4, R> combineLatest(source1: LiveData<out T1>, source2: LiveData<out T2>, source3: LiveData<out T3>, source4: LiveData<out T4>, combineFunction: (T1?, T2?, T3?, T4?) -> R): LiveData<R> {
    val livedata: MediatorLiveData<R> = MediatorLiveData()

    val emit1: Emit<T1?> = Emit()
    val emit2: Emit<T2?> = Emit()
    val emit3: Emit<T3?> = Emit()
    val emit4: Emit<T4?> = Emit()

    val combine: () -> Unit = {
        if (emit1.emitted && emit2.emitted && emit3.emitted && emit4.emitted) {
            val combined = combineFunction(emit1.value, emit2.value, emit3.value, emit4.value)
            livedata.value = combined
        }
    }

    livedata.addSource(source1) { value ->
        emit1.value = value
        combine()
    }
    livedata.addSource(source2) { value ->
        emit2.value = value
        combine()
    }
    livedata.addSource(source3) { value ->
        emit3.value = value
        combine()
    }
    livedata.addSource(source4) { value ->
        emit4.value = value
        combine()
    }
    return livedata
}

fun <T1, T2, T3, T4, T5, R> combineLatest(source1: LiveData<out T1>, source2: LiveData<out T2>, source3: LiveData<out T3>, source4: LiveData<out T4>, source5: LiveData<out T5>, combineFunction: (T1?, T2?, T3?, T4?, T5?) -> R): LiveData<R> {
    val livedata: MediatorLiveData<R> = MediatorLiveData()

    val emit1: Emit<T1?> = Emit()
    val emit2: Emit<T2?> = Emit()
    val emit3: Emit<T3?> = Emit()
    val emit4: Emit<T4?> = Emit()
    val emit5: Emit<T5?> = Emit()

    val combine: () -> Unit = {
        if (emit1.emitted && emit2.emitted && emit3.emitted && emit4.emitted && emit5.emitted) {
            val combined = combineFunction(emit1.value, emit2.value, emit3.value, emit4.value, emit5.value)
            livedata.value = combined
        }
    }

    livedata.addSource(source1) { value ->
        emit1.value = value
        combine()
    }
    livedata.addSource(source2) { value ->
        emit2.value = value
        combine()
    }
    livedata.addSource(source3) { value ->
        emit3.value = value
        combine()
    }
    livedata.addSource(source4) { value ->
        emit4.value = value
        combine()
    }
    livedata.addSource(source5) { value ->
        emit5.value = value
        combine()
    }
    return livedata
}


//fun <A, B, C, D, R> combineLatest(first: LiveData<out A>, second: LiveData<out B>, third: LiveData<out C>, fourth: LiveData<out D>, combineFunction: (A?, B?, C?, D?) -> R): LiveData<R> {
//    val livedata: MediatorLiveData<R> = MediatorLiveData()
//
//    val firstEmit: Emit<A?> = Emit()
//    val secondEmit: Emit<B?> = Emit()
//    val thirdEmit: Emit<C?> = Emit()
//    val fourthEmit: Emit<D?> = Emit()
//
//    val combine: () -> Unit = {
//        if (firstEmit.emitted && secondEmit.emitted && thirdEmit.emitted && fourthEmit.emitted) {
//            val combined = combineFunction(firstEmit.value, secondEmit.value, thirdEmit.value, fourthEmit.value)
//            livedata.value = combined
//        }
//    }
//
//    livedata.addSource(first) { value ->
//        firstEmit.value = value
//        combine()
//    }
//    livedata.addSource(second) { value ->
//        secondEmit.value = value
//        combine()
//    }
//    livedata.addSource(third) { value ->
//        thirdEmit.value = value
//        combine()
//    }
//    livedata.addSource(fourth) { value ->
//        fourthEmit.value = value
//        combine()
//    }
//    return livedata
//}

fun <T1, T2, R> combineFirstOrNull(source1: LiveData<out T1?>, source2: LiveData<out T2?>, combineFunction: (T1, T2) -> R?): LiveData<R?> {
    val livedata: MediatorLiveData<R> = MediatorLiveData()

    val emit1: Emit<T1?> = Emit()
    val emit2: Emit<T2?> = Emit()

    val combine: () -> Unit = {
        val value1 = emit1.value
        val value2 = emit2.value
        livedata.value = if (value1 != null && value2 != null) combineFunction(value1, value2) else null
    }

    livedata.addSource(source1) { value ->
        emit1.value = value
        combine()
    }
    livedata.addSource(source2) { value ->
        emit2.value = value
        combine()
    }
    return livedata
}

fun <T1, T2, T3, R> combineFirstOrNull(source1: LiveData<out T1?>, source2: LiveData<out T2?>, source3: LiveData<out T3?>, combineFunction: (T1, T2, T3) -> R?): LiveData<R?> {
    val livedata: MediatorLiveData<R> = MediatorLiveData()

    val emit1: Emit<T1?> = Emit()
    val emit2: Emit<T2?> = Emit()
    val emit3: Emit<T3?> = Emit()

    val combine: () -> Unit = {
        val value1 = emit1.value
        val value2 = emit2.value
        val value3 = emit3.value
        livedata.value = if (value1 != null && value2 != null && value3 != null) combineFunction(value1, value2, value3) else null
    }

    livedata.addSource(source1) { value ->
        emit1.value = value
        combine()
    }
    livedata.addSource(source2) { value ->
        emit2.value = value
        combine()
    }
    livedata.addSource(source3) { value ->
        emit3.value = value
        combine()
    }
    return livedata
}

fun <T1, T2, T3, T4, R> combineFirstOrNull(source1: LiveData<out T1?>, source2: LiveData<out T2?>, source3: LiveData<out T3?>, source4: LiveData<out T4?>, combineFunction: (T1, T2, T3, T4) -> R?): LiveData<R?> {
    val livedata: MediatorLiveData<R> = MediatorLiveData()

    val emit1: Emit<T1?> = Emit()
    val emit2: Emit<T2?> = Emit()
    val emit3: Emit<T3?> = Emit()
    val emit4: Emit<T4?> = Emit()

    val combine: () -> Unit = {
        val value1 = emit1.value
        val value2 = emit2.value
        val value3 = emit3.value
        val value4 = emit4.value
        livedata.value = if (value1 != null && value2 != null && value3 != null && value4 != null) combineFunction(value1, value2, value3, value4) else null
    }

    livedata.addSource(source1) { value ->
        emit1.value = value
        combine()
    }
    livedata.addSource(source2) { value ->
        emit2.value = value
        combine()
    }
    livedata.addSource(source3) { value ->
        emit3.value = value
        combine()
    }
    livedata.addSource(source4) { value ->
        emit4.value = value
        combine()
    }
    return livedata
}

/**
 * Combines the latest values from multiple LiveData objects.
 * First emits after all LiveData objects have emitted a value, and will emit afterwards after any
 * of them emits a new value.
 *
 * The difference between combineLatest and zip is that the zip only emits after all LiveData
 * objects have a new value, but combineLatest will emit after any of them has a new value.
 */
fun <T1, T2> combineFirst(source1: LiveData<T1>, source2: LiveData<T2>): LiveData<Pair<T1?, T2?>> =
    combineFirst(source1, source2) { x, y -> Pair(x, y) }

fun <T1, T2, R> combineFirst(source1: LiveData<T1>, source2: LiveData<T2>, combineFunction: (T1?, T2?) -> R): LiveData<R> {
    val livedata: MediatorLiveData<R> = MediatorLiveData()

    val emit1: Emit<T1?> = Emit()
    val emit2: Emit<T2?> = Emit()


    val combine: () -> Unit = {
        if (emit1.emitted || emit2.emitted) {
            val combined = combineFunction(emit1.value, emit2.value)
            livedata.value = combined
        }
    }

    livedata.addSource(source1) { value ->
        emit1.value = value
        combine()
    }
    livedata.addSource(source2) { value ->
        emit2.value = value
        combine()
    }
    return livedata
}

fun <T1, T2, T3> combineFirst(source1: LiveData<T1>, source2: LiveData<T2>, source3: LiveData<T3>): LiveData<Triple<T1?, T2?, T3?>> =
    combineFirst(source1, source2, source3) { x, y, z -> Triple(x, y, z) }

fun <T1, T2, T3, R> combineFirst(source1: LiveData<T1>, source2: LiveData<T2>, source3: LiveData<T3>, combineFunction: (T1?, T2?, T3?) -> R): LiveData<R> {
    val livedata: MediatorLiveData<R> = MediatorLiveData()

    val emit1: Emit<T1?> = Emit()
    val emit2: Emit<T2?> = Emit()
    val emit3: Emit<T3?> = Emit()

    val combine: () -> Unit = {
        if (emit1.emitted || emit2.emitted || emit3.emitted) {
            val combined = combineFunction(emit1.value, emit2.value, emit3.value)
            livedata.value = combined
        }
    }

    livedata.addSource(source1) { value ->
        emit1.value = value
        combine()
    }
    livedata.addSource(source2) { value ->
        emit2.value = value
        combine()
    }
    livedata.addSource(source3) { value ->
        emit3.value = value
        combine()
    }
    return livedata
}


/**
 * Combines the latest values from multiple LiveData objects.
 * First emits after all LiveData objects have emitted a value, and will emit afterwards after any
 * of them emits a new value.
 *
 * The difference between combineLatest and zip is that the zip only emits after all LiveData
 * objects have a new value, but combineLatest will emit after any of them has a new value.
 */
// non-null latest
fun <T1, T2> combineNonNull(source1: LiveData<out T1?>, source2: LiveData<out T2?>): LiveData<Pair<T1, T2>> = combineNonNull(source1, source2) { x, y -> Pair(x!!, y!!) }

fun <T1, T2, R> combineNonNull(source1: LiveData<out T1?>, source2: LiveData<out T2?>, combineFunction: (T1, T2) -> R): LiveData<R> {
    val livedata: MediatorLiveData<R> = MediatorLiveData()

    val emit1: Emit<T1> = Emit()
    val emit2: Emit<T2> = Emit()

    val combine: () -> Unit = {
        if (emit1.emitted && emit2.emitted) {
            val value1 = emit1.value
            val value2 = emit2.value
            if (value1 != null && value2 != null) livedata.value = combineFunction(value1, value2)
        }
    }

    livedata.addSource(source1) { value ->
        if (value != null) emit1.value = value
        combine()
    }
    livedata.addSource(source2) { value ->
        if (value != null) emit2.value = value
        combine()
    }
    return livedata
}

fun <T1, T2, T3> combineNonNull(source1: LiveData<out T1?>, source2: LiveData<out T2?>, source3: LiveData<out T3?>): LiveData<Triple<T1, T2, T3>> = combineNonNull(source1, source2, source3) { x, y, z -> Triple(x!!, y!!, z!!) }

fun <T1, T2, T3, R> combineNonNull(source1: LiveData<out T1?>, source2: LiveData<out T2?>, source3: LiveData<out T3?>, combineFunction: (T1, T2, T3) -> R): LiveData<R> {
    val livedata: MediatorLiveData<R> = MediatorLiveData()

    val emit1: Emit<T1> = Emit()
    val emit2: Emit<T2> = Emit()
    val emit3: Emit<T3> = Emit()

    val combine: () -> Unit = {
        if (emit1.emitted && emit2.emitted && emit3.emitted) {
            val value1 = emit1.value
            val value2 = emit2.value
            val value3 = emit3.value
            if (value1 != null && value2 != null && value3 != null) livedata.value = combineFunction(value1, value2, value3)
        }
    }

    livedata.addSource(source1) { value ->
        if (value != null) emit1.value = value
        combine()
    }
    livedata.addSource(source2) { value ->
        if (value != null) emit2.value = value
        combine()
    }
    livedata.addSource(source3) { value ->
        if (value != null) emit3.value = value
        combine()
    }
    return livedata
}

fun <T1, T2, T3, T4> combineNonNull(source1: LiveData<out T1?>, source2: LiveData<out T2?>, source3: LiveData<out T3?>, source4: LiveData<out T4?>): LiveData<Quartet<T1, T2, T3, T4>> = combineNonNull(source1, source2, source3, source4) { t1, t2, t3, t4 -> Quartet(t1!!, t2!!, t3!!, t4!!) }

fun <T1, T2, T3, T4, R> combineNonNull(source1: LiveData<out T1?>, source2: LiveData<out T2?>, source3: LiveData<out T3?>, source4: LiveData<out T4?>, combineFunction: (T1, T2, T3, T4) -> R): LiveData<R> {
    val livedata: MediatorLiveData<R> = MediatorLiveData()

    val emit1: Emit<T1> = Emit()
    val emit2: Emit<T2> = Emit()
    val emit3: Emit<T3> = Emit()
    val emit4: Emit<T4> = Emit()

    val combine: () -> Unit = {
        if (emit1.emitted && emit2.emitted && emit3.emitted && emit4.emitted) {
            val value1 = emit1.value
            val value2 = emit2.value
            val value3 = emit3.value
            val value4 = emit4.value
            if (value1 != null && value2 != null && value3 != null && value4 != null) livedata.value = combineFunction(value1, value2, value3, value4)
        }
    }

    livedata.addSource(source1) { value ->
        if (value != null) emit1.value = value
        combine()
    }
    livedata.addSource(source2) { value ->
        if (value != null) emit2.value = value
        combine()
    }
    livedata.addSource(source3) { value ->
        if (value != null) emit3.value = value
        combine()
    }
    livedata.addSource(source4) { value ->
        if (value != null) emit4.value = value
        combine()
    }
    return livedata
}

fun <T1, T2, T3, T4, T5> combineNonNull(source1: LiveData<out T1?>, source2: LiveData<out T2?>, source3: LiveData<out T3?>, source4: LiveData<out T4?>, source5: LiveData<out T5?>): LiveData<Quintet<T1, T2, T3, T4, T5>> = combineNonNull(source1, source2, source3, source4, source5) { t1, t2, t3, t4, t5 -> Quintet(t1!!, t2!!, t3!!, t4!!, t5!!) }

fun <T1, T2, T3, T4, T5, R> combineNonNull(source1: LiveData<out T1?>, source2: LiveData<out T2?>, source3: LiveData<out T3?>, source4: LiveData<out T4?>, source5: LiveData<out T5?>, combineFunction: (T1, T2, T3, T4, T5) -> R): LiveData<R> {
    val livedata: MediatorLiveData<R> = MediatorLiveData()

    val emit1: Emit<T1> = Emit()
    val emit2: Emit<T2> = Emit()
    val emit3: Emit<T3> = Emit()
    val emit4: Emit<T4> = Emit()
    val emit5: Emit<T5> = Emit()

    val combine: () -> Unit = {
        if (emit1.emitted && emit2.emitted && emit3.emitted && emit4.emitted && emit5.emitted) {
            val value1 = emit1.value
            val value2 = emit2.value
            val value3 = emit3.value
            val value4 = emit4.value
            val value5 = emit5.value
            if (value1 != null && value2 != null && value3 != null && value4 != null && value5 != null) livedata.value = combineFunction(value1, value2, value3, value4, value5)
        }
    }

    livedata.addSource(source1) { value ->
        if (value != null) emit1.value = value
        combine()
    }
    livedata.addSource(source2) { value ->
        if (value != null) emit2.value = value
        combine()
    }
    livedata.addSource(source3) { value ->
        if (value != null) emit3.value = value
        combine()
    }
    livedata.addSource(source4) { value ->
        if (value != null) emit4.value = value
        combine()
    }
    livedata.addSource(source5) { value ->
        if (value != null) emit5.value = value
        combine()
    }
    return livedata
}


fun combineBooleanAll(vararg sources: LiveData<out Boolean?>): LiveData<Boolean> {
    val livedata: MediatorLiveData<Boolean> = MediatorLiveData()
    val emits = List(sources.size) { Emit<Boolean?>() }

    val combine = {
        if (emits.all { it.emitted }) {
            livedata.value = emits.all { it.value == true }
        }
    }

    sources.forEachIndexed { index, data ->
        livedata.addSource(data) { value ->
            emits[index].value = value
            combine()
        }
    }
    return livedata
}

fun combineBooleanAny(vararg sources: LiveData<out Boolean?>): LiveData<Boolean> {
    val livedata: MediatorLiveData<Boolean> = MediatorLiveData()
    val emits = List(sources.size) { Emit<Boolean?>() }

    val combine = {
        if (emits.any { it.emitted }) {
            livedata.value = emits.any { it.value == true }
        }
    }

    sources.forEachIndexed { index, data ->
        livedata.addSource(data) { value ->
            emits[index].value = value
            combine()
        }
    }
    return livedata
}


internal class Emit<T> {

    internal var emitted: Boolean = false

    internal var value: T? = null
        set(value) {
            field = value
            emitted = true
        }

    fun reset() {
        value = null
        emitted = false
    }
}

data class Quartet<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

data class Quintet<out A, out B, out C, out D, out E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)

data class Sextet<out A, out B, out C, out D, out E, out F>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F
)




