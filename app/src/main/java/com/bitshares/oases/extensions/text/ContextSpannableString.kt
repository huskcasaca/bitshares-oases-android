package com.bitshares.oases.extensions.text

import android.text.Spannable
import bitshareskit.extensions.isBid
import bitshareskit.extensions.symbolOrId
import bitshareskit.extensions.symbolOrUid
import bitshareskit.models.AssetAmount
import bitshareskit.models.FeeParams
import bitshareskit.models.SimplePrice
import bitshareskit.models.operationType
import bitshareskit.objects.*
import bitshareskit.operations.*
import com.bitshares.oases.R
import com.bitshares.oases.chain.formatCoreAssetAmount
import com.bitshares.oases.chain.operationColorResMap
import com.bitshares.oases.chain.operationNameStringResMap
import com.bitshares.oases.database.entities.User
import com.bitshares.oases.database.entities.toAccount
import modulon.extensions.charset.BLANK_SPACE
import modulon.extensions.charset.EMPTY_SPACE
import modulon.extensions.charset.ZERO_WIDTH_BLANK_SPACE
import modulon.extensions.text.*
import modulon.spans.AVATAR_FONT_SCALE_FACTOR
import modulon.spans.FONT_SCALE_FACTOR_100
import modulon.union.UnionContext
import java.util.*

// default factor
// span         -> FONT_SCALE_FACTOR_90
// kdenticon    -> AVATAR_FONT_SCALE_FACTOR

