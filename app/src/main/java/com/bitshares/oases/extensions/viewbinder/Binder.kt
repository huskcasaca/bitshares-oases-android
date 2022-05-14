package com.bitshares.oases.extensions.viewbinder

import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.view.Gravity
import android.widget.LinearLayout
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import bitshareskit.chain.ChainConfig
import bitshareskit.entities.Block
import bitshareskit.extensions.*
import bitshareskit.models.*
import bitshareskit.objects.*
import bitshareskit.operations.FillOrderOperation
import bitshareskit.operations.LimitOrderCreateOperation
import bitshareskit.operations.Operation
import com.bitshares.oases.R
import com.bitshares.oases.chain.AccountBalance
import com.bitshares.oases.chain.CommitteeMember
import com.bitshares.oases.database.entities.BitsharesNode
import com.bitshares.oases.database.entities.Node
import com.bitshares.oases.database.entities.User
import com.bitshares.oases.database.entities.toAccount
import com.bitshares.oases.extensions.text.*
import com.bitshares.oases.globalPreferenceManager
import com.bitshares.oases.preference.old.Graphene
import com.bitshares.oases.preference.old.Settings
import com.caverock.androidsvg.SVG
import kdenticon.HashUtils
import kdenticon.Kdenticon
import modulon.component.cell.BaseCell
import modulon.component.cell.ComponentCell
import modulon.component.cell.IconSize
import modulon.component.cell.tables
import modulon.extensions.font.typefaceMonoRegular
import modulon.extensions.graphics.createRoundRectDrawable
import modulon.extensions.text.*
import modulon.extensions.view.backgroundTintColor
import modulon.extensions.view.doOnClick
import modulon.extensions.view.dp
import modulon.extensions.viewbinder.startScrolling
import modulon.spans.FONT_SCALE_FACTOR_100
import java.text.DateFormat
import java.util.*
import kotlin.math.abs

// avatar
fun BaseCell.bindKdenticonAvatar(value: String, size: IconSize) {
    iconSize = size
    val backgroundColor = context.getColor(R.color.background_cover)
    val backgroundRadius = resources.getDimension(iconSize.size) / 10
    val drawableSize = resources.getDimensionPixelSize(iconSize.size)
    val hashString = HashUtils.sha256(value)
    val s = Kdenticon.toSvg(hashString, drawableSize, 0.1f)
    iconView.apply {
        updateLayoutParams<LinearLayout.LayoutParams> {
            // TODO: 23/1/2022 should be 4dp?
            leftMargin = (-2).dp
            rightMargin = (-2).dp + componentOffset
            gravity = Gravity.TOP or Gravity.START
        }
        isVisible = true
        background = createRoundRectDrawable(backgroundColor, backgroundRadius)
        val drawable = PictureDrawable(SVG.getFromString(s).renderToPicture())
        setImageDrawable(drawable)
    }
}

