package graphene.chain

import graphene.protocol.K100NullId
import graphene.protocol.K100NullType
import graphene.protocol.emptyIdType
import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K100NullObject(
    @SerialName(KEY_ID) override val id: K100NullId = emptyIdType(),
) : K000AbstractObject(), K100NullType {

}

