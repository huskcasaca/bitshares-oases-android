package com.bitshares.oases.netowrk.java_websocket

import kotlinx.atomicfu.atomic

/**
 * Class used to compute the Exponential Moving Average with stability fixes of a sequence of values.
 * @see [Exponential Moving Average](https://en.wikipedia.org/wiki/Moving_average#Exponential_moving_average).
 */

class StabledMovingAverage(a: Double = DEFAULT_ALPHA) {

    companion object {
        const val DEFAULT_ALPHA = 0.2
    }

    private var accumulatedValue: Double = 0.0
    private var isInitialized = false

    /**
     * Variable [alpha] represents the degree of weighting decrease, a constant smoothing factor
     * between 0 and 1. A higher alpha discounts older observations faster.
     */
    private var alpha: Double = a
        set(value: Double) {
            field = value
            synchronized(this) {
                isInitialized = false
                accumulatedValue = 0.0
            }
        }

    /**  The current average value. */
    val value: Double
        get() = if (isInitialized) accumulatedValue else 0.0

    /**
     * Method that updates the average with a new sample
     * @param [data] New value
     * @return       The updated average value
     */

    fun update(value: Double): Double {
        synchronized(this) {
            if (!isInitialized) {
                isInitialized = true
                accumulatedValue = value
                return value
            }
//            // Prevent unexpected violation
//            if (value * 0.5 >= accumulatedValue) {
//                return accumulatedValue
//            }
            val newValue = accumulatedValue + alpha * (value - accumulatedValue)
            accumulatedValue = newValue
            return newValue
        }
    }

}