package com.bitshares.oases.chain

import com.bitshares.oases.MainApplication
import com.bitshares.oases.preference.old.I18N
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

object BrainKeyDict {

    private const val ENGLISH_DICT_NAME = "english.txt"
    private const val RUSSIAN_DICT_NAME = "russian.txt"
    private const val SIMPLIFIED_CHINESE_DICT_NAME = "chinese_simplified.txt"
    private const val TRADITIONAL_CHINESE_DICT_NAME = "chinese_traditional.txt"

    private val DICT_ENGLISH by lazy { BufferedReader(InputStreamReader(MainApplication.context.assets.open(ENGLISH_DICT_NAME), "UTF-8")).readLines() }
    private val DICT_RUSSIAN by lazy { BufferedReader(InputStreamReader(MainApplication.context.assets.open(RUSSIAN_DICT_NAME), "UTF-8")).readLines() }
    private val DICT_SIMPLIFIED_CHINESE by lazy { BufferedReader(InputStreamReader(MainApplication.context.assets.open(SIMPLIFIED_CHINESE_DICT_NAME), "UTF-8")).readLines() }
    private val DICT_TRADITIONAL_CHINESE by lazy { BufferedReader(InputStreamReader(MainApplication.context.assets.open(TRADITIONAL_CHINESE_DICT_NAME), "UTF-8")).readLines() }

    fun getDictionary(locale: Locale = Locale.getDefault()): List<String> {
        return when (locale) {
            I18N.ENGLISH.locale -> DICT_ENGLISH
            I18N.RUSSIAN.locale -> DICT_RUSSIAN
            I18N.SIMPLIFIED_CHINESE.locale -> DICT_SIMPLIFIED_CHINESE
            I18N.TRADITIONAL_CHINESE.locale -> DICT_TRADITIONAL_CHINESE
            else -> DICT_ENGLISH
        }
    }

    val DICT_ALL by lazy { DICT_ENGLISH + DICT_RUSSIAN + DICT_SIMPLIFIED_CHINESE + DICT_TRADITIONAL_CHINESE }

}