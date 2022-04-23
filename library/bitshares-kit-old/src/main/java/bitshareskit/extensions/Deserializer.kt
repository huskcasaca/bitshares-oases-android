package bitshareskit.extensions

import bitshareskit.models.*
import bitshareskit.models.Optional
import bitshareskit.objects.GrapheneObject
import bitshareskit.objects.GrapheneSerializable
import org.java_json.JSONArray
import org.java_json.JSONObject
import java.util.*


fun JSONObject.optGrapheneTime(name: String): Date = formatIsoTime(optString(name))
//fun JSONObject.optGrapheneTimeMillis(name: String): Long = formatIsoTime(optString(name)).time
inline fun <reified T: GrapheneObject> JSONObject.optGrapheneInstance(name: String): T = createGraphene(optString(name))
inline fun <reified T: GrapheneObject> JSONObject.optGrapheneInstance(name: String, defaultValue: T): T = optString(name).let { if (it.isEmpty()) defaultValue else createGraphene(it) }
inline fun <reified T: GrapheneObject> JSONObject.optGrapheneInstance(name: String, defaultValue: Nothing?): T? = optString(name).let { if (it.isEmpty()) defaultValue else createGraphene(it) }

fun JSONArray.optGrapheneTime(index: Int): Date = formatIsoTime(optString(index))
//fun JSONArray.optGrapheneTimeMillis(index: Int): Long = formatIsoTime(optString(index)).time
inline fun <reified T: GrapheneObject> JSONArray.optGrapheneInstance(index: Int): T = createGraphene(optString(index))
inline fun <reified T: GrapheneObject> JSONArray.optGrapheneInstance(index: Int, defaultValue: T): T = optString(index).let { if (it.isEmpty()) defaultValue else createGraphene(it) }
inline fun <reified T: GrapheneObject> JSONArray.optGrapheneInstance(index: Int, defaultValue: Nothing?): T? = optString(index).let { if (it.isEmpty()) defaultValue else createGraphene(it) }

inline fun <reified T: Any> JSONObject.optIterable(name: String): Iterable<T> = optJSONArray(name).mapNotNull { it as? T }.asIterable()



//inline fun <reified T: Any> JSONArray.asIterable(): Iterable<T> {
////    return Iterable {
////        object : Iterator<T> {
////            override fun hasNext(): Boolean = this@asIterable.iterator().hasNext()
////            override fun next(): T = this@asIterable.iterator().next() as T
////        }
////    }
//    return (0 until length()).asSequence().mapNotNull { get(it) as? T }.asIterable()
//}

fun JSONObject.optPublicKey(name: String): PublicKey = PublicKey.fromAddress(optString(name))
fun JSONArray.optPublicKey(index: Int): PublicKey = PublicKey.fromAddress(optString(index))

//inline fun <reified T: GrapheneObject> JSONObject.optGrapheneInstance(name: String): T = createGraphene(optString(name))
inline fun <reified T: GrapheneSerializable> JSONObject.optItem(name: String): T = optItemInternal(name)
inline fun <reified T: GrapheneSerializable> JSONObject.optItem(name: String, defaultValue: Nothing?): T? = if (isNull(name)) null else optItemInternal(name)

inline fun <reified T: GrapheneSerializable> JSONObject.optOptionalItem(name: String): Optional<T> {
    if (isNull(name)) return Optional(null)
    return Optional(optItemInternal(name))
}


inline fun <reified T: GrapheneSerializable> JSONObject.optItemInternal(name: String): T {
    return when (T::class) {
        PublicKey::class -> PublicKey.fromAddress(optString(name))
        Vote::class -> Vote.fromStringId(optString(name))
        else -> deserializeGrapheneJsonItem<T>(optJSONObject(name))
    } as T
}

inline fun <reified T: GrapheneSerializable> deserializeGrapheneJsonItem(rawJson: JSONObject): T {
    return when (T::class) {
        SimplePrice::class -> SimplePrice.fromJson(rawJson)
        PriceFeed::class -> PriceFeed.fromJson(rawJson)
        Memo::class -> Memo.fromJson(rawJson)
        Authority::class -> Authority.fromJson(rawJson)
        AssetAmount::class -> AssetAmount.fromJson(rawJson)
        FeeSchedule::class -> FeeSchedule.fromJson(rawJson)
        ChainParameters::class -> ChainParameters.fromJson(rawJson)
        AccountOptions::class -> AccountOptions.fromJson(rawJson)
        LinearVestingPolicyInitializer::class -> LinearVestingPolicyInitializer.fromJson(rawJson)
        CddVestingPolicyInitializer::class -> CddVestingPolicyInitializer.fromJson(rawJson)
        VestingPolicyInitializer::class -> VestingPolicyInitializer.fromJson(rawJson)
        StealthConfirmation::class -> StealthConfirmation.fromJson(rawJson)
        BlindOutput::class -> BlindOutput.fromJson(rawJson)
        else -> throw IllegalArgumentException("No matched class [${T::class}] for method JSONObject.optItem(name: String)")
    } as T
}



fun JSONObject.optULong(name: String, fallback: ULong = 0U): ULong = optString(name).toULongOrNull() ?: fallback
fun JSONObject.getULong(name: String): ULong = getString(name).toULong()
fun JSONObject.optUInt(name: String, fallback: UInt = 0U): UInt = optString(name).toUIntOrNull() ?: fallback
fun JSONObject.getUInt(name: String): UInt = getString(name).toUInt()
fun JSONObject.optUShort(name: String, fallback: UShort = 0U): UShort = optString(name).toUShortOrNull() ?: fallback
fun JSONObject.getUShort(name: String): UShort = getString(name).toUShort()
fun JSONObject.optUByte(name: String, fallback: UByte = 0U): UByte = optString(name).toUByteOrNull() ?: fallback
fun JSONObject.getUByte(name: String): UByte = getString(name).toUByte()

fun JSONArray.optULong(index: Int, fallback: ULong = 0U): ULong = optString(index).toULongOrNull() ?: fallback
fun JSONArray.getULong(index: Int): ULong = getString(index).toULong()
fun JSONArray.optUInt(index: Int, fallback: UInt = 0U): UInt = optString(index).toUIntOrNull() ?: fallback
fun JSONArray.getUInt(index: Int): UInt = getString(index).toUInt()
fun JSONArray.optUShort(index: Int, fallback: UShort = 0U): UShort = optString(index).toUShortOrNull() ?: fallback
fun JSONArray.getUShort(index: Int): UShort = getString(index).toUShort()
fun JSONArray.optUByte(index: Int, fallback: UByte = 0U): UByte = optString(index).toUByteOrNull() ?: fallback
fun JSONArray.getUByte(index: Int): UByte = getString(index).toUByte()






