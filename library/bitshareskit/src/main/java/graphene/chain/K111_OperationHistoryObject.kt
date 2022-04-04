package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K111_OperationHistoryObject(
    @SerialName("id")
    override val id: OperationHistoryId,
    @SerialName("account")
    val account: AccountIdType, // the account this operation applies to
    @SerialName("operation_id")
    val operation: OperationHistoryIdType,
    @SerialName("sequence")
    val sequence: UInt64 = 0U, // the operation position within the given account
    @SerialName("next")
    val next: AccountTransactionHistoryIdType,
) : AbstractObject(), OperationHistoryIdType {
}
