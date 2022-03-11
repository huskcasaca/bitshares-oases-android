package com.bitshares.oases.extensions.viewbinder

import bitshareskit.entities.Block
import bitshareskit.entities.LimitOrder
import bitshareskit.extensions.isInMarket
import bitshareskit.extensions.rebaseInvert
import bitshareskit.extensions.symbolOrId
import bitshareskit.models.Price
import bitshareskit.models.Transaction
import bitshareskit.objects.AssetObject
import bitshareskit.objects.CallOrder
import bitshareskit.objects.GrapheneObject
import bitshareskit.objects.JsonSerializable
import bitshareskit.operations.Operation
import com.bitshares.oases.R
import com.bitshares.oases.extensions.text.*
import modulon.component.ComponentCell
import modulon.component.tables
import modulon.extensions.charset.EMPTY_SPACE
import modulon.extensions.font.typefaceMonoRegular
import modulon.extensions.text.TABULAR_TRANSFORMATION_METHOD
import modulon.extensions.text.appendColored
import modulon.extensions.text.appendMediumDateTimeInstance
import modulon.extensions.text.buildContextSpannedString
import modulon.extensions.view.doOnClick
import modulon.extensions.view.dp
import modulon.extensions.view.updatePaddingVerticalV6
import modulon.extensions.viewbinder.cell
import modulon.extensions.viewbinder.spacer
import modulon.layout.linear.VerticalLayout
import org.java_json.JSONArray
import org.java_json.JSONObject

const val SEPARATOR = "/"

internal class TextTable {
    private val map: MutableMap<CharSequence, CharSequence> = mutableMapOf()
    infix fun CharSequence.to(value: CharSequence) {
        map[this] = value
    }
    fun build() = map
}
internal fun buildTable(block: TextTable.() -> Unit) = TextTable().apply(block).build()

fun ComponentCell.bindRawData(item: JsonSerializable) {
    textView.isSingleLine = false
    textView.typeface = typefaceMonoRegular
    textView.textSize = 12f
    val rawJson = when  (item) {
        is GrapheneObject -> item.toJsonObject().toString(2)
        is Operation -> item.rawJsonTemp.toString(2)
        is Block -> item.rawJson.toString(2)
        else -> when (val element = item?.toJsonElement()) {
            is JSONObject -> element.toString(2)
            is JSONArray -> element.toString(2)
            else -> element.toString()
        }
    }
    text = rawJson
}

// TODO: 2022/2/11 remove
fun ComponentCell.bindNothing() {
    title = EMPTY_SPACE
    subtitle = EMPTY_SPACE
    text = EMPTY_SPACE
    subtext = EMPTY_SPACE
}

fun ComponentCell.bindMarketGroup(asset: AssetObject) {
    titleView.textSize = 22f
    subtextView.typeface = typefaceMonoRegular
    if (!asset.isExist) {
        title = context.getString(R.string.invalid_number)
        subtext = context.getString(R.string.invalid_number)
    } else {
        title = asset.symbolOrId
        subtext = asset.id
    }
}

fun ComponentCell.bindLimitOrderTable(order: LimitOrder) {
    title = if (order.isInMarket(order.market)) createLimitOrderInstanceDescription(order.order, order.isBuy) else createLimitOrderInstanceDescription(order.order)
    tableView.tables = buildTable {
        context.getString(R.string.limit_order_price) to buildContextSpannedString { appendRealPriceSpan(order.price) }
        context.getString(R.string.limit_order_amount) to buildContextSpannedString { appendAssetAmountSpan(order.totalSales) }
        context.getString(R.string.limit_order_total) to buildContextSpannedString { appendAssetAmountSpan(order.totalReceives) }
        context.getString(R.string.limit_order_expiration) to buildContextSpannedString { appendColored(context.getColor(R.color.tag_text_primary)) { appendMediumDateTimeInstance(order.order.expiration) } }
    }
}

fun ComponentCell.bindCallOrderTable(order: CallOrder) {
    title = buildContextSpannedString { appendCallOrderDescription(order) }
    tableView.tables = buildTable {
        "Call Price" to buildContextSpannedString { appendPriceSpan(order.callPrice.rebaseInvert(AssetObject.CORE_ASSET)) }
        "Debt" to buildContextSpannedString { appendAssetAmountSpan(order.debt) }
        "Collateral" to buildContextSpannedString { appendAssetAmountSpan(order.collateral) }
    }
}


fun ComponentCell.bindPrice(price: Price) {
    subtitleView.transformationMethod = TABULAR_TRANSFORMATION_METHOD
    subtitle = price.toString()
    doOnClick {
        price.isInverted = !price.isInverted
        subtitle = price.toString()
    }
}

fun VerticalLayout.bindTransaction(transaction: Transaction) {
    removeAllViews()
    cell {
        title = "Transaction ${transaction}"
    }
    transaction.operations.forEach {
        cell {
            updatePaddingVerticalV6()
            bindOperation(it)
        }
    }
    spacer { height = 8.dp }
}
