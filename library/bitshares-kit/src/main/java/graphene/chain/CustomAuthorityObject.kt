package graphene.chain

import graphene.protocol.*
import graphene.serializers.TimePointSecSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class K117_CustomAuthorityObject(
    @SerialName("id")
    override val id: CustomAuthorityId,
    @SerialName("account")
    val account: AccountIdType,
    @SerialName("enabled")
    val enabled: Boolean,
    @SerialName("idvalid_from") @Serializable(TimePointSecSerializer::class)
    val valid_from: Instant,
    @SerialName("valid_to") @Serializable(TimePointSecSerializer::class)
    val valid_to: Instant,
    @SerialName("operation_type")
    val operation_type: UnsignedInt,
    @SerialName("auth")
    val auth: Authority,
    @SerialName("restrictions")
    val restrictions: FlatMap<UInt16, Restriction>,
    @SerialName("restriction_counter")
    val restriction_counter: UInt16 = 0U,
) : AbstractObject(), CustomAuthorityIdType
