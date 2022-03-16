package graphene.protocol

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
class Extensions

typealias AccountAuthMap = SortedMap<K102_AccountType, Weight>
typealias KeyAuthMap = SortedMap<PublicKeyType, Weight>
typealias AddressAuthMap = SortedMap<AddressType, Weight>

