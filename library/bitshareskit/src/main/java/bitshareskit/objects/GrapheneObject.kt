package bitshareskit.objects

import androidx.room.ColumnInfo
import androidx.room.Ignore
import androidx.room.PrimaryKey
import bitshareskit.chain.ChainConfig.EMPTY_INSTANCE
import bitshareskit.objects.ObjectType.*
import bitshareskit.serializer.writeVarLong
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import org.java_json.JSONObject

class GrapheneJsonSerializer<T: GrapheneObject> : KSerializer<T> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("GrapheneJson", PrimitiveKind.STRING)

    private fun String.toGrapheneObject(): T = GrapheneObject.fromJson<T>(JSONObject(this))

    override fun deserialize(decoder: Decoder): T {
        decoder as JsonDecoder
        return decoder.decodeString().toGrapheneObject()
    }

    override fun serialize(encoder: Encoder, value: T) {
        encoder as JsonEncoder
        encoder.encodeString(value.toJsonString())
    }
}

//@Serializable(with = GrapheneInstanceSerializer::class)

val <T: GrapheneObject> (T).NULL get() = null as T?

val <T: GrapheneObject> T.EMPTY get() = null as T?

@Serializable(with = GrapheneJsonSerializer::class)
open class GrapheneObject(@ColumnInfo(name = "data") private val rawJson: JSONObject) : GrapheneSerializable, Cloneable {

