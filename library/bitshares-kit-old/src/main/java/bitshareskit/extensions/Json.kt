package bitshareskit.extensions

import bitshareskit.objects.GrapheneSerializable
import bitshareskit.objects.GrapheneSortSerializable
import bitshareskit.objects.JsonSerializable
import org.java_json.JSONArray
import org.java_json.JSONObject
import java.math.BigInteger
import java.util.*




fun JSONObject?.orEmpty() = this ?: JSONObject()
fun JSONArray?.orEmpty() = this ?: JSONArray()

fun buildJsonObject(block: JSONObject.() -> Unit) = JSONObject().apply(block)

fun buildJsonArray(block: JSONArray.() -> Unit) = JSONArray().apply(block)
//fun JSONObject.putSerializable(name: String, value: JsonSerializable) = if (value is Optional<*> && value.isPresent) putGrapheneTypes(name, value.fieldSafe!!) else if (value !is Optional<*>) put(name, value.toJsonElement()) else this
fun JSONObject.putSerializable(name: String, value: JsonSerializable) = putGrapheneTypes(name, value.toJsonElement())
fun JSONObject.putItem(name: String, value: Any) = put(name, value)
fun JSONObject.putItemOpt(name: String, value: Any?) = putOpt(name, value)
fun JSONObject.putEmptyArray(name: String) = put(name, JSONArray())
fun JSONObject.putArray(name: String, collection: Collection<Any>) = put(name, JSONArray(collection))
fun JSONObject.putArraySerializable(name: String, collection: Collection<JsonSerializable>) = put(name, JSONArray(collection.map { it.toJsonElement() }))
fun JSONObject.putList(name: String, list: List<Any>) = put(name, JSONArray(list))
fun JSONObject.putGrapheneTime(name: String, value: Date) = put(name, value.toInstant().toString().substring(0..18))

fun JSONObject.putJsonArray(name: String, block: JSONArray.() -> Unit = { }) = put(name, JSONArray().apply(block))
fun JSONObject.putJsonObject(name: String, block: JSONObject.() -> Unit = { }) = putOpt(name, JSONObject().apply(block).takeUnless { it.isEmpty })

fun JSONObject.putJsonArray(name: String, value: JSONArray) = put(name, value)
fun JSONObject.putJsonObject(name: String, value: JSONObject) = put(name, value)

fun JSONObject.putIndexedArray(name: String, collection: Collection<GrapheneSortSerializable>) = put(name, JSONArray(collection.map{ listOf(it.ordinal, it.toJsonElement()) }))

fun JSONObject.putNumber(name: String, value: UByte) = put(name, value.toShort())
fun JSONObject.putNumber(name: String, value: UShort) = put(name, value.toInt())
fun JSONObject.putNumber(name: String, value: UInt) = put(name, value.toLong())
fun JSONObject.putNumber(name: String, value: ULong) = put(name, BigInteger(value.toString()))


fun JSONObject.putGrapheneTypes(name: String, value: Any?): JSONObject {
    return when (value) {
        is JSONArray ->             putJsonArray(name, value)
        is JSONObject ->            putJsonObject(name, value)
        is GrapheneSerializable ->  putSerializable(name, value)
        is UByte ->                 putNumber(name, value)
        is UShort ->                putNumber(name, value)
        is UInt ->                  putNumber(name, value)
        is ULong ->                 putNumber(name, value)
        is Date ->                  putGrapheneTime(name, value)
        else ->                     putItemOpt(name, value)
    }

}


fun JSONArray.putJsonArray(block: JSONArray.() -> Unit) = put(JSONArray().apply(block))

fun JSONArray.putItem(value: Any) = put(value)

fun JSONArray.putNumber(value: UByte) = put(value.toShort())
fun JSONArray.putNumber(value: UShort) = put(value.toInt())
fun JSONArray.putNumber(value: UInt) = put(value.toLong())
fun JSONArray.putNumber(value: ULong) = put(BigInteger(value.toString()))

fun JSONArray.putSerializable(value: JsonSerializable) = put(value.toJsonElement())

















fun JSONArray.putAll(array: JSONArray) {
    (0 until array.length()).forEach {
        this.put(array.get(it))
    }
}

fun jsonArrayOf(vararg obj: Any) = JSONArray().apply {
    obj.forEach { put(it) }
}