// committees / witnesses / workers
fun ComponentCell.bindCommitteeV3(committee: CommitteeMember, iconSize: IconSize = IconSize.COMPONENT_0) {
    bindKdenticonAvatar(committee.account.name, iconSize)
    title = createGrapheneInstanceDescription(committee.committee)
    text = committee.account.name.toUpperCase()
    subtitle = createSimpleMultiSpan(formatAssetBigDecimal(committee.committee.totalVotes, Graphene.KEY_CORE_ASSET.value).coolFormat(), context.getString(R.string.voting_votes))
}
fun ComponentCell.bindWitnessV3(witness: WitnessObject, iconSize: IconSize = IconSize.COMPONENT_0) {
    bindKdenticonAvatar(witness.witnessAccount.name, iconSize)
    title = createGrapheneInstanceDescription(witness)
    subtitle = createSimpleMultiSpan(formatAssetBigDecimal(witness.totalVotes, Graphene.KEY_CORE_ASSET.value).coolFormat(), context.getString(R.string.voting_votes))
    text = witness.witnessAccount.name.uppercase()
    subtext = buildContextSpannedString {
        append(context.getString(R.string.voting_block_missed), modulon.extensions.charset.BLANK_SPACE)
        appendColored(witness.totalMissed.toString(), context.getColor(R.color.tag_text_primary))
    }
}
fun ComponentCell.bindWorkerV3(worker: WorkerObject, active: Boolean) {
    title = buildContextSpannedString {
        appendGrapheneInstanceDescription(worker)
        if (active) {
            appendSimpleColoredSpan(context.getString(R.string.worker_tag_active).toUpperCase(Locale.ROOT), context.getColor(R.color.tag_green))
        }
    }
    subtitle = createSimpleMultiSpan(formatAssetBigDecimal(worker.totalVotesFor, Graphene.KEY_CORE_ASSET.value).coolFormat(), context.getString(R.string.voting_votes))
    text = worker.name
    subtext = buildContextSpannedString {
        append(context.getString(R.string.worker_daily_budget), modulon.extensions.charset.BLANK_SPACE)
        appendColored(formatAssetBalance(worker.dailyPay, Graphene.KEY_CORE_ASSET.value), context.getColor(R.color.tag_text_primary))
        appendNewLine()
        append(context.getString(R.string.worker_start_from), modulon.extensions.charset.BLANK_SPACE)
        appendColored(DateFormat.getDateInstance(DateFormat.MEDIUM).format(worker.workBeginDate), context.getColor(R.color.tag_text_primary))
        append(modulon.extensions.charset.BLANK_SPACE, context.getString(R.string.worker_end_to), modulon.extensions.charset.BLANK_SPACE)
        appendColored(DateFormat.getDateInstance(DateFormat.MEDIUM).format(worker.workEndDate), context.getColor(R.color.tag_text_primary))
        appendNewLine()
        append(context.getString(R.string.worker_issuer), modulon.extensions.charset.BLANK_SPACE)
        appendAccountSpan(worker.workerAccount)
    }
}
// TODO: 23/1/2022 add iconsize
fun ComponentCell.bindCommitteeSimple(committee: CommitteeMember, iconSize: IconSize = IconSize.COMPONENT_0) {
    bindKdenticonAvatar(committee.account.name, iconSize)
    title = createGrapheneInstanceDescription(committee.committee)
    subtitle = createSimpleMultiSpan(formatAssetBigDecimal(committee.committee.totalVotes, Graphene.KEY_CORE_ASSET.value).coolFormat(), context.getString(R.string.voting_votes))
    text = committee.account.name.toUpperCase()
}
// TODO: 23/1/2022 add iconsize
fun ComponentCell.bindWitnessSimple(witness: WitnessObject, iconSize: IconSize = IconSize.COMPONENT_0) {
    bindKdenticonAvatar(witness.witnessAccount.name, iconSize)
    title = createGrapheneInstanceDescription(witness)
    subtitle = createSimpleMultiSpan(formatAssetBigDecimal(witness.totalVotes, Graphene.KEY_CORE_ASSET.value).coolFormat(), context.getString(R.string.voting_votes))
    text = witness.witnessAccount.name.toUpperCase()
}
fun ComponentCell.bindWorkerSimple(worker: WorkerObject) {
    title = createGrapheneInstanceDescription(worker)
    subtitle = createSimpleMultiSpan(formatAssetBigDecimal(worker.totalVotesFor, Graphene.KEY_CORE_ASSET.value).coolFormat(), context.getString(R.string.voting_votes))
    text = worker.name
}


