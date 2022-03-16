package graphene.chain

import graphene.protocol.K100_NullIdType
import graphene.protocol.K100_NullType
import graphene.protocol.emptyIdType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K100_NullObject(
    @SerialName(KEY_ID) override val id: K100_NullIdType = emptyIdType(),
) : K000_AbstractObject(), K100_NullType {

}

