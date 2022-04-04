package graphene.chain

import graphene.protocol.*
import graphene.serializers.ObjectIdDefaultSerializer
import graphene.serializers.ObjectIdSerializer
import graphene.serializers.StaticVarSerializer
import kotlinx.serialization.KSerializer
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

typealias OperationResult = @Serializable(with = OperationResultSerializer::class) Any

@Serializable
data class VoidResult(
    @Transient val reserved: Unit = Unit,
) : OperationResult()

@Serializable
data class GenericOperationResult(
    @SerialName("new_objects")
    val newObjects: FlatSet<ObjectIdType>,
    @SerialName("updated_objects")
    val updatedObjects: FlatSet<ObjectIdType>,
    @SerialName("removed_objects")
    val removedObjects: FlatSet<ObjectIdType>,
) : OperationResult()

@Serializable
data class GenericExchangeOperationResult(
    @SerialName("paid") val paid: List<Asset>,
    @SerialName("received") val received: List<Asset>,
    @SerialName("fees") val fees: List<Asset>,
) : OperationResult()

@Serializable
data class ExtendableOperationResultDtl(
    @SerialName("impacted_accounts") val impacted_accounts: Optional<FlatSet<AccountIdType>> = optional(),
    @SerialName("new_objects") val newObjects: Optional<FlatSet<ObjectIdType>> = optional(),
    @SerialName("updated_objects") val updatedObjects: Optional<FlatSet<ObjectIdType>> = optional(),
    @SerialName("removed_objects") val removedObjects: Optional<FlatSet<ObjectIdType>> = optional(),
    @SerialName("paid") val paid: Optional<List<Asset>> = optional(),
    @SerialName("received") val received: Optional<List<Asset>> = optional(),
    @SerialName("fees") val fees: Optional<List<Asset>> = optional(),
)

object OperationResultSerializer : StaticVarSerializer<OperationResult>(
    listOf(
        /* 0 */ VoidResult::class,
        /* 1 */ ObjectIdType::class,
        /* 2 */ Asset::class,
        /* 3 */ GenericOperationResult::class,
        /* 4 */ GenericExchangeOperationResult::class,
        /* 5 */ ExtendableOperationResultDtl::class,
    ),
    mapOf(
        ObjectIdType::class to ObjectIdDefaultSerializer
    )
)