    companion object {
        const val TABLE_NAME = "graphene_object"

        const val COLUMN_OWNER_UID = "owner_uid"
        const val COLUMN_LAST_UPDATE = "last_update"
        const val COLUMN_UID = "uid"

        const val KEY_ID = "id"

        const val GRAPHENE_ID_SEPARATOR = "."

        fun fromJsonOnly(rawJson: JSONObject): GrapheneObject {
            return when (GrapheneObject(rawJson).objectType) {
                NULL_OBJECT -> NullObject(rawJson)
                BASE_OBJECT -> BaseObject(rawJson)
                ACCOUNT_OBJECT -> AccountObject(rawJson)
                ASSET_OBJECT -> AssetObject(rawJson)
                FORCE_SETTLEMENT_OBJECT -> ForceSettlementObject(rawJson)
                COMMITTEE_MEMBER_OBJECT -> CommitteeMemberObject(rawJson)
                WITNESS_OBJECT -> WitnessObject(rawJson)
                LIMIT_ORDER_OBJECT -> LimitOrderObject(rawJson)
                CALL_ORDER_OBJECT -> CallOrderObject(rawJson)
                CUSTOM_OBJECT -> CustomObject(rawJson)
                PROPOSAL_OBJECT -> ProposalObject(rawJson)
                OPERATION_HISTORY_OBJECT -> OperationHistoryObject(rawJson)
                WITHDRAW_PERMISSION_OBJECT -> WithdrawPermissionObject(rawJson)
                VESTING_BALANCE_OBJECT -> VestingBalanceObject(rawJson)
                WORKER_OBJECT -> WorkerObject(rawJson)
                BALANCE_OBJECT -> BalanceObject(rawJson)
                HTLC_OBJECT -> HtlcObject(rawJson)
                CUSTOM_AUTHORITY_OBJECT -> CustomAuthorityObject(rawJson)
                TICKET_OBJECT -> TicketObject(rawJson)
                LIQUIDITY_POOL_OBJECT -> LiquidityPoolObject(rawJson)

                GLOBAL_PROPERTY_OBJECT -> GlobalPropertyObject(rawJson)
                DYNAMIC_GLOBAL_PROPERTY_OBJECT -> DynamicGlobalPropertyObject(rawJson)
//                RESERVED_OBJECT_TYPE -> ReservedObject(rawJson)
                ASSET_DYNAMIC_DATA -> AssetDynamicData(rawJson)
                ASSET_BITASSET_DATA -> AssetBitassetData(rawJson)
                ACCOUNT_BALANCE_OBJECT -> AccountBalanceObject(rawJson)
                ACCOUNT_STATISTICS_OBJECT -> AccountStatisticsObject(rawJson)
                TRANSACTION_OBJECT -> TransactionObject(rawJson)
                BLOCK_SUMMARY_OBJECT -> BlockSummaryObject(rawJson)
                ACCOUNT_TRANSACTION_HISTORY_OBJECT -> AccountTransactionHistoryObject(rawJson)
                BLINDED_BALANCE_OBJECT -> BlindedBalanceObject(rawJson)
                CHAIN_PROPERTY_OBJECT -> ChainPropertyObject(rawJson)
                WITNESS_SCHEDULE_OBJECT -> WitnessScheduleObject(rawJson)
                BUDGET_RECORD_OBJECT -> BudgetRecordObject(rawJson)
                SPECIAL_AUTHORITY_OBJECT -> SpecialAuthorityObject(rawJson)
                BUYBACK_OBJECT -> BuybackObject(rawJson)
                FBA_ACCUMULATOR_OBJECT -> FbaAccumulatorObject(rawJson)
                COLLATERAL_BID_OBJECT -> CollateralBidObject(rawJson)

                ORDER_HISTORY_OBJECT -> OrderHistoryObject(rawJson)
                BUCKET_OBJECT -> BucketObject(rawJson)
            }
        }

        // FIXME: 2021/10/24 java.lang.NoSuchMethodError: No virtual method fromJsonObject(Lorg/java_json/JSONObject;)Lbitshareskit/objects/GrapheneObject
        fun <T: GrapheneObject> fromJson(rawJson: JSONObject) = fromJsonOnly(rawJson) as T

        private val PROTOCOL_SPACE_ORDINAL_RANGE = NULL_OBJECT.ordinal..LIQUIDITY_POOL_OBJECT.ordinal
        private val IMPLEMENTATION_SPACE_ORDINAL_RANGE = GLOBAL_PROPERTY_OBJECT.ordinal..COLLATERAL_BID_OBJECT.ordinal
        private val ACCOUNT_HISTORY_SPACE_ORDINAL_RANGE = ORDER_HISTORY_OBJECT.ordinal..BUCKET_OBJECT.ordinal

        val PROTOCOL_TYPES = ObjectType.values().slice(PROTOCOL_SPACE_ORDINAL_RANGE).map { it.id to it }.toMap()
        val IMPLEMENTATION_TYPES = ObjectType.values().slice(IMPLEMENTATION_SPACE_ORDINAL_RANGE).map { it.id to it }.toMap()
        val ACCOUNT_HISTORY_TYPES = ObjectType.values().slice(ACCOUNT_HISTORY_SPACE_ORDINAL_RANGE).map { it.id to it }.toMap()

    }

    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = COLUMN_UID) var uid: Long
    @ColumnInfo(name = COLUMN_LAST_UPDATE) var lastUpdate = System.currentTimeMillis()
    @Ignore val namespace: Namespace
    @Ignore val objectType: ObjectType
    @Ignore val id: String

    init {
        id = rawJson.optString(KEY_ID)
        val ids = id.split(GRAPHENE_ID_SEPARATOR)
        val namespaceId = ids.getOrNull(0)?.toIntOrNull() ?: 0
        val objectId = ids.getOrNull(1)?.toIntOrNull() ?: EMPTY_INSTANCE.toInt()
        uid = ids.getOrNull(2)?.toLongOrNull()?.coerceAtLeast(EMPTY_INSTANCE) ?: EMPTY_INSTANCE

        namespace = when (namespaceId) {
            Namespace.PROTOCOL_SPACE.id -> Namespace.PROTOCOL_SPACE
            Namespace.IMPLEMENTATION_SPACE.id -> Namespace.IMPLEMENTATION_SPACE
            Namespace.ACCOUNT_HISTORY_SPACE.id -> Namespace.ACCOUNT_HISTORY_SPACE
            else -> Namespace.NULL_SPACE
        }

        objectType = when (namespace) {
            Namespace.PROTOCOL_SPACE -> PROTOCOL_TYPES[objectId]
            Namespace.IMPLEMENTATION_SPACE -> IMPLEMENTATION_TYPES[objectId]
            Namespace.ACCOUNT_HISTORY_SPACE -> ACCOUNT_HISTORY_TYPES[objectId]
            else -> NULL_OBJECT
        } ?: NULL_OBJECT
    }

    val isExist
        get() = namespace != Namespace.NULL_SPACE && objectType != NULL_OBJECT && uid in 0..UInt.MAX_VALUE.toLong()


    fun toJsonString() = rawJson.toString()

//    override fun toString(): String {
//        return rawJson.toString()
//    }

    override fun toByteArray(): ByteArray = buildPacket {
        writeVarLong(uid)
    }.readBytes()

    override fun toJsonElement(): Any = id

    fun toJsonObject() = rawJson

    override fun hashCode(): Int {
        return 31 * id.hashCode() + rawJson.toString().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is GrapheneObject && rawJson.similar(other.rawJson)
        // FIXME: 2021/11/19 livedata distinct test
//        return other is GrapheneObject && rawJson.similar(other.rawJson)
    }

}
