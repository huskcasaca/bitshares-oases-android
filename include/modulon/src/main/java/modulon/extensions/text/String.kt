package modulon.extensions.text

import android.content.Context
import android.text.SpannableStringBuilder
import modulon.extensions.charset.EMPTY_SPACE
import java.text.DecimalFormat
import java.util.*
import kotlin.math.log10
import kotlin.math.pow

fun Any?.toStringOrEmpty() = this?.toString() ?: EMPTY_SPACE

fun Any?.toStringOrNull() = this?.toString()

fun Any?.toStringOrDefault(defaultValue: String) = this?.toString() ?: defaultValue

fun formatTimeStringFromSec(second: Int): String {
    val hours = second / 60 / 60
    val minutes = second.rem(60 * 60) / 60
    val seconds = second.rem(60)
    return if (hours != 0) "$hours Hours " else "" + if (minutes != 0) (if (hours != 0) " " else "") + "$minutes Minutes" else "" + if (seconds != 0 || (hours == 0 && minutes == 0)) (if (hours != 0 || minutes != 0) " " else "") + "$seconds Seconds " else ""
}

fun CharSequence?.toStringOrEmpty(): String = this?.toString().orEmpty()

fun CharSequence?.toStringOrEmpty(block: () -> CharSequence): String = this?.toString() ?: block.invoke().toString()

/**
 * Recursive implementation, invokes itself for each factor of a thousand, increasing the class on each invokation.
 * @param iteration in fact this is the class from the array c
 * @return a String representing the number n formatted in a cool looking way.
 */
tailrec fun Number.coolFormat(iteration: Int = 0): String {
    val d = this.toLong() / 100 / 10.0
    val isRound = d * 10 % 10 == 0.0 //true if the decimal part is equal to 0 (then it's trimmed anyway)
    return if (d < 1000) //this determines the class, i.e. 'k', 'm' etc
        (if (d > 99.9 || isRound || !isRound && d > 9.99) //this decides whether to trim the decimals
            d.toInt() * 10 / 10 else d.toString() + "" // (int) d * 10 / 10 drops the decimal
                ).toString() + " " + listOf("K", "M", "B", "T")[iteration] else d.coolFormat(iteration + 1)
}

fun Number.formatSuffix(iteration: Int = 0): String {
    val d = toDouble().toLong() / 1000.0
    return if (d < 1000) "$d${listOf("K", "M", "B", "T")[iteration]}" else d.formatSuffix(iteration + 1)
}

fun Long.formatBinaryPrefix(): String {
    if (this <= 0) return "0"
    val units = listOf("B", "KiB", "MiB", "GiB", "TiB")
    val digitGroups = (log10(toDouble()) / log10(1024.0)).toInt()
    return DecimalFormat("#,##0.#").format(this / 1024.0.pow(digitGroups.toDouble())).toString() + " " + units[digitGroups]
}

val String.isUpperCase get() = toUpperCase(Locale.ROOT).contentEquals(this)

val String.isLowerCase get() = toLowerCase(Locale.ROOT).contentEquals(this)


// TODO: 22/10/2021
fun SpannableStringBuilder.appendTimeFromSec(context: Context, second: Int) = apply {
    val hours = second / 60 / 60
    val minutes = second.rem(60 * 60) / 60
    val seconds = second.rem(60)

    if (hours != 0) "$hours Hours " else "" +
            if (minutes != 0) (if (hours != 0) " " else "") + "$minutes Minutes" else "" +
                    if (seconds != 0 || (hours == 0 && minutes == 0))
                        (if (hours != 0 || minutes != 0) " " else "") + "$seconds Seconds " else ""
}