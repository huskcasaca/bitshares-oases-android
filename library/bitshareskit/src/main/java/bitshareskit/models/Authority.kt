package bitshareskit.models

import bitshareskit.extensions.*
import bitshareskit.objects.AccountObject
import bitshareskit.objects.GrapheneSerializable
import bitshareskit.serializer.writeGrapheneMap
import bitshareskit.serializer.writeGrapheneUInt
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

//@Serializable(with = Authority.AuthoritySerializer::class)
data class Authority constructor(
    val weightThreshold: UInt,
    val accountAuths: Map<AccountObject, UShort> = emptyMap(),
    val keyAuths: Map<PublicKey, UShort> = emptyMap(),
    val addressAuths: Map<PublicKey, UShort> = emptyMap(),
) : GrapheneSerializable {

//    @Serializer(forClass = Authority::class)
//    object AuthoritySerializer : KSerializer<Authority> {
//        override val descriptor: SerialDescriptor
//            get() = PrimitiveDescriptor("Authority", PrimitiveKind.STRING)
//
//        override fun deserialize(decoder: Decoder): Authority {
//            TODO()
//        }
//
//        @ImplicitReflectionSerializer
//        override fun serialize(encoder: Encoder, value: Authority) {
//            (encoder as JsonOutput).encodeJsonObject {
//                put(KEY_WEIGHT_THRESHOLD, value.weightThreshold.toJsonLiteral())
//                put(KEY_ACCOUNT_AUTHS, encoder.json.toJson(AccountAuthsSerializer, value.accountAuths))
//            }
//        }
//    }
    /*

			"owner": {
				"weight_threshold": 1,
				"account_auths": [
					["1.2.23083", 38874],
					["1.2.25533", 3307]
				],
				"key_auths": [
					["BTS5kHZuAv19bYcNEW5q2RMo2SWBvrHLQff7owAYLidZrur73rSkA", 1],
					["BTS7CSE7roASQyc5dr9aVtEP32dA33BkSxhRUFTNEEWgS3812SuYe", 1]
				],
				"address_auths": []
			}
     */

    companion object {

        const val KEY_WEIGHT_THRESHOLD = "weight_threshold"
        const val KEY_ACCOUNT_AUTHS = "account_auths"
        const val KEY_KEY_AUTHS = "key_auths"
        const val KEY_ADDRESS_AUTHS = "address_auths"
        const val KEY_EXTENSIONS = "extensions"

        fun fromJson(rawJson: JSONObject): Authority = Authority(
            rawJson.optUInt(KEY_WEIGHT_THRESHOLD),
            rawJson.optIterable<JSONArray>(KEY_ACCOUNT_AUTHS).map { it.optGrapheneInstance<AccountObject>(0) to it.optUShort(1) }.toMap(),
            rawJson.optIterable<JSONArray>(KEY_KEY_AUTHS).map { it.optPublicKey(0) to it.optUShort(1) }.toMap(),
            emptyMap()
        )
    }

    override fun toByteArray(): ByteArray = buildPacket {
        writeGrapheneUInt(weightThreshold)
        writeGrapheneMap(accountAuths)
        writeGrapheneMap(keyAuths)
        writeGrapheneMap(addressAuths)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putNumber(KEY_WEIGHT_THRESHOLD, weightThreshold)
        putJsonArray(KEY_ACCOUNT_AUTHS) {
            accountAuths.forEach { (account, threshold) ->
                putJsonArray {
                    putSerializable(account)
                    putNumber(threshold)
                }
            }
        }
        putJsonArray(KEY_KEY_AUTHS) {
            keyAuths.forEach { (key, threshold) ->
                putJsonArray {
                    putSerializable(key)
                    putNumber(threshold)
                }
            }
        }
        putJsonArray(KEY_ADDRESS_AUTHS) {
            addressAuths.forEach { (key, threshold) ->
                putJsonArray {
                    putSerializable(key)
                    putNumber(threshold)
                }
            }
        }



    }


}