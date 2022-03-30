package graphene.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(with = SpecialAuthoritySerializer::class)
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
data class SpecialAuthority(
    override val tagType: Int64 = 0,
    override val storage: BaseSpecialAuthority = NoSpecialAuthority()
) : StaticVariant<BaseSpecialAuthority>(), GrapheneComponent

@Serializable(with = BaseSpecialAuthoritySerializer::class)
sealed class BaseSpecialAuthority : GrapheneComponent

@Serializable
class NoSpecialAuthority : BaseSpecialAuthority()

@Serializable
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
data class TopHoldersSpecialAuthority (
    @SerialName("asset")
    val asset: K103_AssetType = emptyIdType(),
    @SerialName("num_top_holders")
    val numTopHolders: UInt8 = 1U
) : BaseSpecialAuthority()

