package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K202_ReservedObject(
    @SerialName("id")
    override val id: K202_ReservedIdType,
) : AbstractObject(), K202_ReservedType