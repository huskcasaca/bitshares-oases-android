package graphene.protocol

import kotlinx.serialization.Serializable

typealias TypedHtlcHash = StaticVariant<HtlcHash>

@Serializable
sealed class HtlcHash

@Serializable
data class HtlcAlgoRipemd160(val hash: String): HtlcHash()

@Serializable
data class HtlcAlgoSha1(val hash: String): HtlcHash()

@Serializable
data class HtlcAlgoSha256(val hash: String): HtlcHash()

@Serializable
data class HtlcAlgoHash160(val hash: String): HtlcHash()