// accounts / users v1
// TODO: 22/1/2022 add iconSize and showMemberType
fun BaseCell.bindAccountV1(account: AccountObject, showMemberType: Boolean, iconSize: IconSize = IconSize.COMPONENT_0) {
    bindKdenticonAvatar(account.name, iconSize)
    title = buildContextSpannedString {
        appendItem(account.name.toUpperCase())
//        appendAccountMemberTag(context, account)
    }
    subtext = if (account.isLifetimeMember) createInstanceSpan(account.uid, context.getColor(R.color.tag_component)) else createInstanceSpan(account.uid)
}
fun BaseCell.bindUserV1(user: User, iconSize: IconSize = IconSize.COMPONENT_0) {
    bindKdenticonAvatar(user.name, iconSize)
    title = user.name.toUpperCase()
    subtext = buildContextSpannedString {
        when (user.chainId) {
            ChainConfig.Chain.CHAIN_ID_MAIN_NET -> appendInstanceSpan(user.uid)
            ChainConfig.Chain.CHAIN_ID_TEST_NET -> appendSimpleColoredMultiSpan(context.getString(R.string.chain_type_tag_testnet).toUpperCase() + modulon.extensions.charset.BLANK_SPACE + "UID", user.uid.toString(), context.getColor(R.color.tag_component_warning))
            else -> appendSimpleColoredMultiSpan(context.getString(R.string.chain_type_tag_unknown).toUpperCase() + modulon.extensions.charset.BLANK_SPACE + "UID", user.uid.toString(), context.getColor(R.color.tag_component_error))
        }
    }
}
fun BaseCell.bindUserDrawer(user: User, iconSize: IconSize = IconSize.COMPONENT_0) {
    bindKdenticonAvatar(user.name, iconSize)
    title = buildContextSpannedString {
        append(user.name.toUpperCase())
        when (user.chainId) {
            ChainConfig.Chain.CHAIN_ID_MAIN_NET -> appendInstanceSpan(user.uid)
            ChainConfig.Chain.CHAIN_ID_TEST_NET -> appendSimpleColoredMultiSpan(context.getString(R.string.chain_type_tag_testnet).toUpperCase() + modulon.extensions.charset.BLANK_SPACE + "UID", user.uid.toString(), context.getColor(R.color.tag_component_warning))
            else -> appendSimpleColoredMultiSpan(context.getString(R.string.chain_type_tag_unknown).toUpperCase() + modulon.extensions.charset.BLANK_SPACE + "UID", user.uid.toString(), context.getColor(R.color.tag_component_error))
        }
    }
}

// accounts / users v3
fun ComponentCell.bindAccountV3(account: AccountObject, showMemberType: Boolean, iconSize: IconSize = IconSize.COMPONENT_0) {
    bindKdenticonAvatar(account.name, iconSize)
    title = createAccountMemberInstanceDescription(account, showMemberType)
    text = account.name.toUpperCase()
}
fun ComponentCell.bindUserV3(user: User, iconSize: IconSize = IconSize.COMPONENT_0) {
    bindKdenticonAvatar(user.name, iconSize)
    title = createAccountMemberInstanceDescription(user.toAccount(), false)
    text = user.name.toUpperCase()
}

// assets
fun ComponentCell.bindAssetV1(asset: AssetObject) {
    text = buildContextSpannedString { appendAssetName(asset, true) }
    subtext = createInstanceSpan(asset.uid)
}
fun ComponentCell.bindAssetV3(asset: AssetObject, showAssetType: Boolean) {
    title = buildContextSpannedString { appendAssetInstanceDescription(asset, showAssetType) }
    text = buildContextSpannedString { appendAssetName(asset, false) }
}

// feed
fun ComponentCell.bindFeed(feed: PriceFeed, average: Price?) {
    bindAccountV3(feed.provider, true, IconSize.COMPONENT_0)
    subtext = buildContextSpannedString {
        append(context.getString(R.string.asset_feeds_feed_price), modulon.extensions.charset.BLANK_SPACE)
        appendColored(feed.settlementPrice.toString(), context.getColor(R.color.tag_light_green))
        appendBlankSpan()
        if (average != null) append("(${formatPercentage(((feed.settlementPrice.value - average.value) / average.value).toDouble(), 2)})")
        appendNewLine()
        append(context.getString(R.string.asset_feeds_core_exchange_rate), modulon.extensions.charset.BLANK_SPACE)
        appendColored(feed.coreExchangeRate.toString(), context.getColor(R.color.tag_text_primary))
        appendNewLine()
        append(context.getString(R.string.asset_feeds_maintenance_collateral_ratio), modulon.extensions.charset.BLANK_SPACE)
        appendColored(formatGrapheneRatio(feed.maintenanceCollateralRatio), context.getColor(R.color.tag_text_primary))
        append(modulon.extensions.charset.BLANK_SPACE, context.getString(R.string.asset_feeds_maximum_short_squeeze_ratio), modulon.extensions.charset.BLANK_SPACE)
        appendColored(formatGrapheneRatio(feed.maximumShortSqueezeRatio), context.getColor(R.color.tag_text_primary))
        appendNewLine()
        append(context.getString(R.string.asset_feeds_published), modulon.extensions.charset.BLANK_SPACE)
        appendColored(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(feed.time), context.getColor(R.color.tag_text_primary))
    }
}

