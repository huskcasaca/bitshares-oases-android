package graphene.chain

import graphene.protocol.*
import graphene.serializers.TimePointSecSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K110_ProposalObject(
    @SerialName("id")
    override val id: ProposalId,
    @SerialName("expiration_time") @Serializable(TimePointSecSerializer::class)
    val expirationTime: Instant,
    @SerialName("review_period_time")
    val reviewPeriodTime: Optional<@Serializable(with = TimePointSecSerializer::class) Instant> = optional(),
    @SerialName("proposed_transaction")
    val proposedTransaction: Transaction,
    @SerialName("required_active_approvals")
    val requiredActiveApprovals: FlatSet<AccountIdType>,
    @SerialName("available_active_approvals")
    val availableActiveApprovals: FlatSet<AccountIdType>,
    @SerialName("required_owner_approvals")
    val requiredOwnerApprovals: FlatSet<AccountIdType>,
    @SerialName("available_owner_approvals")
    val availableOwnerApprovals: FlatSet<AccountIdType>,
    @SerialName("available_key_approvals")
    val availableKeyApprovals: FlatSet<PublicKeyType>,
    @SerialName("proposer")
    val proposer: AccountIdType,
    @SerialName("fail_reason")
    val failReason: String,
) : AbstractObject(), ProposalIdType {

//    bool is_authorized_to_execute(database& db) const;

}
