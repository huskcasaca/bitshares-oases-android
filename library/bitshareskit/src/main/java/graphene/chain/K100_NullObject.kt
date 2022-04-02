package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.*

@Serializable
data class K100_NullObject(
    @SerialName("id")
    override val id: NullIdType,
) : AbstractObject(), NullType
