package bitshareskit.extensions

import bitshareskit.chain.ChainConfig.EMPTY_INSTANCE
import bitshareskit.chain.ChainConfig.GLOBAL_INSTANCE
import bitshareskit.entities.LimitOrder
import bitshareskit.models.AssetAmount
import bitshareskit.models.Market
import bitshareskit.models.SimplePrice
import bitshareskit.objects.*
import bitshareskit.objects.GrapheneObject.Companion.GRAPHENE_ID_SEPARATOR
import bitshareskit.objects.ObjectType.*
import bitshareskit.operations.FillOrderOperation
import bitshareskit.operations.LimitOrderCreateOperation
import bitshareskit.operations.Operation
import org.java_json.JSONObject

inline fun <reified T: GrapheneObject> getGrapheneObjectType() = when (T::class) {
    NullObject::class -> NULL_OBJECT
    BaseObject::class -> BASE_OBJECT
    AccountObject::class -> ACCOUNT_OBJECT
    AssetObject::class -> ASSET_OBJECT
    ForceSettlementObject::class -> FORCE_SETTLEMENT_OBJECT
    CommitteeMemberObject::class -> COMMITTEE_MEMBER_OBJECT
    WitnessObject::class -> WITNESS_OBJECT
    LimitOrderObject::class -> LIMIT_ORDER_OBJECT
    CallOrderObject::class -> CALL_ORDER_OBJECT
    CustomObject::class -> CUSTOM_OBJECT
    ProposalObject::class -> PROPOSAL_OBJECT
    OperationHistoryObject::class -> OPERATION_HISTORY_OBJECT
    WithdrawPermissionObject::class -> WITHDRAW_PERMISSION_OBJECT
    VestingBalanceObject::class -> VESTING_BALANCE_OBJECT
    WorkerObject::class -> WORKER_OBJECT
    BalanceObject::class -> BALANCE_OBJECT
    HtlcObject::class -> HTLC_OBJECT
    CustomAuthorityObject::class -> CUSTOM_AUTHORITY_OBJECT
    TicketObject::class -> TICKET_OBJECT
    LiquidityPoolObject::class -> LIQUIDITY_POOL_OBJECT

    GlobalPropertyObject::class -> GLOBAL_PROPERTY_OBJECT
    DynamicGlobalPropertyObject::class -> DYNAMIC_GLOBAL_PROPERTY_OBJECT
    AssetDynamicData::class -> ASSET_DYNAMIC_DATA
    AssetBitassetData::class -> ASSET_BITASSET_DATA
    AccountBalanceObject::class -> ACCOUNT_BALANCE_OBJECT
    AccountStatisticsObject::class -> ACCOUNT_STATISTICS_OBJECT
    TransactionObject::class -> TRANSACTION_OBJECT
    BlockSummaryObject::class -> BLOCK_SUMMARY_OBJECT
    AccountTransactionHistoryObject::class -> ACCOUNT_TRANSACTION_HISTORY_OBJECT
    BlindedBalanceObject::class -> BLINDED_BALANCE_OBJECT
    ChainPropertyObject::class -> CHAIN_PROPERTY_OBJECT
    WitnessScheduleObject::class -> WITNESS_SCHEDULE_OBJECT
    BudgetRecordObject::class -> BUDGET_RECORD_OBJECT
    SpecialAuthorityObject::class -> SPECIAL_AUTHORITY_OBJECT
    BuybackObject::class -> BUYBACK_OBJECT
    FbaAccumulatorObject::class -> FBA_ACCUMULATOR_OBJECT
    CollateralBidObject::class -> COLLATERAL_BID_OBJECT

    OrderHistoryObject::class -> ORDER_HISTORY_OBJECT
    BucketObject::class -> BUCKET_OBJECT
    else -> throw IllegalArgumentException(T::class.toString())
}

inline fun <reified T: GrapheneObject> formatIdentifier(uid: Long): String = formatIdentifier(getGrapheneObjectType<T>(), uid)
inline fun <reified T: GrapheneObject> formatIdentifier(uidList: List<Long>): List<String> = formatIdentifier(getGrapheneObjectType<T>(), uidList)
inline fun <reified T: GrapheneObject> formatIdentifier(id: String): String = formatIdentifier<T>(formatInstance(id))
//inline fun <reified T: GrapheneObject> formatIdentifier(id: String): String {
//    val obj = createGrapheneObject(id)
//    val objType = getGrapheneObjectType<T>()
//    return if (obj.isExist && obj.objectType == objType) obj.id else formatIdentifier(objType, EMPTY_INSTANCE)
//}

