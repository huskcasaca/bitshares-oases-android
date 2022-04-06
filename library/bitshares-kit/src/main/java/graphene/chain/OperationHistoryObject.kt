package graphene.chain

import graphene.protocol.*
import graphene.serializers.ObjectIdDefaultSerializer
import graphene.serializers.StaticVarSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonArray

@Serializable
data class K111_OperationHistoryObject(
    @SerialName("id")
    override val id: OperationHistoryId,
    @SerialName("op")
    val op: JsonArray,
    @SerialName("result")
    val result: OperationResult,
    /** the block that caused this operation  */
    @SerialName("block_num")
    val blockNum: UInt32, // = 0
    /** the transaction in the block  */
    @SerialName("trx_in_block")
    val trxInBlock: UInt16, // = 0
    /** the operation within the transaction  */
    @SerialName("op_in_trx")
    val opInTrx: UInt16, // = 0
    /** any virtual operations implied by operation in block  */
    @SerialName("virtual_op")
    val virtualOp: UInt32, // = 0
) : AbstractObject(), OperationHistoryIdType {
}



@Serializable
data class K209_AccountTransactionHistoryObject(
    @SerialName("id")
    override val id: AccountTransactionHistoryId,
    @SerialName("account")
    val account: AccountIdType, // the account this operation applies to
    @SerialName("operation_id")
    val operation: OperationHistoryIdType,
    @SerialName("sequence")
    val sequence: UInt64 = 0U, // the operation position within the given account
    @SerialName("next")
    val next: AccountTransactionHistoryIdType,

    ) : AbstractObject(), AccountTransactionHistoryIdType {

}