fun ContextSpannableStringBuilder.appendKdenticon(string: CharSequence, avatarBackgroundColor: Int, backgroundColor: Int, fontColor: Int, scale: Float = AVATAR_FONT_SCALE_FACTOR) = apply {
    appendSeparator()
    append(string.toString().toUpperCase(Locale.ROOT))
    setSpan(AvatarDrawableSpan(avatarBackgroundColor, backgroundColor, fontColor, scale), length - string.length, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    append(ZERO_WIDTH_BLANK_SPACE)
}

fun UnionContext.createKdenticon(string: CharSequence, avatarBackgroundColor: Int, backgroundColor: Int, fontColor: Int) = buildContextSpannedString { appendKdenticon(string, avatarBackgroundColor, backgroundColor, fontColor) }

fun ContextSpannableStringBuilder.appendSimpleKdenticon(string: CharSequence, scale: Float = AVATAR_FONT_SCALE_FACTOR) = apply {
    appendKdenticon(string, context.getColor(R.color.tag_normal_dark), context.getColor(R.color.tag_normal), context.getColor(R.color.tag_text_primary), scale)
}

fun ContextSpannableStringBuilder.appendAccountMemberTag(account: AccountObject) = apply {
    if (account.isLifetimeMember) {
        appendTag(context.getString(R.string.tag_lifetime_member).toUpperCase(), context.getColor(R.color.tag_component), context.getColor(R.color.tag_text_inverted))
    } else {
        appendTag(context.getString(R.string.tag_basic_member).toUpperCase(), context.getColor(R.color.tag_default), context.getColor(R.color.tag_text_default))
    }
}

fun UnionContext.createGrapheneInstance(instance: GrapheneObject) = buildContextSpannedString { appendGrapheneInstance(instance) }

fun ContextSpannableStringBuilder.appendAccountMemberDescriptionOnly(showLifetime: Boolean, isLifetime: Boolean) = apply {
    if (!showLifetime) {
        appendSimpleSpan(context.getString(R.string.tag_blockchain_account).toUpperCase())
    } else {
        if (isLifetime) {
            appendSimpleColoredSpan(context.getString(R.string.tag_lifetime_member).toUpperCase(), context.getColor(R.color.tag_component))
        } else {
            appendSimpleSpan(context.getString(R.string.tag_basic_member).toUpperCase())
        }
    }
}
fun ContextSpannableStringBuilder.appendAccountMemberInstanceDescription(account: AccountObject, showLifetime: Boolean) = apply {
    if (!showLifetime) {
        appendMultiSpan(context.getString(R.string.tag_blockchain_account).toUpperCase(), account.uid.toString(), context.getColor(R.color.tag_normal), context.getColor(R.color.tag_normal_dark), context.getColor(R.color.tag_text_default), context.getColor(R.color.tag_text_default), true, true)
    } else {
//        if (account.isLifetimeMember) {
//            appendMultiSpan(context.getString(R.string.tag_lifetime_member).toUpperCase(), account.uid.toString(), context.getColor(R.color.tag_component_light), context.getColor(R.color.tag_component), context.getColor(R.color.tag_text_inverted), context.getColor(R.color.tag_text_inverted), true, true)
//        } else {
//            appendMultiSpan(context.getString(R.string.tag_basic_member).toUpperCase(), account.uid.toString(), context.getColor(R.color.tag_normal), context.getColor(R.color.tag_normal_dark), context.getColor(R.color.tag_text_default), context.getColor(R.color.tag_text_default), true, true)
//        }
        if (account.isLifetimeMember) {
            appendSimpleColoredMultiSpan(context.getString(R.string.tag_lifetime_member).toUpperCase(), account.uid.toString(), context.getColor(R.color.tag_component))
        } else {
            appendSimpleMultiSpan(context.getString(R.string.tag_basic_member).toUpperCase(), account.uid.toString())
        }
    }
}
fun ContextSpannableStringBuilder.appendAssetInstanceDescription(asset: AssetObject, showAssetType: Boolean) = apply {
    if (!showAssetType) {
        appendSimpleColoredMultiSpan(context.getString(R.string.object_type_asset).toUpperCase(), asset.uid.toString(), context.getColor(R.color.tag_component))
    } else {
        when (asset.assetType) {
            AssetObjectType.CORE -> appendSimpleColoredMultiSpan(context.getString(R.string.asset_type_tag_core).toUpperCase(), asset.uid.toString(), context.getColor(R.color.tag_component))
            AssetObjectType.UIA -> appendSimpleColoredMultiSpan(context.getString(R.string.asset_type_tag_user).toUpperCase(), asset.uid.toString(), context.getColor(R.color.tag_dark_grey))
            AssetObjectType.MPA -> appendSimpleColoredMultiSpan(context.getString(R.string.asset_type_tag_market).toUpperCase(), asset.uid.toString(), context.getColor(R.color.tag_cyan))
            AssetObjectType.PREDICTION -> appendSimpleColoredMultiSpan(context.getString(R.string.asset_type_tag_prediction).toUpperCase(), asset.uid.toString(), context.getColor(R.color.tag_orange))
            AssetObjectType.UNDEFINED -> appendSimpleColoredMultiSpan(context.getString(R.string.asset_type_tag_undefined).toUpperCase(), asset.uid.toString(), context.getColor(R.color.tag_red))
        }
    }
}

fun ContextSpannableStringBuilder.appendLocalUserMember() = appendSimpleSpan(context.getString(R.string.tag_local_user).toUpperCase())

fun ContextSpannableStringBuilder.appendGrapheneInstance(instance: GrapheneObject): CharSequence = apply {
    val stringRes = when (instance.objectType) {
        ObjectType.NULL_OBJECT -> R.string.object_type_null
        ObjectType.BASE_OBJECT -> R.string.object_type_base
        ObjectType.ACCOUNT_OBJECT -> R.string.object_type_account
        ObjectType.ASSET_OBJECT -> R.string.object_type_asset
        ObjectType.FORCE_SETTLEMENT_OBJECT -> R.string.object_type_force_settlement
        ObjectType.COMMITTEE_MEMBER_OBJECT -> R.string.object_type_committee_member
        ObjectType.WITNESS_OBJECT -> R.string.object_type_witness
        ObjectType.LIMIT_ORDER_OBJECT -> R.string.object_type_limit_order
        ObjectType.CALL_ORDER_OBJECT -> R.string.object_type_call_order
        ObjectType.CUSTOM_OBJECT -> R.string.object_type_custom
        ObjectType.PROPOSAL_OBJECT -> R.string.object_type_proposal
        ObjectType.OPERATION_HISTORY_OBJECT -> R.string.object_type_operation_history
        ObjectType.WITHDRAW_PERMISSION_OBJECT -> R.string.object_type_withdraw_permission
        ObjectType.VESTING_BALANCE_OBJECT -> R.string.object_type_vesting_balance
        ObjectType.WORKER_OBJECT -> R.string.object_type_worker
        ObjectType.BALANCE_OBJECT -> R.string.object_type_balance
        ObjectType.HTLC_OBJECT -> R.string.object_type_htlc
        ObjectType.CUSTOM_AUTHORITY_OBJECT -> R.string.object_type_custom_authority
        ObjectType.TICKET_OBJECT -> R.string.object_type_ticket
        ObjectType.LIQUIDITY_POOL_OBJECT -> R.string.object_type_liquidity_pool

        ObjectType.GLOBAL_PROPERTY_OBJECT -> R.string.object_type_global_property
        ObjectType.DYNAMIC_GLOBAL_PROPERTY_OBJECT -> R.string.object_type_dynamic_global_property
//        RESERVED_OBJECT_TYPE -> R.string.object_type_reserved
        ObjectType.ASSET_DYNAMIC_DATA -> R.string.object_type_asset_dynam
        ObjectType.ASSET_BITASSET_DATA -> R.string.object_type_asset_bitass
        ObjectType.ACCOUNT_BALANCE_OBJECT -> R.string.object_type_account_balance
        ObjectType.ACCOUNT_STATISTICS_OBJECT -> R.string.object_type_account_statistics
        ObjectType.TRANSACTION_OBJECT -> R.string.object_type_transaction
        ObjectType.BLOCK_SUMMARY_OBJECT -> R.string.object_type_block_summary
        ObjectType.ACCOUNT_TRANSACTION_HISTORY_OBJECT -> R.string.object_type_account_transaction_history
        ObjectType.BLINDED_BALANCE_OBJECT -> R.string.object_type_blinded_balance
        ObjectType.CHAIN_PROPERTY_OBJECT -> R.string.object_type_chain_property
        ObjectType.WITNESS_SCHEDULE_OBJECT -> R.string.object_type_witness_schedule
        ObjectType.BUDGET_RECORD_OBJECT -> R.string.object_type_budget_record
        ObjectType.SPECIAL_AUTHORITY_OBJECT -> R.string.object_type_special_authority
        ObjectType.BUYBACK_OBJECT -> R.string.object_type_buyback
        ObjectType.FBA_ACCUMULATOR_OBJECT -> R.string.object_type_fba_accumulator
        ObjectType.COLLATERAL_BID_OBJECT -> R.string.object_type_collateral_bid

        ObjectType.ORDER_HISTORY_OBJECT -> R.string.object_type_order_history
        ObjectType.BUCKET_OBJECT -> R.string.object_type_bucket
    }
    appendSimpleMultiSpan(context.getString(stringRes).toUpperCase(Locale.ROOT), instance.uid.toString(), false, FONT_SCALE_FACTOR_100)
}



fun ContextSpannableStringBuilder.appendWorkerInstanceDescription(worker: WorkerObject, showWorkerType: Boolean) = apply {
    if (showWorkerType) {
        when (worker.workerType) {
            WorkerObject.WORKER_TYPE_REFUND -> appendSimpleColoredMultiSpan(context.getString(R.string.worker_type_refund).toUpperCase(Locale.ROOT), worker.uid.toString(), context.getColor(R.color.tag_orange))
            WorkerObject.WORKER_TYPE_VESTING -> {
                if (worker.name.contains(Regex("(bsip|BSIP)[0-9]+"))) {
                    appendSimpleColoredMultiSpan(context.getString(R.string.worker_type_bsip).toUpperCase(Locale.ROOT), worker.uid.toString(), context.getColor(R.color.tag_light_blue))
                } else {
                    appendSimpleColoredMultiSpan(context.getString(R.string.worker_type_vesting).toUpperCase(Locale.ROOT), worker.uid.toString(), context.getColor(R.color.tag_component))
                }
            }
            WorkerObject.WORKER_TYPE_BURN -> appendSimpleColoredMultiSpan(context.getString(R.string.worker_type_burn).toUpperCase(Locale.ROOT), worker.uid.toString(), context.getColor(R.color.tag_red))
            else -> appendSimpleColoredMultiSpan(context.getString(R.string.worker_tag_unknown).toUpperCase(Locale.ROOT), worker.uid.toString(), context.getColor(R.color.tag_component_inactive))
        }
    } else {
        appendSimpleColoredMultiSpan(context.getString(R.string.object_type_worker).toUpperCase(), worker.uid.toString(), context.getColor(R.color.tag_component))
    }
}

fun ContextSpannableStringBuilder.appendLimitOrderInstanceDescription(order: LimitOrderObject, isBuy: Boolean) = apply {
    if (isBuy) {
        appendSimpleColoredMultiSpan(context.getString(R.string.limit_order_type_buy).toUpperCase(), order.uid.toString(), context.getColor(R.color.tag_component))
    } else {
        appendSimpleColoredMultiSpan(context.getString(R.string.limit_order_type_sell).toUpperCase(), order.uid.toString(), context.getColor(R.color.tag_component_error))
    }
}

fun ContextSpannableStringBuilder.appendLimitOrderInstanceDescription(order: LimitOrderObject) = apply {
    appendSimpleColoredMultiSpan(context.getString(R.string.object_type_limit_order).toUpperCase(), order.uid.toString(), context.getColor(R.color.tag_component))
}

fun UnionContext.createLimitOrderInstanceDescription(order: LimitOrderObject, isBuy: Boolean) = buildContextSpannedString { appendLimitOrderInstanceDescription(order, isBuy) }
fun UnionContext.createLimitOrderInstanceDescription(order: LimitOrderObject) = buildContextSpannedString { appendLimitOrderInstanceDescription(order) }



fun ContextSpannableStringBuilder.appendGrapheneInstanceDescription(instance: GrapheneObject) {
    when (instance) {
        is AccountObject -> appendAccountMemberInstanceDescription(instance, true)
        is AssetObject -> appendAssetInstanceDescription(instance, true)
        is CommitteeMemberObject -> appendSimpleColoredMultiSpan(context.getString(R.string.object_type_committee_member).toUpperCase(), instance.uid.toString(), context.getColor(R.color.tag_component))
        is WitnessObject -> appendSimpleColoredMultiSpan(context.getString(R.string.object_type_witness).toUpperCase(), instance.uid.toString(), context.getColor(R.color.tag_component))
        is WorkerObject -> appendWorkerInstanceDescription(instance, true)
        is LimitOrderObject -> appendSimpleColoredMultiSpan(context.getString(R.string.object_type_limit_order).toUpperCase(), instance.uid.toString(), context.getColor(R.color.tag_component))
    }
}

fun UnionContext.createGrapheneInstanceDescription(instance: GrapheneObject) = buildContextSpannedString { appendGrapheneInstanceDescription(instance) }

fun UnionContext.createAccountMemberDescriptionOnly(showLifetime: Boolean, isLifetime: Boolean) = buildContextSpannedString { appendAccountMemberDescriptionOnly(showLifetime, isLifetime) }
fun UnionContext.createAccountMemberInstanceDescription(account: AccountObject, showLifetime: Boolean) = buildContextSpannedString { appendAccountMemberInstanceDescription(account, showLifetime) }

fun UnionContext.createLocalUserMember() = buildContextSpannedString { appendLocalUserMember() }



// FONT_SCALE_FACTOR_100 spans
//fun ContextSpannableStringBuilder.appendAccountSpan(account: AccountObject, strip: Boolean = true) = if (account.name.isNotBlank() && !strip || (account.name.length > 32 && strip)) appendSimpleKdenticon(account.name) else appendGrapheneInstance(account)
fun ContextSpannableStringBuilder.appendAccountSpan(account: AccountObject) = appendSimpleKdenticon(account.name)
fun ContextSpannableStringBuilder.appendAccountSpan(user: User) = appendAccountSpan(user.toAccount())

fun ContextSpannableStringBuilder.appendAssetSpan(asset: AssetObject) = if (asset.symbol.isNotBlank() && asset.symbol.length <= 32) appendSimpleSpan(asset.symbol, false, FONT_SCALE_FACTOR_100) else appendGrapheneInstance(asset)

fun ContextSpannableStringBuilder.appendAssetAmountSpan(amount: AssetAmount) = appendSimpleMultiSpan(amount.values, amount.symbols, false, FONT_SCALE_FACTOR_100)
fun ContextSpannableStringBuilder.appendAssetAmountSpan(amount: AssetAmount, color: Int) = appendSimpleColoredMultiSpan(amount.values, amount.symbols, color, false, FONT_SCALE_FACTOR_100)

// price 0.145 CNY/BTS
fun ContextSpannableStringBuilder.appendPriceSpan(price: SimplePrice) = appendSimpleMultiSpan(price.values, price.symbols, false, FONT_SCALE_FACTOR_100)
fun ContextSpannableStringBuilder.appendRealPriceSpan(price: SimplePrice) = appendSimpleMultiSpan(price.realValues, price.symbols, false, FONT_SCALE_FACTOR_100)
// price 0.145 CNY
fun ContextSpannableStringBuilder.appendPriceUnitSpan(price: SimplePrice) = appendSimpleMultiSpan(price.values, price.unit, false, FONT_SCALE_FACTOR_100)
fun ContextSpannableStringBuilder.appendRealPriceUnitSpan(price: SimplePrice) = appendSimpleMultiSpan(price.realValues, price.unit, false, FONT_SCALE_FACTOR_100)

fun ContextSpannableStringBuilder.appendBlockHeightSpan(height: Long) = appendSimpleMultiSpan("BLOCK", height.toString(), false, FONT_SCALE_FACTOR_100)



fun ContextSpannableStringBuilder.appendSimpleAssetAmountSpan(amount: AssetAmount) = appendSimpleMultiSpan(amount.values, amount.asset.symbolOrId, false, FONT_SCALE_FACTOR_100)




fun ContextSpannableStringBuilder.appendAssetName(asset: AssetObject, tag: Boolean = false) = apply {
    if (asset.symbol.isNotEmpty()) when (asset.assetType) {
        AssetObjectType.CORE -> {
            appendItem(asset.symbol)
            if (tag) appendTag(context.getString(R.string.asset_type_tag_core).toUpperCase(Locale.ROOT), context.getColor(R.color.component), context.getColor(R.color.text_primary_inverted))
        }
        AssetObjectType.UIA -> {
            if (asset.symbol.contains(".") && !asset.symbol.endsWith("1.0")) {
                appendScaled(asset.symbol.split(".")[0], 0.8f)
                appendScaled(BLANK_SPACE, 0.5f)
                appendItem(asset.symbol.split(".")[1])
            } else {
                appendItem(asset.symbol)
            }
        }
        AssetObjectType.MPA -> {
            if (asset.symbol.contains(".") && !asset.symbol.endsWith("1.0")) {
                appendScaled(asset.symbol.split(".")[0], 0.8f)
                appendScaled(BLANK_SPACE, 0.5f)
                appendItem(asset.symbol.split(".")[1])
            } else {
//                appendScaled(AssetObject.SMARTCOIN_PREFIX, 0.8f)
                appendItem(asset.symbol)
            }
            if (tag) appendTag(context.getString(R.string.asset_type_tag_market).toUpperCase(Locale.ROOT), context.getColor(R.color.tag_cyan), context.getColor(R.color.text_primary_inverted))
        }
        AssetObjectType.PREDICTION -> {
            if (asset.symbol.contains(".") && !asset.symbol.endsWith("1.0")) {
                appendScaled(asset.symbol.split(".")[0], 0.8f)
                appendScaled(BLANK_SPACE, 0.5f)
                appendItem(asset.symbol.split(".")[1])
            } else {
                appendItem(asset.symbol)
            }
            if (tag) appendTag(context.getString(R.string.asset_type_tag_prediction).toUpperCase(Locale.ROOT), context.getColor(R.color.tag_orange), context.getColor(R.color.text_primary_inverted))
        }
        AssetObjectType.UNDEFINED -> {
            appendItem(asset.symbol)
            if (tag) appendTag(context.getString(R.string.asset_type_tag_undefined).toUpperCase(Locale.ROOT), context.getColor(R.color.tag_red), context.getColor(R.color.text_primary_inverted))
        }
    } else appendItem(asset.id)
}

fun UnionContext.createAssetName(asset: AssetObject) = buildContextSpannedString { appendAssetName(asset) }

fun UnionContext.createAccountSpan(account: AccountObject) = buildContextSpannedString { appendAccountSpan(account) }
fun UnionContext.createAccountSpan(user: User) = buildContextSpannedString { appendAccountSpan(user) }

fun UnionContext.createAssetSpan(asset: AssetObject) = buildContextSpannedString { appendAssetSpan(asset) }

fun UnionContext.createSimpleKdenticon(string: CharSequence) = buildContextSpannedString { appendSimpleKdenticon(string) }
fun UnionContext.createAssetAmountSpan(amount: AssetAmount) = buildContextSpannedString { appendAssetAmountSpan(amount) }
fun UnionContext.createPriceSpan(price: SimplePrice) = buildContextSpannedString { appendPriceSpan(price) }


fun ContextSpannableStringBuilder.appendScaledAccountSpan(account: AccountObject, scale: Float = AVATAR_FONT_SCALE_FACTOR) = appendSimpleKdenticon(account.name, scale)

fun ContextSpannableStringBuilder.appendOperationNameSpan(operation: Operation) = apply {
    when (operation) {
        is FillOrderOperation -> appendSpan(context.getString(if (operation.order is LimitOrderObject) R.string.operation_type_fill_limit_order else R.string.operation_type_fill_call_order).toUpperCase(Locale.ROOT), context.getColor(R.color.operation_fill_order), context.getColor(R.color.text_primary_inverted), true)
        else -> appendSpan(context.getString(operationNameStringResMap.getValue(operation.operationType)).toUpperCase(Locale.ROOT), context.getColor(operationColorResMap.getValue(operation.operationType)), context.getColor(R.color.text_primary_inverted), true)
    }
}

fun ContextSpannableStringBuilder.appendOperationNameSpan(type: OperationType) = apply {
    appendSpan(context.getString(operationNameStringResMap.getValue(type)).toUpperCase(Locale.ROOT), context.getColor(operationColorResMap.getValue(type)), context.getColor(R.color.text_primary_inverted), true)
}

fun ContextSpannableStringBuilder.appendOperationNameSpan(type: FeeParams) = apply {
    appendSpan(context.getString(operationNameStringResMap.getValue(type.operationType)).toUpperCase(Locale.ROOT), context.getColor(operationColorResMap.getValue(type.operationType)), context.getColor(R.color.text_primary_inverted), true)
}


fun ContextSpannableStringBuilder.appendOperationDescriptionSpan(op: Operation) = apply {
    when (op) {
        is TransferOperation -> {
            val from = createAccountSpan(op.from)
            val to = createAccountSpan(op.to)
            val amount = createAssetAmountSpan(op.amount)
            appendFormatArguments(context.getString(R.string.operation_description_transfer), from, to, amount)
        }
        is LimitOrderCreateOperation -> {
            val account = createAccountSpan(op.account)
            val order = createGrapheneInstance(op.result)
            val sells = createAssetAmountSpan(op.sells)
            val receives = createAssetAmountSpan(op.receives)
            val sellPrice = createPriceSpan(op.sellPrice)
            val receivePrice = createPriceSpan(op.receivePrice)
            val bidString = createFormatArguments(context.getString(R.string.operation_description_limit_order_sell), account, order, sells, sellPrice)
            val askString = createFormatArguments(context.getString(R.string.operation_description_limit_order_buy), account, order, receives, receivePrice)
            var inverted = op.isBid
            appendItem(if (inverted) bidString else askString)
        }
        is LimitOrderCancelOperation -> {
            val account = createAccountSpan(op.account)
            val order = createGrapheneInstance(op.order)
            appendFormatArguments(context.getString(R.string.operation_description_limit_order_cancel), account, order)
        }
        is CallOrderUpdateOperation -> {
            val account = createAccountSpan(op.account)
            val debt = createAssetAmountSpan(op.deltaDebt)
            val debtSymbol = createAssetSpan(op.deltaDebt.asset)
            val collateral = createAssetAmountSpan(op.deltaCollateral)
            appendFormatArguments(context.getString(R.string.operation_description_call_order_update), account, debt, debtSymbol, collateral)
        }
        is FillOrderOperation -> {
            val account = createAccountSpan(op.account)
            val order = createGrapheneInstance(op.order)
            val pays = createAssetAmountSpan(op.pays)
            val receives = createAssetAmountSpan(op.receives)
            val payPrice = createPriceSpan(op.payPrice)
            val receivePrice = createPriceSpan(op.receivePrice)
            val bidString = createFormatArguments(context.getString(R.string.operation_description_fill_order_sell), account, order, pays, payPrice)
            val askString = createFormatArguments(context.getString(R.string.operation_description_fill_order_buy), account, order, receives, receivePrice)
            var inverted = op.isBid
            appendItem(if (inverted) bidString else askString)
        }
        is AccountCreateOperation -> {
            val account = createAccountSpan(op.registrar)
            val newAccount = createAccountSpan(op.result)
            appendFormatArguments(context.getString(R.string.operation_description_account_create), account, newAccount)
        }
        is AccountUpdateOperation -> {
            val account = createAccountSpan(op.account)
            appendFormatArguments(context.getString(R.string.operation_description_account_update), account)
        }
        is AccountWhitelistOperation -> {
            val account = createAccountSpan(op.account)
            val accountToList = createAccountSpan(op.accountToList)
            val string = when (op.method) {
                // FIXME: 24/8/2021 fix descriptions
                AccountWhitelistOperation.REMOVE_BLACKLIST -> context.getString(R.string.operation_description_account_whitelist_blacklist_unlisted)
                AccountWhitelistOperation.ADD_WHITELIST -> context.getString(R.string.operation_description_account_whitelist_whitelisted)
                AccountWhitelistOperation.REMOVE_WHITELIST -> context.getString(R.string.operation_description_account_whitelist_whitelist_unlisted)
                AccountWhitelistOperation.ADD_BLACKLIST -> context.getString(R.string.operation_description_account_whitelist_blacklisted)
                else -> EMPTY_SPACE
            }
            appendFormatArguments(string, account, accountToList)
        }
        is AccountUpgradeOperation -> {
            val account = createAccountSpan(op.account)
            val string = if (op.isLifetime) context.getString(R.string.operation_description_account_upgrade_lifetime) else context.getString(R.string.operation_description_account_upgrade_annual)
            appendFormatArguments(string, account)
        }
        is AccountTransferOperation -> {
            val account = createAccountSpan(op.account)
            val newOwner = createAccountSpan(op.newOwner)
            appendFormatArguments(context.getString(R.string.operation_description_account_transfer), account, newOwner)
        }
        is AssetCreateOperation -> {
            val issuer = createAccountSpan(op.issuer)
            val asset = createAssetSpan(op.result)
            appendFormatArguments(context.getString(R.string.operation_description_asset_create), issuer, asset)
        }
        is AssetUpdateOperation -> {
            val issuer = createAccountSpan(op.issuer)
            val asset = createAssetSpan(op.asset)
            appendFormatArguments(context.getString(R.string.operation_description_asset_update), issuer, asset)
        }
        is AssetUpdateBitassetOperation -> {
            val issuer = createAccountSpan(op.issuer)
            val asset = createAssetSpan(op.asset)
            appendFormatArguments(context.getString(R.string.operation_description_asset_update_bitasset), issuer, asset)
        }
        is AssetUpdateFeedProducersOperation -> {
            val issuer = createAccountSpan(op.issuer)
            val asset = createAssetSpan(op.asset)
            appendFormatArguments(context.getString(R.string.operation_description_asset_update_feed_producers), issuer, asset)
        }
        is AssetIssueOperation -> {
            val issuer = createAccountSpan(op.issuer)
            val issueTo = createAccountSpan(op.issueTo)
            val amount = createAssetAmountSpan(op.amount)
            appendFormatArguments(context.getString(R.string.operation_description_asset_issue), issuer, issueTo, amount)
        }
        is AssetReserveOperation -> {
            val account = createAccountSpan(op.account)
            val amount = createAssetAmountSpan(op.amount)
            appendFormatArguments(context.getString(R.string.operation_description_asset_reserve), account, amount)
        }
        is AssetFundFeePoolOperation -> {
            val account = createAccountSpan(op.account)
            val asset = createAssetSpan(op.asset)
            val fund = createAssetAmountSpan(formatCoreAssetAmount(op.fund))
            appendFormatArguments(context.getString(R.string.operation_description_asset_fund_fee_pool), account, asset, fund)
        }
        is AssetSettleOperation -> {
            val account = createAccountSpan(op.account)
            val amount = createAssetAmountSpan(op.amount)
            if (op.isInstantSettle) {
                appendFormatArguments(context.getString(R.string.operation_description_asset_settle_instant), account, amount)
            } else {
                appendFormatArguments(context.getString(R.string.operation_description_asset_settle), account, amount)
            }
        }
        is AssetGlobalSettleOperation -> {
            val account = createAccountSpan(op.issuer)
            val asset = createAssetSpan(op.asset)
            val price = createPriceSpan(op.price)
            appendFormatArguments(context.getString(R.string.operation_description_asset_global_settle), account, asset, price)
        }
        is AssetPublishFeedOperation -> {
            val account = createAccountSpan(op.publisher)
            val feed = createPriceSpan(op.feed.settlementPrice)
            appendFormatArguments(context.getString(R.string.operation_description_asset_publish_feed), account, feed)
        }
        is WitnessCreateOperation -> {
            val account = createAccountSpan(op.account)
            appendFormatArguments(context.getString(R.string.operation_description_witness_create), account)
        }
        is WitnessUpdateOperation -> {
            val account = createAccountSpan(op.account)
            appendFormatArguments(context.getString(R.string.operation_description_witness_update), account)
        }
        is ProposalCreateOperation -> {
            val account = createAccountSpan(op.account)
            val proposal = createGrapheneInstance(op.result)
            appendFormatArguments(context.getString(R.string.operation_description_proposal_create), account, proposal)
        }
        is ProposalUpdateOperation -> {
            val account = createAccountSpan(op.account)
            val proposal = createGrapheneInstance(op.proposal)
            appendFormatArguments(context.getString(R.string.operation_description_proposal_update), account, proposal)
        }
        is ProposalDeleteOperation -> {
            val account = createAccountSpan(op.account)
            val proposal = createGrapheneInstance(op.proposal)
            appendFormatArguments(context.getString(R.string.operation_description_proposal_delete), account, proposal)
        }
        is WithdrawPermissionCreateOperation -> {
            val from = createAccountSpan(op.from)
            val authorized = createAccountSpan(op.authorized)
            appendFormatArguments(context.getString(R.string.operation_description_withdraw_permission_create), from, authorized)
        }
        is WithdrawPermissionUpdateOperation -> {
            val from = createAccountSpan(op.from)
            val to = createAccountSpan(op.to)
            appendFormatArguments(context.getString(R.string.operation_description_withdraw_permission_update), from, to)
        }
        is WithdrawPermissionClaimOperation -> {
            val from = createAccountSpan(op.from)
            val authorized = createAccountSpan(op.authorized)
            appendFormatArguments(context.getString(R.string.operation_description_withdraw_permission_claim), from, authorized)
        }
        is WithdrawPermissionDeleteOperation -> {
            val from = createAccountSpan(op.from)
            val authorized = createAccountSpan(op.authorized)
            appendFormatArguments(context.getString(R.string.operation_description_withdraw_permission_delete), from, authorized)
        }
        is CommitteeMemberCreateOperation -> {
            val account = createAccountSpan(op.account)
            appendFormatArguments(context.getString(R.string.operation_description_committee_member_create), account)
        }
        is CommitteeMemberUpdateOperation -> { // TODO
            val account = createAccountSpan(op.account)
            appendFormatArguments(context.getString(R.string.operation_description_committee_member_update), account)
        }
        is CommitteeMemberUpdateGlobalParametersOperation -> {
            val committee = createAccountSpan(op.committee)
            appendFormatArguments(context.getString(R.string.operation_description_committee_member_update_global_parameters), committee)
        }
        is VestingBalanceCreateOperation -> {
            val creator = createAccountSpan(op.creator)
            val owner = createAccountSpan(op.owner)
            val amount = createAssetAmountSpan(op.amount)
            appendFormatArguments(context.getString(R.string.operation_description_vesting_balance_create), creator, owner, amount)
        }
        is VestingBalanceWithdrawOperation -> {
            val owner = createAccountSpan(op.owner)
            val amount = createAssetAmountSpan(op.amount)
            appendFormatArguments(context.getString(R.string.operation_description_vesting_balance_withdraw), owner, amount)
        }
        is WorkerCreateOperation -> {
            val owner = createAccountSpan(op.owner)
            val worker = createGrapheneInstance(op.result)
            val budget = createAssetAmountSpan(formatCoreAssetAmount(op.dailyPay))
            appendFormatArguments(context.getString(R.string.operation_description_worker_create), owner, worker, budget)
        }
        is CustomOperation -> {
            val account = createAccountSpan(op.account)
            appendFormatArguments(context.getString(R.string.operation_description_custom), account)
        }
        is AssertOperation -> {
            appendFormatArguments(context.getString(R.string.operation_description_assert))
        }
        is BalanceClaimOperation -> {
            val account = createAccountSpan(op.account)
            val amount = createAssetAmountSpan(op.totalClaimed)
            appendFormatArguments(context.getString(R.string.operation_description_balance_claim), account, amount)
        }
        is OverrideTransferOperation -> {
            val issuer = createAccountSpan(op.issuer)
            val from = createAccountSpan(op.from)
            val to = createAccountSpan(op.to)
            val amount = createAssetAmountSpan(op.amount)
            appendFormatArguments(context.getString(R.string.operation_description_override_transfer), issuer, from, to, amount)
        }
        is TransferToBlindOperation -> {
            val account = createAccountSpan(op.from)
            val amount = createAssetAmountSpan(op.amount)
            appendFormatArguments(context.getString(R.string.operation_description_transfer_to_blind), account, amount)
        }
        is BlindTransferOperation -> {
            appendFormatArguments(context.getString(R.string.operation_description_blind_transfer))
        }
        is TransferFromBlindOperation -> {
            val account = createAccountSpan(op.to)
            val amount = createAssetAmountSpan(op.amount)
            appendFormatArguments(context.getString(R.string.operation_description_transfer_from_blind), account, amount)
        }
        is AssetSettleCancelOperation -> {
            val account = createAccountSpan(op.account)
            val amount = createAssetAmountSpan(op.amount)
            appendFormatArguments(context.getString(R.string.operation_description_asset_settle_cancel), account, amount)
        }
        is AssetClaimFeesOperation -> {
            val issuer = createAccountSpan(op.issuer)
            val asset = createAssetSpan(op.amount.asset)
            val amount = createAssetAmountSpan(op.amount)
            appendFormatArguments(context.getString(R.string.operation_description_asset_claim_fees), issuer, asset, amount)
        }
        is FbaDistributeOperation -> {
            appendFormatArguments(context.getString(R.string.operation_description_fba_distribute))
        }
        is BidCollateralOperation -> {
            val bidder = createAccountSpan(op.bidder)
            val collateral = createAssetAmountSpan(op.additionalCollateral)
            val debt = createAssetAmountSpan(op.debtCovered)
            appendFormatArguments(context.getString(R.string.operation_description_bid_collateral), bidder, collateral, debt)
        }
        is ExecuteBidOperation -> {
            val bidder = createAccountSpan(op.bidder)
            appendFormatArguments(context.getString(R.string.operation_description_execute_bid))
        }
        is AssetClaimPoolOperation -> {
            val issuer = createAccountSpan(op.issuer)
            val asset = createAssetSpan(op.asset)
            val amount = createAssetAmountSpan(op.amount)
            appendFormatArguments(context.getString(R.string.operation_description_asset_claim_pool), issuer, asset, amount)
        }
        is AssetUpdateIssuerOperation -> {
            val issuer = createAccountSpan(op.issuer)
            val newIssuer = createAccountSpan(op.newIssuer)
            val asset = createAssetSpan(op.asset)
            appendFormatArguments(context.getString(R.string.operation_description_asset_update_issuer), issuer, newIssuer, asset)
        }
        is HtlcCreateOperation -> {
            val from = createAccountSpan(op.from)
            val to = createAccountSpan(op.to)
            val amount = createAssetAmountSpan(op.amount)
            val expire = createDateBackground(op.expiryTime)
            appendFormatArguments(context.getString(R.string.operation_description_htlc_create), from, to, amount, expire)
        }
        is HtlcRedeemOperation -> {
            val redeemer = createAccountSpan(op.redeemer)
            val htlc = createGrapheneInstance(op.htlc)
            appendFormatArguments(context.getString(R.string.operation_description_htlc_redeem), redeemer, htlc)
        }
        is HtlcRedeemedOperation -> {
            val from = createAccountSpan(op.from)
            val to = createAccountSpan(op.to)
            val amount = createAssetAmountSpan(op.amount)
            val htlc = createGrapheneInstance(op.htlc)
            appendFormatArguments(context.getString(R.string.operation_description_htlc_redeemed), from, to, amount, htlc)
        }
        is HtlcExtendOperation -> {
            val issuer = createAccountSpan(op.issuer)
            val seconds = createSimpleSpan(op.secondSToAdd.toString())
            appendFormatArguments(context.getString(R.string.operation_description_htlc_extend), issuer, seconds)
        }
        is HtlcRefundOperation -> {
            val to = createAccountSpan(op.to)
            val htlc = createGrapheneInstance(op.htlc)
            appendFormatArguments(context.getString(R.string.operation_description_htlc_refund), to, htlc)
        }
        else -> {
            appendFormatArguments(context.getString(R.string.operation_description_unknown))
        }
    }
}

fun ContextSpannableStringBuilder.appendLimitOrderCreateOperationDescriptionSpan(operation: LimitOrderCreateOperation, inverted: Boolean) = apply {
    val account = createAccountSpan(operation.account)
    val order = createGrapheneInstance(operation.result)
    val sells = createAssetAmountSpan(operation.sells)
    val receives = createAssetAmountSpan(operation.receives)
    val sellPrice = createPriceSpan(operation.sellPrice)
    val receivePrice = createPriceSpan(operation.receivePrice)
    val bidString = createFormatArguments(context.getString(R.string.operation_description_limit_order_sell), account, order, sells, receivePrice)
    val askString = createFormatArguments(context.getString(R.string.operation_description_limit_order_buy), account, order, receives, sellPrice)
    appendItem(if (inverted) bidString else askString)
}

fun ContextSpannableStringBuilder.appendFillOrderOperationDescriptionSpan(operation: FillOrderOperation, inverted: Boolean) = apply {
    val account = createAccountSpan(operation.account)
    val order = createGrapheneInstance(operation.order)
    val pays = createAssetAmountSpan(operation.pays)
    val receives = createAssetAmountSpan(operation.receives)
    val payPrice = createPriceSpan(operation.payPrice)
    val receivePrice = createPriceSpan(operation.receivePrice)
    val bidString = createFormatArguments(context.getString(R.string.operation_description_fill_order_sell), account, order, pays, receivePrice)
    val askString = createFormatArguments(context.getString(R.string.operation_description_fill_order_buy), account, order, receives, payPrice)
    appendItem(if (inverted) bidString else askString)
}

fun UnionContext.createOperationNameSpan(operation: Operation) = buildContextSpannedString { appendOperationNameSpan(operation) }



fun ContextSpannableStringBuilder.appendCallOrderInstanceDescription(order: CallOrderObject) = apply {
    appendSimpleColoredMultiSpan(context.getString(R.string.object_type_call_order).toUpperCase(), order.uid.toString(), context.getColor(R.color.tag_component))
}


fun ContextSpannableStringBuilder.appendCallOrderDescription(order: CallOrder) = apply {
    appendSimpleColoredMultiSpan(context.getString(R.string.object_type_call_order).toUpperCase(), order.debt.asset.symbolOrUid, context.getColor(R.color.tag_component))
}

