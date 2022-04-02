package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K207_TransactionHistoryObject(
    @SerialName("id")
    override val id: K207_TransactionHistoryIdType,
//    @SerialName("trx")
//    val trx: signed_transaction,
//    @SerialName("trx_id")
//    val trx_id: transaction_id_type,
) : AbstractObject(), K207_TransactionHistoryType {
//    time_point_sec get_expiration()const { return trx.expiration; }
}
