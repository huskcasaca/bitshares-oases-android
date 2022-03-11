package com.bitshares.oases.database.converters

import androidx.room.TypeConverter
import bitshareskit.extensions.jsonArrayOf
import bitshareskit.models.PrivateKey
import modulon.extensions.charset.toHexByteArray
import modulon.extensions.charset.toHexString
import org.java_json.JSONArray

class KeyListConverters {

    @TypeConverter
    fun fromKeyList(encryptedKeys: Set<PrivateKey>): String {
        val result = JSONArray()
        encryptedKeys.forEach {
            result.put(jsonArrayOf(it.keyBytes?.toHexString().orEmpty(), it.type.ordinal, it.prefix))
        }
        return result.toString()
    }

    @TypeConverter
    fun toKeyList(message: String): Set<PrivateKey> {
        return runCatching {
            val result: MutableSet<PrivateKey> = mutableSetOf()
            JSONArray(message).forEach {
                if (it != null) {
                    it as JSONArray
                    val keyBytes = it.optString(0).toHexByteArray()
                    val keyType = PrivateKey.KeyType.values()[it.optInt(1)]
                    val prefix = it.optString(2)
                    result.add(PrivateKey(keyBytes, keyType, prefix).apply {
                        isEncrypted = true
                    })
                }
            }
//            require(result.isNotEmpty())
            result
        }.getOrDefault(emptySet())
    }


}
