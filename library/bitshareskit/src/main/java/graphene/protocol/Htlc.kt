package graphene.protocol

import graphene.serializers.StaticVarSerializer
import kotlinx.serialization.Serializable


@Serializable(with = HtlcHashSerializer::class)
sealed class HtlcHash

@Serializable
data class HtlcAlgoRipemd160(
    val hash: String
): HtlcHash()

@Serializable
data class HtlcAlgoSha1(
    val hash: String
): HtlcHash()

@Serializable
data class HtlcAlgoSha256(
    val hash: String
): HtlcHash()

@Serializable
data class HtlcAlgoHash160(
    val hash: String
): HtlcHash()


object HtlcHashSerializer : StaticVarSerializer<HtlcHash>(
    listOf(
        HtlcAlgoRipemd160::class,
        HtlcAlgoSha1::class,
        HtlcAlgoSha256::class,
        HtlcAlgoHash160::class,
    )
)