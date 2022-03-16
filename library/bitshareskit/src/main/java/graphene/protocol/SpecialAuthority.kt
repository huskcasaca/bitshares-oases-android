package graphene.protocol

import kotlinx.serialization.Serializable

@Serializable
data class SpecialAuthority(
    val tagType: Int64 = 0,
    val list: List<BaseSpecialAuthority> = emptyList()
) : GrapheneComponent



@Serializable
sealed class BaseSpecialAuthority : GrapheneComponent

@Serializable
class NoSpecialAuthority(

) : BaseSpecialAuthority()

@Serializable
data class TopHoldersSpecialAuthority (
    val asset: K103_AssetType = emptyIdType(),
    val numTopHolders: UInt8 = 1U
) : BaseSpecialAuthority()

