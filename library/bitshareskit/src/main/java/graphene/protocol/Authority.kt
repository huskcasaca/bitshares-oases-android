package graphene.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
data class Authority(
    @SerialName("weight_threshold")
    val weightThreshold: uint32_t,
    @SerialName("account_auths")
    val accountAuths: FlatMap<AccountIdType, Weight>,
    @SerialName("key_auths")
    val keyAuths: FlatMap<PublicKeyType, Weight>,
    @SerialName("address_auths")
    val addressAuths: FlatMap<AddressType, Weight>,
) : GrapheneComponent {

    companion object {
        internal val INVALID = Authority(
            uint32_t.MAX_VALUE, // val weightThreshold: UInt32,
            sortedMapOf(), // val accountAuths: AccountAuthMap,
            sortedMapOf(), // val keyAuths: KeyAuthMap,
            sortedMapOf(), // val addressAuths: AddressAuthMap,
        )
    }
}

