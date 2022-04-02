package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K209_AccountTransactionHistoryObject(
    @SerialName("id")
    override val id: AccountTransactionHistoryIdType,
    @SerialName("account")
    val account: AccountType, // the account this operation applies to
    @SerialName("operation_id")
    val operation_id: OperationHistoryType,
    @SerialName("sequence")
    val sequence: uint64_t = 0U, // the operation position within the given account
    @SerialName("next")
    val next: AccountTransactionHistoryType,

    ) : AbstractObject(), AccountTransactionHistoryType {

}