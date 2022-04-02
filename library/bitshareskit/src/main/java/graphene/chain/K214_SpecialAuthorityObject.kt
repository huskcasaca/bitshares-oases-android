package graphene.chain

import graphene.protocol.*
import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K214_SpecialAuthorityObject(
    @SerialName("id")
    override val id: K214_SpecialAuthorityIdType,
    @SerialName("account")
    val account: K102_AccountType,
) : AbstractObject(), K214_SpecialAuthorityType
