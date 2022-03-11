package com.bitshares.oases.netowrk.java_websocket

/**
 * Class used to compute the Exponential Moving Average with stability fixes of a sequence of values.
 * @see [Exponential Moving Average](https://en.wikipedia.org/wiki/Moving_average#Exponential_moving_average).
 */

class StabledMovingAverage(a: Double = DEFAULT_ALPHA) {

    companion object {
        const val DEFAULT_ALPHA = 0.2
    }

    private var accumulatedValue: Double? = null

    /**
     * Variable [alpha] represents the degree of weighting decrease, a constant smoothing factor
     * between 0 and 1. A higher alpha discounts older observations faster.
     */
    private var alpha: Double = a
        set(value: Double) {
            field = value
            accumulatedValue = null
        }

    /**  The current average value. */
    val average: Double
        get() = if (accumulatedValue == null) 0.0 else accumulatedValue as Double

    /**
     * Method that updates the average with a new sample
     * @param [data] New value
     * @return       The updated average value
     */
    fun update(data: Number): Double {
        val value = data.toDouble()
        if (accumulatedValue == null) {
            accumulatedValue = value
            return value
        }
        // Prevent unexpected violation
        if (value * 0.5 >= accumulatedValue!!) {
            return accumulatedValue!!
        }
        val newValue = accumulatedValue!! + alpha * (value - accumulatedValue!!)
        accumulatedValue = newValue
        return newValue
    }

}