// auth
fun ComponentCell.bindAccountAuth(account: AccountObject, threshold: UShort? = null) {
    titleView.typeface = typefaceMonoRegular
    titleView.textSize = 15f
    subtextView.typeface = typefaceMonoRegular
    title = account.name.toUpperCase(Locale.ROOT)
    if (threshold != null) subtitle = threshold.toString()
    subtext = account.id
    bindKdenticonAvatar(account.name, IconSize.SIZE_3)
    iconView.updateLayoutParams<LinearLayout.LayoutParams> {
        leftMargin = (-5).dp
        rightMargin = (-5).dp + componentOffset
    }
}
fun ComponentCell.bindPublicKey(key: PublicKey, threshold: UShort? = null) {
    icon = R.drawable.ic_tab_private_key_mode.contextDrawable()
    titleView.typeface = typefaceMonoRegular
    titleView.textSize = 15f
    titleView.isSingleLine = false
    subtextView.typeface = typefaceMonoRegular
    title = key.address.ifEmpty { context.getString(R.string.invalid_public_key) }
    if (threshold != null) subtitle = threshold.toStringOrEmpty()
}
fun ComponentCell.bindPrivateKey(key: PrivateKey, threshold: UShort? = null) {
    icon = when (key.type) {
        PrivateKey.KeyType.UNDEFINED -> R.drawable.ic_tab_private_key_mode
        PrivateKey.KeyType.SEED -> R.drawable.ic_tab_cloud_mode
        PrivateKey.KeyType.WIF -> R.drawable.ic_tab_private_key_mode
        PrivateKey.KeyType.MNEMONIC -> R.drawable.ic_tab_brain_key_mode
        PrivateKey.KeyType.RESTORE -> R.drawable.ic_tab_restore_mode
    }.contextDrawable()
    titleView.typeface = typefaceMonoRegular
    titleView.textSize = 15f
    subtextView.typeface = typefaceMonoRegular
    title = key.address.ifEmpty { context.getString(R.string.invalid_private_key) }
    subtext = key.wif
    if (threshold != null) subtitle = threshold.toStringOrEmpty()
    subtitleView.textColor = if (threshold == 0U.toUShort()) context.getColor(R.color.component_error) else context.getColor(R.color.component)
}

// block
fun ComponentCell.bindBlock(block: Block) {
    title = createSimpleMultiSpan(context.getString(R.string.block_height_prefix).toUpperCase(), block.blockNum.toString())
    subtitle = createSimpleReversedMultiSpan(block.transactionCount.toString(), context.getString(R.string.block_transaction_count))
//    subtitleView.apply {
//        typeface = typefaceMonoRegular
//        isSingleLine = true
//        ellipsize = TextUtils.TruncateAt.MIDDLE
//    }
//    text = block.hash
    subtitleView.isSingleLine = true
    subtext = buildContextSpannedString {
        appendItem(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(block.timestamp))
        appendBlankSpan()
        append(block.hash.takeLast(8))
        appendNewLine()
        appendAccountSpan(block.witness.witnessAccount)
    }
}

