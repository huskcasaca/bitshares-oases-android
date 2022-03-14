package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K101BaseObject(
    @SerialName(KEY_ID) override val id: K101BaseId = emptyIdType(),
) : K000AbstractObject(), K101BaseType {

}