//fun formatInstance(id: String): Long = createGrapheneObject(id).let { if (it.isExist) it.uid else EMPTY_INSTANCE }
fun formatInstance(id: String): Long = id.split(GRAPHENE_ID_SEPARATOR).getOrNull(2)?.toLongOrNull()?.coerceAtLeast(EMPTY_INSTANCE) ?: EMPTY_INSTANCE
fun formatIdentifier(type: ObjectType, uid: Long): String = type.namespace.id.toString() + GRAPHENE_ID_SEPARATOR + type.id + GRAPHENE_ID_SEPARATOR + uid
fun formatIdentifier(type: ObjectType, uidList: List<Long>): List<String> = uidList.map { formatIdentifier(type, it) }

fun isGrapheneInstanceValid(id: String): Boolean = createGrapheneObject(id).isExist
fun isGrapheneInstanceValid(uid: Long): Boolean = uid in 0..UInt.MAX_VALUE.toLong()


inline fun <reified T: GrapheneObject> createGraphene(uid: Long): T = createGrapheneObject1(formatIdentifier<T>(uid))
inline fun <reified T: GrapheneObject> createGraphene(id: String): T = createGrapheneObject1(if (T::class == GrapheneObject::class) id else formatIdentifier<T>(id))


//fun createGrapheneObject(id: String) = if (isGrapheneInstanceValid(id)) GrapheneObject.fromJsonOnly(JSONObject(mapOf(GrapheneObject.KEY_ID to id))) else createGrapheneEmptyInstance()
fun createGrapheneObject(id: String) = GrapheneObject.fromJsonOnly(JSONObject(mapOf(GrapheneObject.KEY_ID to id)))
inline fun <reified T: GrapheneObject> createGrapheneObject1(id: String) = T::class.let { if (it == GrapheneObject::class) createGrapheneObjectExcept(id) else it.constructors.first().call(JSONObject(mapOf(GrapheneObject.KEY_ID to id))) }

inline fun <reified T: GrapheneObject> createGrapheneObjectExcept(id: String) = GrapheneObject.fromJsonOnly(JSONObject(mapOf(GrapheneObject.KEY_ID to id))) as T

inline fun <reified T: GrapheneObject> createGrapheneGlobalInstance(): T = createGraphene<T>(formatIdentifier<T>(GLOBAL_INSTANCE))
inline fun <reified T: GrapheneObject> createGrapheneEmptyInstance(): T = createGraphene<T>(formatIdentifier<T>(EMPTY_INSTANCE))


inline fun <reified T: GrapheneObject> isIdentifierValid(id: String): Boolean = createGraphene<T>(id).isExist

val <T: GrapheneObject> T.NULL get() = null as T?

val GrapheneObject?.idOrEmpty get() = this?.id.orEmpty()

fun GrapheneObject?.isValid(): Boolean {
// TODO: removed
//    contract {
//        returns(true) implies (this@isValid != null)
//    }
    return this?.isExist == true
}

inline val AssetObject.symbolOrId get() = symbol.ifEmpty { id }
inline val AssetObject.symbolOrUid get() = symbol.ifEmpty { uid.toString() }
inline val AccountObject.nameOrId get() = name.ifEmpty { id }
inline val AccountObject.nameOrUid get() = name.ifEmpty { uid.toString() }

inline val AssetObject?.symbolOrEmpty get() = this?.symbol.orEmpty()
inline val AccountObject?.nameOrEmpty get() = this?.name.orEmpty()

fun AccountObject.isCommitteeAccount() = uid == AccountObject.COMMITTEE_ACCOUNT_UID
fun AccountObject.isWitnessAccount() = uid == AccountObject.WITNESS_ACCOUNT_UID
fun AccountObject.isRelaxedCommitteeAccount() = uid == AccountObject.RELAXED_COMMITTEE_ACCOUNT_UID
fun AccountObject.isNullAccount() = uid == AccountObject.NULL_ACCOUNT_UID
fun AccountObject.isTempAccount() = uid == AccountObject.TEMP_ACCOUNT_UID
fun AccountObject.isProxyToSelf() = uid == AccountObject.PROXY_TO_SELF_UID

