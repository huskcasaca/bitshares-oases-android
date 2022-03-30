package graphene.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
data class Authority (
    @SerialName("weight_threshold")
    val weightThreshold: UInt32 = 0U,
    @SerialName("account_auths")
    val accountAuths: AccountAuthMap = sortedMapOf(),
    @SerialName("key_auths")
    val keyAuths: KeyAuthMap = sortedMapOf(),
    @SerialName("address_auths")
    val addressAuths: AddressAuthMap = sortedMapOf(),
) : GrapheneComponent