// ticker
fun ComponentCell.bindTicker(ticker: Ticker) {
    val invert = globalPreferenceManager.INVERT_COLOR.value
    val componentColor = when {
        ticker.percentChange > 0 -> if (invert) context.getColor(R.color.component_error) else context.getColor(R.color.component_active)
        ticker.percentChange < 0 -> if (invert) context.getColor(R.color.component_active) else context.getColor(R.color.component_error)
        else -> context.getColor(R.color.component_inactive)
    }
    title = buildContextSpannedString {
        if (ticker.quote.isExist) appendAssetName(ticker.quote) else appendItem(context.getString(R.string.invalid_number))
        appendScaled(modulon.extensions.charset.BLANK_SPACE, 0.6f)
        appendColored(createScaled(SEPARATOR, 0.8f), context.getColor(R.color.text_secondary))
        appendScaled(modulon.extensions.charset.BLANK_SPACE, 0.6f)
        appendColored(context.getColor(R.color.text_secondary)) {
            appendScaled(0.8f) {
                if (ticker.base.isExist) appendAssetName(ticker.base) else appendItem(context.getString(R.string.invalid_number))
            }
        }
    }
    subtitleView.textColor = componentColor
    subtitle = buildContextSpannedString {
        append(modulon.extensions.charset.ZERO_WIDTH_BLANK_SPACE)
        if (ticker.latest.isNaN()) {
            appendScaled(context.getString(R.string.invalid_number), 0.96f)
        } else {
            appendScaled("${formatAssetBigDecimal(ticker.latest, ticker.base.precision + ticker.quote.precision).toPlainString()} ${ticker.base.symbolOrId}", 0.96f)
        }

    }
    text = buildContextSpannedString {
        if (ticker.latest.isNaN()) {
            appendColored(context.getString(R.string.invalid_number), componentColor)
        } else {
            val symbol = when {
                ticker.percentChange > 0 -> "↑" //↑
                ticker.percentChange < 0 -> "↓" //↓
                else -> ""
            }
            appendColored("$symbol${abs(ticker.percentChange)}%", componentColor)
        }
    }
    tableView.tables = mapOf(
        context.getString(R.string.ticker_high) + modulon.extensions.charset.BLANK_SPACE to buildContextSpannedString {
            if (ticker.lowestAsk.isNaN()) {
                appendItem(context.getString(R.string.invalid_number))
            } else {
                appendSimpleMultiSpan(formatAssetBigDecimal(ticker.lowestAsk, ticker.base.precision + ticker.quote.precision).formatNumber(), ticker.base.symbolOrId, false, FONT_SCALE_FACTOR_100)
            }
        },
        context.getString(R.string.ticker_low) + modulon.extensions.charset.BLANK_SPACE to buildContextSpannedString {
            if (ticker.highestBid.isNaN()) {
                appendItem(context.getString(R.string.invalid_number))
            } else {
                appendSimpleMultiSpan(formatAssetBigDecimal(ticker.highestBid, ticker.base.precision + ticker.quote.precision).formatNumber(), ticker.base.symbolOrId, false, FONT_SCALE_FACTOR_100)
            }
        },
        context.getString(R.string.ticker_volume) + modulon.extensions.charset.BLANK_SPACE to buildContextSpannedString {
            if (ticker.highestBid.isNaN()) {
                appendItem(context.getString(R.string.invalid_number))
            } else {
                appendSimpleMultiSpan(formatAssetBigDecimal(ticker.quoteVolume, ticker.base.precision + ticker.quote.precision).formatSuffix(), ticker.quote.symbolOrId, false, FONT_SCALE_FACTOR_100)
            }
        }
    )

}

// node
fun ComponentCell.bindNode(node: Node) {
    title = buildContextSpannedString {
        append(node.name.ifBlank { Uri.parse(node.url).host })
        when (node.chainId) {
            ChainConfig.Chain.CHAIN_ID_MAIN_NET -> appendSimpleColoredSpan(context.getString(R.string.chain_type_bitshares_mainnet).toUpperCase(), context.getColor(R.color.tag_component))
            ChainConfig.Chain.CHAIN_ID_TEST_NET -> appendSimpleColoredSpan(context.getString(R.string.chain_type_bitshares_testnet).toUpperCase(), context.getColor(R.color.tag_component_warning))
            else -> appendSimpleColoredSpan(context.getString(R.string.chain_type_unknown_network).toUpperCase(), context.getColor(R.color.tag_component_inactive))
        }
    }
    textView.startScrolling()
//    subtitleView.isVisible = node.apis.any { it }
//    subtitleView.text = buildContextSpannedString {
//        node.apis.forEachIndexed { index, bool ->
//            if (bool) when (index) {
//                0 -> appendSimpleSpan(context, context.resources.getStringArray(R.array.node_types)[2].toUpperCase())
//                1 -> appendSimpleSpan(context, context.resources.getStringArray(R.array.node_types)[3].toUpperCase())
//                2 -> appendSimpleSpan(context, context.resources.getStringArray(R.array.node_types)[4].toUpperCase())
//                3 -> appendSimpleSpan(context, context.resources.getStringArray(R.array.node_types)[8].toUpperCase())
//            }
//        }
//    }
//    textView1.text = node.name.ifBlank { Uri.parse(node.url).host }
    when (node.latency) {
        Node.LATENCY_CONNECTING -> {
            subtext = context.getString(R.string.node_settings_connecting)
            subtextView.textColor = context.getColor(R.color.cell_text_primary)
        }
        Node.LATENCY_TIMEOUT -> {
            subtext = context.getString(R.string.node_settings_timeout)
            subtextView.textColor = context.getColor(R.color.component_error)
        }
        else -> {
            if (Settings.KEY_CURRENT_NODE_ID.value == node.id) {
                subtext = "${context.getString(R.string.node_settings_connected)}, ${context.getString(R.string.node_settings_latency)} ${node.latency} ${context.getString(R.string.node_settings_latency_ms)}"
                subtextView.textColor = context.getColor(R.color.component_active)
            } else {
                subtext = "${context.getString(R.string.node_settings_latency)} ${node.latency} ${context.getString(R.string.node_settings_latency_ms)}"
                subtextView.textColor = context.getColor(R.color.cell_text_secondary)
            }
        }
    }
}

