package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K207_TransactionHistoryObject(
    @SerialName("id")
    override val id: TransactionHistoryId,
    @SerialName("trx")
    val trx: SignedTransaction,
    @SerialName("trx_id")
    val trx_id: TransactionIdType,
) : AbstractObject(), TransactionHistoryIdType {
//    time_point_sec get_expiration()const { return trx.expiration; }
}
