package graphene.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
data class Authority (
    @SerialName("weight_threshold")
    val weightThreshold: UInt32,
    @SerialName("account_auths")
    val accountAuths: AccountAuthMap,
    @SerialName("key_auths")
    val keyAuths: KeyAuthMap,
    @SerialName("address_auths")
    val addressAuths: AddressAuthMap,
) : GrapheneComponent {

    companion object {
        internal val INVALID = Authority(
            UInt32.MAX_VALUE, // val weightThreshold: UInt32,
            sortedMapOf(), // val accountAuths: AccountAuthMap,
            sortedMapOf(), // val keyAuths: KeyAuthMap,
            sortedMapOf(), // val addressAuths: AddressAuthMap,
        )
    }
}

