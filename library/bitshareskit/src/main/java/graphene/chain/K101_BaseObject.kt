package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.*

@Serializable
data class K101_BaseObject(
    @SerialName("id")
    override val id: BaseIdType,
) : AbstractObject(), BaseType