fun ComponentCell.bindNode(node: BitsharesNode) {
    title = buildContextSpannedString {
        append(node.name.ifBlank { Uri.parse(node.url).host })
        when (node.chainId) {
            ChainConfig.Chain.CHAIN_ID_MAIN_NET -> appendSimpleColoredSpan(context.getString(R.string.chain_type_bitshares_mainnet).toUpperCase(), context.getColor(R.color.tag_component))
            ChainConfig.Chain.CHAIN_ID_TEST_NET -> appendSimpleColoredSpan(context.getString(R.string.chain_type_bitshares_testnet).toUpperCase(), context.getColor(R.color.tag_component_warning))
            else -> appendSimpleColoredSpan(context.getString(R.string.chain_type_unknown_network).toUpperCase(), context.getColor(R.color.tag_component_inactive))
        }
    }
    titleView.startScrolling()
    subtext = node.url
    subtitle = "${node.latency} ${context.getString(R.string.node_settings_latency_ms)}"
    subtitleView.textColor = context.getColor(R.color.cell_text_secondary)
}

fun ComponentCell.bindNode(node: BitsharesNode, isSelected: Boolean, isActive: Boolean) {
    title = buildContextSpannedString {
        append(node.name.ifBlank { node.url.toUri().host })
        appendBlankSpan()
        appendScaled(0.9f) {
            when (node.chainId) {
                ChainConfig.Chain.CHAIN_ID_MAIN_NET ->
                    appendSimpleColoredSpan(context.getString(R.string.chain_type_bitshares_mainnet).toUpperCase(), context.getColor(R.color.tag_component))
                ChainConfig.Chain.CHAIN_ID_TEST_NET ->
                    appendSimpleColoredSpan(context.getString(R.string.chain_type_bitshares_testnet).toUpperCase(), context.getColor(R.color.tag_component_warning))
                else ->
                    appendSimpleColoredSpan(context.getString(R.string.chain_type_unknown_network).toUpperCase(), context.getColor(R.color.tag_component_inactive))
            }
        }
    }
    textView.startScrolling()
    subtext = node.url
    when (node.latency) {
        BitsharesNode.LATENCY_CONNECTING -> {
            subtitle = context.getString(R.string.node_settings_connecting)
            subtitleView.textColor = context.getColor(R.color.cell_text_secondary)
        }
        BitsharesNode.LATENCY_TIMEOUT, BitsharesNode.LATENCY_UNRESOLVED, BitsharesNode.LATENCY_UNKNOWN -> {
            subtitle = context.getString(R.string.node_settings_timeout)
            subtitleView.textColor = context.getColor(R.color.component_error)
        }
        else -> {
            subtitle = "${node.latency} ${context.getString(R.string.node_settings_latency_ms)}"
            subtitleView.textColor = context.getColor(R.color.cell_text_secondary)

        }
//            when {
//                isActive -> {
//                    subtitle = "${context.getString(R.string.node_settings_connected)}, ${context.getString(R.string.node_settings_latency)} ${node.latency} ${context.getString(R.string.node_settings_latency_ms)}"
//                    subtitleView.textColor = context.getColor(R.color.component_active)
//                }
//                isSelected -> {
//                    subtitle = context.getString(R.string.node_settings_connecting) + "${context.getString(R.string.node_settings_latency)} ${node.latency} ${context.getString(R.string.node_settings_latency_ms)}"
//                    subtitleView.textColor = context.getColor(R.color.cell_text_secondary)
//                }
//                else -> {
//                    subtitle = "${context.getString(R.string.node_settings_latency)} ${node.latency} ${context.getString(R.string.node_settings_latency_ms)}"
//                    subtitleView.textColor = context.getColor(R.color.cell_text_secondary)
//                }
//            }
    }
    checkView.backgroundTintColor = context.getColor(if (isSelected && !isActive) R.color.component_warning else R.color.component)
    isChecked = isSelected || isActive
}


