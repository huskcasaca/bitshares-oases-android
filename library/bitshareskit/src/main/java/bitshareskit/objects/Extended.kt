package bitshareskit.objects

import bitshareskit.models.AssetAmount
import bitshareskit.models.SimplePrice


data class CallOrder(
    val order: CallOrderObject,
    val borrower: AccountObject,
    val collateral: AssetAmount,
    val debt: AssetAmount,
    val callPrice: SimplePrice
)


