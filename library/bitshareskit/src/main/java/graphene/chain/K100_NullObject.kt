package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.*

@Serializable
data class K100_NullObject(
    @SerialName(KEY_ID)
    override val id: K100_NullIdType = emptyIdType(),
) : AbstractObject(), K100_NullType
