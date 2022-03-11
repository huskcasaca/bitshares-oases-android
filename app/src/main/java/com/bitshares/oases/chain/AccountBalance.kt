package com.bitshares.oases.chain

import bitshareskit.extensions.collateralAmount
import bitshareskit.extensions.debtAmount
import bitshareskit.extensions.isInstanceOf
import bitshareskit.extensions.salesAmount
import bitshareskit.models.AssetAmount
import bitshareskit.objects.AccountBalanceObject
import bitshareskit.objects.CallOrderObject
import bitshareskit.objects.LimitOrderObject

data class AccountBalance(
    val balance: AccountBalanceObject,
    val limitOrders: List<LimitOrderObject>,
    val callOrders: List<CallOrderObject>,
    val value: AssetAmount? = null
) {

    val balanceAmount get() = balance.amount

    // FIXME: 2022/2/28 lintVitalAnalyzeRelease Caused by: java.lang.IndexOutOfBoundsException: Empty list doesn't contain element at index 0.
    val limitAmount get() = AssetAmount(limitOrders.sumOf { it.salesAmount.amount }, balanceAmount.asset)

    val isDebt get() = callOrders.isNotEmpty() && callOrders.first().debtAmount.asset.isInstanceOf(balanceAmount.asset)
    // FIXME: 2022/2/28 lintVitalAnalyzeRelease Caused by: java.lang.IndexOutOfBoundsException: Empty list doesn't contain element at index 0.
    val callAmount get() = AssetAmount(callOrders.sumOf { if (isDebt) it.debtAmount.amount else it.collateralAmount.amount }, balanceAmount.asset)

    val totalAmount get() = balanceAmount + limitAmount + if (isDebt) - callAmount else callAmount

}