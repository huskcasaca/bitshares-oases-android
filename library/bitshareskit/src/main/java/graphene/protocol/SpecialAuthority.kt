package graphene.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(with = SpecialAuthoritySerializer::class)
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
data class SpecialAuthority(
    override val tagType: Int64,
    override val storage: BaseSpecialAuthority
) : StaticVariant<BaseSpecialAuthority>(tagType, storage), GrapheneComponent {

    companion object {
        internal val INVALID = SpecialAuthority(
            0,
            NoSpecialAuthority.INVALID
        )
    }
}

@Serializable(with = BaseSpecialAuthoritySerializer::class)
sealed class BaseSpecialAuthority : GrapheneComponent

@Serializable
class NoSpecialAuthority : BaseSpecialAuthority() {

    companion object {

        internal val INVALID = NoSpecialAuthority(
        )

    }

}

@Serializable
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
data class TopHoldersSpecialAuthority (
    @SerialName("asset")
    val asset: K103_AssetType,
    @SerialName("num_top_holders")
    val numTopHolders: UInt8 = 1U
) : BaseSpecialAuthority() {

    companion object {
        internal val INVALID = TopHoldersSpecialAuthority(
            emptyIdType(),
            UInt8.MAX_VALUE
        )
    }

}





