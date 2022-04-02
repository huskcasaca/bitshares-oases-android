package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K209_AccountTransactionHistoryObject(
    @SerialName("id")
    override val id: K209_AccountTransactionHistoryIdType,
    @SerialName("account")
    val account: K102_AccountType, // the account this operation applies to
    @SerialName("operation_id")
    val operation_id: K111_OperationHistoryType,
    @SerialName("sequence")
    val sequence: uint64_t = 0U, // the operation position within the given account
    @SerialName("next")
    val next: K209_AccountTransactionHistoryType,

    ) : AbstractObject(), K209_AccountTransactionHistoryType {

}