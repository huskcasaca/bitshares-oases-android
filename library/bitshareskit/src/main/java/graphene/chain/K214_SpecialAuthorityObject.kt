package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K214_SpecialAuthorityObject(
    @SerialName("id")
    override val id: SpecialAuthorityId,
    @SerialName("account")
    val account: AccountIdType,
) : AbstractObject(), SpecialAuthorityIdType
