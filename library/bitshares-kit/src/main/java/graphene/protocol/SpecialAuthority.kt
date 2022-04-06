package graphene.protocol

import graphene.serializers.StaticVarSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable(with = SpecialAuthoritySerializer::class)
sealed class SpecialAuthority : GrapheneComponent

@Serializable
data class NoSpecialAuthority(
    @Transient
    val reserved: Unit = Unit
) : SpecialAuthority() {
    companion object {
        internal val INVALID = NoSpecialAuthority(
        )
    }
}

@Serializable
data class TopHoldersSpecialAuthority (
    @SerialName("asset")
    val asset: AssetIdType,
    @SerialName("num_top_holders")
    val numTopHolders: UInt8 = 1U
) : SpecialAuthority()

object SpecialAuthoritySerializer : StaticVarSerializer<SpecialAuthority>(
    listOf(
        NoSpecialAuthority::class,
        TopHoldersSpecialAuthority::class
    )
)