package graphene.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Authority constructor(
    @SerialName(KEY_WEIGHT_THRESHOLD)
    val weightThreshold: UInt32 = 0U,
    @SerialName(KEY_ACCOUNT_AUTHS) @Serializable(with = SortedMapSerializer::class)
    val accountAuths: AccountAuthMap = sortedMapOf(),
    @SerialName(KEY_KEY_AUTHS) @Serializable(with = SortedMapSerializer::class)
    val keyAuths: KeyAuthMap = sortedMapOf(),
    @SerialName(KEY_ADDRESS_AUTHS) @Serializable(with = SortedMapSerializer::class)
    val addressAuths: AddressAuthMap = sortedMapOf(),
) : GrapheneComponent {

    companion object {
        const val KEY_WEIGHT_THRESHOLD = "weight_threshold"
        const val KEY_ACCOUNT_AUTHS = "account_auths"
        const val KEY_KEY_AUTHS = "key_auths"
        const val KEY_ADDRESS_AUTHS = "address_auths"
        const val KEY_EXTENSIONS = "extensions"

        val EMPTY = Authority()
    }
}
