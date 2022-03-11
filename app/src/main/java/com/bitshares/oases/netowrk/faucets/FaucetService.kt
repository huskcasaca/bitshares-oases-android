package com.bitshares.oases.netowrk.faucets

import bitshareskit.models.PublicKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.io.NotSerializableException

interface FaucetService {

    @Headers("Content-Type: application/json")
    @POST("/api/v1/accounts")
    suspend fun register(@Body account: RegisterRequest): RegisterResult

}

@Serializable(with = RegisterResultSerializer::class)
sealed class RegisterResult


object RegisterResultSerializer : KSerializer<RegisterResult> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("RegisterResult", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): RegisterResult {
        if (decoder is JsonDecoder) {
            val jsonObject = decoder.decodeJsonElement().jsonObject
            if (jsonObject.containsKey("account")) return decoder.json.decodeFromJsonElement<RegisterSuccess>(jsonObject)
            if (jsonObject.containsKey("error")) return decoder.json.decodeFromJsonElement<RegisterFailure>(jsonObject)
        }
        throw NotSerializableException()
    }
    override fun serialize(encoder: Encoder, value: RegisterResult) {
        return if (encoder is JsonEncoder) {
            when (value) {
                is RegisterSuccess -> RegisterSuccess.serializer().serialize(encoder, value)
                is RegisterFailure -> RegisterFailure.serializer().serialize(encoder, value)
            }
        } else throw NotSerializableException()

    }
}

@Serializable
@Polymorphic
data class RegisterSuccess(
    @SerialName("account") val account: AccountInfo,
    @SerialName("status") val status: String
): RegisterResult()

@Serializable
data class RegisterFailure(
    @SerialName("error") val error: ErrorResult
): RegisterResult()

@Serializable
data class RegisterRequest(
    @SerialName("account") val account: AccountInfo
)

@Serializable
data class AccountInfo(
    @SerialName("name") val name: String,
    @SerialName("owner_key") @Serializable(with = PublicKeySerializer::class) val ownerKey: PublicKey,
    @SerialName("active_key") @Serializable(with = PublicKeySerializer::class) val activeKey: PublicKey,
    @SerialName("memo_key") @Serializable(with = PublicKeySerializer::class) val memoKey: PublicKey,
    @SerialName("refcode") val referCode: String? = null,
    @SerialName("referrer") val referrer: String? = null,
    @SerialName("registrar") val registrar: String? = null
)

@Serializable
data class ErrorResult(
    @SerialName("base") val base: List<String>
)
