package graphene.protocol

import kotlinx.serialization.Serializable

@Serializable
class AddressType: GrapheneComponent, Comparable<AddressType> {

    override fun compareTo(other: AddressType): Int {
        return 0
    }
}