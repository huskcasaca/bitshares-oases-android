package com.bitshares.oases.extensions.text

import android.text.SpannedString
import com.bitshares.oases.R
import modulon.extensions.charset.EMPTY_SPACE
import modulon.extensions.text.*
import modulon.union.UnionContext

object StringFilter {

    const val LENGTH_MASK = 1 shl 28
    const val CONTAINS_MASK = 1 shl 30

    const val LENGTH_AT_LEAST_6 = 1 shl 0 or LENGTH_MASK
    const val LENGTH_AT_LEAST_8 = 1 shl 1 or LENGTH_MASK
    const val LENGTH_AT_LEAST_12 = 1 shl 2 or LENGTH_MASK

    const val CONTAINS_NUMBER = 1 shl 0 or CONTAINS_MASK
    const val CONTAINS_ALPHABET = 1 shl 1 or CONTAINS_MASK
    const val CONTAINS_UPPER_CASE = 1 shl 2 or CONTAINS_MASK
    const val CONTAINS_LOWER_CASE = 1 shl 3 or CONTAINS_MASK
    const val CONTAINS_SYMBOL = 1 shl 4 or CONTAINS_MASK

    val NUMBER_REGEX = Regex("[0-9]+")
    val LOWER_CASE_LETTER_REGEX = Regex("[a-z]+")
    val UPPER_CASE_LETTER_REGEX = Regex("[A-Z]+")
    val LETTER_REGEX = Regex("[a-zA-Z]+")

    const val FILTER_PASSWORD_STRENGTH_DIGITS_SIMPLE = 0x00

    const val FILTER_PASSWORD_STRENGTH_NORMAL = 0x01


    const val FILTER_PASSWORD_STRENGTH_REGISTER = 0x02

    const val FILTER_CHEAP_ACCOUNT_NAME = 0x10


    const val FILTER_REQUIRE_EQUALS = 0xA0
}



private fun String.matchesNumber() = matches(StringFilter.NUMBER_REGEX)
private fun String.containsNumber() = contains(StringFilter.NUMBER_REGEX)
private fun String.containsLower() = contains(StringFilter.LOWER_CASE_LETTER_REGEX)
private fun String.containsUpper() = contains(StringFilter.UPPER_CASE_LETTER_REGEX)
private fun String.containsLetter() = contains(StringFilter.LETTER_REGEX)
private fun String.atLeast(len: Int) = length >= len
private fun String.atMost(len: Int) = length <= len


private fun String.endsWithDash() = endsWith('-')


private fun UnionContext.color(string: String, valid: Boolean): SpannedString {
    val validColor = context.getColor(R.color.component_active)
    val invalidColor = context.getColor(R.color.component_error)
    return createColored(string, if (valid) validColor else invalidColor )
}


fun validateStringFilter(field: String, filter: Int): Boolean {
    return field.run {
        when (filter) {
            StringFilter.FILTER_PASSWORD_STRENGTH_DIGITS_SIMPLE ->  matchesNumber() && atLeast(6)
            StringFilter.FILTER_PASSWORD_STRENGTH_NORMAL ->         containsLetter() && containsNumber() && atLeast(8)
            StringFilter.FILTER_PASSWORD_STRENGTH_REGISTER ->       containsUpper() && containsLower() && containsNumber() && atLeast(12)
            StringFilter.FILTER_CHEAP_ACCOUNT_NAME ->               containsLetter() && containsNumber() && !endsWithDash() && atLeast(8)
            else -> throwFilterType(filter)
        }
    }
}

fun validateStringFilter(field1: String, field2: String, filter: Int): Boolean {
    return when (filter) {
        StringFilter.FILTER_REQUIRE_EQUALS -> field1.contentEquals(field2)
        else -> throwFilterType(filter)
    }
}


fun UnionContext.createStringFilterHint(field: String, filter: Int): CharSequence {
    if (validateStringFilter(field, filter)) return EMPTY_SPACE
    return field.run {
        when (filter) {
            StringFilter.FILTER_PASSWORD_STRENGTH_DIGITS_SIMPLE -> {
                color(context.getString(R.string.password_requirement_description_normal_6_digits), matchesNumber() && atLeast(6))
            }
            StringFilter.FILTER_PASSWORD_STRENGTH_NORMAL -> {
                val string = context.getString(R.string.password_requirement_description_normal)
                val length = color(context.getString(R.string.password_requirement_at_least_8_characters), atLeast(8))
                val letter = color(context.getString(R.string.password_requirement_a_letter), containsLetter())
                val number = color(context.getString(R.string.password_requirement_a_number), containsNumber())
                createFormatArguments(string, length, letter, number)
            }
            StringFilter.FILTER_PASSWORD_STRENGTH_REGISTER -> {
                val string = context.getString(R.string.password_requirement_description_triple)
                val length = color(context.getString(R.string.password_requirement_at_least_12_characters), atLeast(12))
                val upper = color(context.getString(R.string.password_requirement_a_uppercase_letter), containsUpper())
                val lower = color(context.getString(R.string.password_requirement_a_lowercase_letter), containsLower())
                val number = color(context.getString(R.string.password_requirement_a_number), containsNumber())
                createFormatArguments(string, length, upper, lower, number)
            }
            StringFilter.FILTER_CHEAP_ACCOUNT_NAME -> {
                val string = context.getString(R.string.account_name_requirement_description_normal)
                val dash = color(context.getString(R.string.account_name_requirement_dash_end), !endsWithDash())
                val length = color(context.getString(R.string.password_requirement_at_least_8_characters), atLeast(8))
                val letter = color(context.getString(R.string.password_requirement_a_letter), containsLetter())
                val number = color(context.getString(R.string.password_requirement_a_number), containsNumber())
                buildContextSpannedString {
                    appendFormatArguments(string, length, letter, number)
                    if (endsWithDash()) {
                        appendNewLine()
                        appendItem(dash)
                    }
                }
            }
            else -> throwFilterType(filter)
        }
    }
}

fun UnionContext.createStringFilterHint(field1: String, field2: String, filter: Int): CharSequence {
    if (validateStringFilter(field1, field2, filter)) return EMPTY_SPACE
    val validColor = context.getColor(R.color.component_active)
    val invalidColor = context.getColor(R.color.component_error)
    return when (filter) {
        StringFilter.FILTER_REQUIRE_EQUALS -> {
            color(context.getString(R.string.filter_fields_not_match), field1.contentEquals(field2))
        }
        else -> throwFilterType(filter)
    }
}

fun throwFilterType(filter: Int): Nothing = throw IllegalArgumentException("Unsupported filter type: $filter")