// operation
fun ComponentCell.bindOperation(op: Operation) {
    doOnClick()
//    bindOperationDescription(op)
    title = createOperationNameSpan(op)
    // TODO: 19/10/2021 replace all DateFormat.getDateTimeInstance with text span
//    text = buildContextSpannedString {  }
//    when (op) {
//        is LimitOrderCreateOperation -> {
//            var inverted = op.isBid
//            doOnClick {
//                inverted = !inverted
//                text = buildContextSpannedString { appendLimitOrderCreateOperationDescriptionSpan(context, op, inverted) }
//            }
//        }
//        is FillOrderOperation -> {
//            var inverted = op.isBid
//            doOnClick {
//                inverted = !inverted
//                text = buildContextSpannedString { appendFillOrderOperationDescriptionSpan(context, op, inverted) }
//            }
//        }
//    }
    subtext = buildContextSpannedString {
        appendOperationDescriptionSpan(op)
    }
    // FIXME: 2022/4/24  
//    subtext(0) {
//        startScrolling()
//    }
    subtext(1) {
        startScrolling()
        text = buildContextSpannedString {
            appendDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, op.createTime)
            appendBlockHeightSpan(op.blockHeight)
        }
    }
}
fun ComponentCell.bindOperationDescription(op: Operation) {
    text = buildContextSpannedString { appendOperationDescriptionSpan(op) }
    when (op) {
        is LimitOrderCreateOperation -> {
            var inverted = op.isBid
            doOnClick {
                inverted = !inverted
                text = buildContextSpannedString { appendLimitOrderCreateOperationDescriptionSpan(op, inverted) }
            }
        }
        is FillOrderOperation -> {
            var inverted = op.isBid
            doOnClick {
                inverted = !inverted
                text = buildContextSpannedString { appendFillOrderOperationDescriptionSpan(op, inverted) }
            }
        }
    }
}

// account balance
fun ComponentCell.bindAccountBalance(balance: AccountBalanceObject) {
    text = buildContextSpannedString { appendAssetName(balance.asset, true) }
    subtext = formatAssetBalance(balance.balance, balance.asset)
}
fun ComponentCell.bindAccountBalanceTable(balance: AccountBalance) {
    title = buildContextSpannedString { appendAssetName(balance.balance.asset, true) }
    tableView.tables = buildTable {
        if (balance.limitOrders.isNotEmpty() || balance.callOrders.isNotEmpty()) {
            context.getString(R.string.account_balance_total) to buildContextSpannedString { appendSimpleAssetAmountSpan(balance.totalAmount) }
        }
        context.getString(R.string.account_balance_balances) to buildContextSpannedString { appendSimpleAssetAmountSpan(balance.balanceAmount) }
        if (balance.limitOrders.isNotEmpty()) {
            context.getString(R.string.account_balance_in_order) to buildContextSpannedString { appendSimpleAssetAmountSpan(balance.limitAmount) }
        }
        if (balance.callOrders.isNotEmpty()) {
            if (balance.isDebt) {
                context.getString(R.string.account_balance_debt)
            } else {
                context.getString(R.string.account_balance_collateral)
            } to buildContextSpannedString { appendSimpleAssetAmountSpan(balance.callAmount) }
        }
    }
}