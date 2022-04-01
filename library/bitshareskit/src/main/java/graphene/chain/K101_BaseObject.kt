package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.*

@Serializable
data class K101_BaseObject(
    @SerialName("id")
    override val id: K101_BaseIdType,
) : AbstractObject(), K101_BaseType

