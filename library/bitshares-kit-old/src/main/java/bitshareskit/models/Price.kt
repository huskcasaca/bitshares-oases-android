package bitshareskit.models

import java.math.BigDecimal

interface Price {

    val base: AssetAmount
    val quote: AssetAmount

    val value: BigDecimal
    val valueInverted: BigDecimal

    val realValue: BigDecimal
    val realValueInverted: BigDecimal

    val invertedPair: Price
    var isInverted: Boolean
    val isValid: Boolean


    
}

