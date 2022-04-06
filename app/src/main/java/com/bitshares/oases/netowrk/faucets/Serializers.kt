package com.bitshares.oases.netowrk.faucets

import bitshareskit.models.PublicKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

private fun String.toPublicKey(): PublicKey = PublicKey.fromAddress(this)

@Serializer(forClass = PublicKey::class)
object PublicKeySerializer : KSerializer<PublicKey> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("PublicKey", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): PublicKey {
        decoder as JsonDecoder
        return decoder.decodeJsonElement().jsonPrimitive.content.toPublicKey()
    }
    override fun serialize(encoder: Encoder, value: PublicKey) {
        encoder as JsonEncoder
        encoder.encodeJsonElement(JsonPrimitive(value.address))
    }
}

