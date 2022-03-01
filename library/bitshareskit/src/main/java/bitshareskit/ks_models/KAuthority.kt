package bitshareskit.ks_models

import bitshareskit.ks_models.KPublicKeySerializer.Companion.toPublicKey
import bitshareskit.ks_object_base.*
import bitshareskit.ks_object_base.KGrapheneIdSerializer.Companion.toObjectId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class KAuthority constructor(
    @SerialName(KEY_WEIGHT_THRESHOLD) val weightThreshold: UInt = 0U,
    @SerialName(KEY_ACCOUNT_AUTHS) val accountAuths: List<AccountAuth> = emptyList(),
    @SerialName(KEY_KEY_AUTHS) val keyAuths: List<KeyAuth> = emptyList(),
    @SerialName(KEY_ADDRESS_AUTHS) val addressAuths: List<AddressAuth> = emptyList(),
) : GrapheneComponent {

    companion object {
        const val KEY_WEIGHT_THRESHOLD = "weight_threshold"
        const val KEY_ACCOUNT_AUTHS = "account_auths"
        const val KEY_KEY_AUTHS = "key_auths"
        const val KEY_ADDRESS_AUTHS = "address_auths"
        const val KEY_EXTENSIONS = "extensions"

        val EMPTY = KAuthority()
    }
}

@Serializable(with = AccountAuthSerializer::class)
data class AccountAuth(
    val account: K102AccountId,
    val threshold: UInt16
)

class AccountAuthSerializer : KSerializer<AccountAuth> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AccountAuth", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): AccountAuth {
        return if (decoder is JsonDecoder) {
            val element = decoder.decodeJsonElement() as JsonArray
            val account: K102AccountId = (element[0] as JsonPrimitive).content.toObjectId()
            val threshold: UInt16 = (element[1] as JsonPrimitive).content.toUInt16()
            AccountAuth(account, threshold)
        } else TODO()
    }
    override fun serialize(encoder: Encoder, value: AccountAuth) = TODO()
}


@Serializable(with = KeyAuthSerializer::class)
data class KeyAuth(
    val key: KPublicKey,
    val threshold: UInt16
)

class KeyAuthSerializer : KSerializer<KeyAuth> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("KeyAuth", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): KeyAuth {
        return if (decoder is JsonDecoder) {
            val element = decoder.decodeJsonElement() as JsonArray
            val key: KPublicKey = (element[0] as JsonPrimitive).content.toPublicKey()
            val threshold: UInt16 = (element[1] as JsonPrimitive).content.toUInt16()
            KeyAuth(key, threshold)
        } else TODO()
    }
    override fun serialize(encoder: Encoder, value: KeyAuth) = TODO()
}

@Serializable(with = AddressAuthSerializer::class)
data class AddressAuth(
    val key: KPublicKey,
    val threshold: UInt16
)

class AddressAuthSerializer : KSerializer<AddressAuth> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AddressAuth", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): AddressAuth {
        return if (decoder is JsonDecoder) {
            val element = decoder.decodeJsonElement() as JsonArray
            val key: KPublicKey = (element[0] as JsonPrimitive).content.toPublicKey()
            val threshold: UInt16 = (element[1] as JsonPrimitive).content.toUInt16()
            AddressAuth(key, threshold)
        } else TODO()
    }
    override fun serialize(encoder: Encoder, value: AddressAuth) = TODO()
}