//fun AssetObject.isSCoreAsset() = uid == AssetObject.CORE_ASSET_UID
fun AssetObject.isCore() = assetType == AssetObjectType.CORE
fun AssetObject.isUserIssued() = assetType == AssetObjectType.UIA
fun AssetObject.isSmartcoin() = assetType == AssetObjectType.MPA
fun AssetObject.isPrediction() = assetType == AssetObjectType.PREDICTION
fun AssetObject.isUndefined() = assetType == AssetObjectType.UNDEFINED

fun LimitOrderObject.isInMarket(market: Market) = (salePrice.base.asset.uid == market.base.uid && salePrice.quote.asset.uid == market.quote.uid) || (salePrice.base.asset.uid == market.quote.uid && salePrice.quote.asset.uid == market.base.uid)
fun LimitOrder.isInMarket(market: Market) = (base.asset.uid == market.base.uid && quote.asset.uid == market.quote.uid) || (base.asset.uid == market.quote.uid && quote.asset.uid == market.base.uid)

inline val LimitOrderObject.filledPercent get() = 1.0 * (salePrice.base.amount - sales) / salePrice.base.amount
inline val LimitOrderObject.unfilledPercent get() = 1.0 * sales / salePrice.base.amount

inline val LimitOrderObject.salesAmount get() = AssetAmount(sales, salePrice.base.asset)

inline val CallOrderObject.collateralAmount get() = AssetAmount(collateral, callPrice.base.asset)
inline val CallOrderObject.debtAmount get() = AssetAmount(debt, callPrice.quote.asset)

val AccountBalanceObject.balanceAmount get() = AssetAmount(balance, asset)

fun GrapheneObject.isInstanceOf(obj: GrapheneObject) = id == obj.id && isExist && obj.isExist


val SimplePrice.rebasedPrice: SimplePrice
    get() = if (base.asset.uid > quote.asset.uid && base.asset.assetType == AssetObjectType.MPA) SimplePrice(quote, base) else this

fun SimplePrice.rebase(asset: AssetObject): SimplePrice = if (base.asset.uid == asset.uid) SimplePrice(base, quote) else if (quote.asset.uid == asset.uid) SimplePrice(quote, base) else this
fun SimplePrice.rebaseInvert(asset: AssetObject): SimplePrice = if (quote.asset.uid == asset.uid) SimplePrice(base, quote) else if (base.asset.uid == asset.uid) SimplePrice(quote, base) else this


val LimitOrderCreateOperation.isBid
    get() = sells.asset.uid == sellPrice.rebasedPrice.quote.asset.uid

val LimitOrderCreateOperation.isAsk
    get() = !isBid

val FillOrderOperation.isBid
    get() = pays.asset.uid == price.rebasedPrice.quote.asset.uid

//val SimplePrice.rebasedMarket: Market
//    get() = if (base.asset.uid > quote.asset.uid && base.asset.assetType == AssetObjectType.MPA) Market(quote.asset, base.asset) else Market(base.asset, quote.asset)

//

val SimplePrice.rebasedMarket: Market
    get() = if (base.asset.uid < quote.asset.uid) Market(quote.asset, base.asset) else Market(base.asset, quote.asset)


val LimitOrderObject.rebasedMarket
    get() = salePrice.rebasedMarket

fun createAccountObject(id: String, name: String) = AccountObject(
    buildJsonObject {
        put(GrapheneObject.KEY_ID, id)
        put(AccountObject.KEY_NAME, name)
    }
)

fun createAccountObject(uid: Long, name: String) = AccountObject(
    buildJsonObject {
        put(GrapheneObject.KEY_ID, formatIdentifier<AccountObject>(uid))
        put(AccountObject.KEY_NAME, name)
    }
)


fun createAssetObject(uid: Long, name: String) = AssetObject(
    buildJsonObject {
        put(GrapheneObject.KEY_ID, formatIdentifier<AssetObject>(uid))
        put(AssetObject.KEY_SYMBOL, name)
    }
)

fun createAssetObject(id: String, name: String) = AssetObject(
    buildJsonObject {
        put(GrapheneObject.KEY_ID, id)
        put(AssetObject.KEY_SYMBOL, name)
    }
)


inline fun <reified T: GrapheneObject> T?.orEmpty(): T = if (this != null && this.isValid()) this else createGrapheneEmptyInstance()


fun Operation.isValid() = extractOperationComponents1(this).all { it.isValid() }




val Market.marketNameOrId get() = "${quote.symbol.ifEmpty { quote.id }}/${base.symbol.ifEmpty { base.id }}"