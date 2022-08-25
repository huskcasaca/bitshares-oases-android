package bitshareskit.models

import bitshareskit.extensions.buildJsonObject
import bitshareskit.extensions.optGrapheneInstance
import bitshareskit.objects.AccountObject
import bitshareskit.objects.AssetObject
import bitshareskit.objects.ByteSerializable
import bitshareskit.objects.JsonSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

enum class PredicateType {
    ACCOUNT_NAME_EQ_LIT_PREDICATE,
    ASSET_SYMBOL_EQ_LIT_PREDICATE,
    BLOCK_ID_PREDICATE
}

sealed class Predicate : JsonSerializable, ByteSerializable {

    companion object {
        fun fromJsonPair(rawJson: JSONArray): Predicate{
            return fromJson(rawJson.optJSONObject(1), getPredicateType(rawJson.optInt(0)))
        }

        fun fromJson(rawJson: JSONObject, type: PredicateType): Predicate {
            return when (type) {
                PredicateType.ACCOUNT_NAME_EQ_LIT_PREDICATE -> AccountNameEqLitPredicate.fromJson(rawJson)
                PredicateType.ASSET_SYMBOL_EQ_LIT_PREDICATE -> AssetSymbolEqLitPredicate.fromJson(rawJson)
                PredicateType.BLOCK_ID_PREDICATE -> BlockIdPredicate.fromJson(rawJson)
            }
        }

        private fun getPredicateType(type: Int): PredicateType {
            return PredicateType.values()[type]
        }
    }

    override fun toByteArray(): ByteArray = buildPacket {
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
    }
}

data class AccountNameEqLitPredicate(
    val account: AccountObject,
    val name: String
): Predicate(){

    companion object {
        const val KEY_ACCOUNT_ID = "account_id"
        const val KEY_NAME = "name"
        fun fromJson(rawJson: JSONObject): AccountNameEqLitPredicate {
            return AccountNameEqLitPredicate(
                rawJson.optGrapheneInstance(KEY_ACCOUNT_ID),
                rawJson.optString(KEY_NAME)
            )
        }
    }
}

data class AssetSymbolEqLitPredicate(
    val asset: AssetObject,
    val symbol: String
): Predicate(){

    companion object {
        const val KEY_ASSET_ID = "asset_id"
        const val KEY_SYMBOL = "symbol"
        fun fromJson(rawJson: JSONObject): AssetSymbolEqLitPredicate {
            return AssetSymbolEqLitPredicate(
                rawJson.optGrapheneInstance(KEY_ASSET_ID),
                rawJson.optString(KEY_SYMBOL)
            )
        }
    }

    override fun toByteArray(): ByteArray = buildPacket {
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
    }
}

data class BlockIdPredicate(
    val id: String
): Predicate() {

    companion object {
        const val KEY_ID = "id"
        fun fromJson(rawJson: JSONObject): BlockIdPredicate {
            return BlockIdPredicate(
                rawJson.optString(KEY_ID)
            )
        }
    }

    override fun toByteArray(): ByteArray = buildPacket {
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
    }

}

