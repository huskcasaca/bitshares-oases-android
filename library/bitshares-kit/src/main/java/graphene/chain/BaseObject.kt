package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.*

@Serializable
data class K100_NullObject(
    @SerialName("id")
    override val id: NullId,
) : AbstractObject(), NullIdType

@Serializable
data class K101_BaseObject(
    @SerialName("id")
    override val id: BaseId,
) : AbstractObject(), BaseIdType

@Serializable
data class K202_ReservedObject(
    @SerialName("id")
    override val id: ReservedId,
) : AbstractObject(), ReservedIdType