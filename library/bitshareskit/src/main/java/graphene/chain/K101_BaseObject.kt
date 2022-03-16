package graphene.chain

import graphene.protocol.K101_BaseIdType
import graphene.protocol.K101_BaseType
import graphene.protocol.emptyIdType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K101_BaseObject(
    @SerialName(KEY_ID) override val id: K101_BaseIdType = emptyIdType(),
) : K000_AbstractObject(), K101_BaseType {

}

