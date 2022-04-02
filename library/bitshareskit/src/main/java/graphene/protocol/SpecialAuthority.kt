package graphene.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal val INVALID_TYPED_SPECIAL_AUTHORITY = TypedSpecialAuthority(
    0,
    NoSpecialAuthority.INVALID
)

@Serializable(with = SpecialAuthoritySerializer::class)
sealed class SpecialAuthority : GrapheneComponent

@Serializable
class NoSpecialAuthority : SpecialAuthority() {

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
) : SpecialAuthority() {

    companion object {
        internal val INVALID = TopHoldersSpecialAuthority(
            emptyIdType(),
            UInt8.MAX_VALUE
        )
    }

}





