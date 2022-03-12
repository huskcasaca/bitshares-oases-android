package com.bitshares.oases.preference.old

import android.content.Context
import java.util.*

enum class I18N(val locale: Locale, val localizedName: String) {
    DEFAULT(Locale.ROOT, "Default"),
    ENGLISH(Locale.ENGLISH, "English (United State)"),
    RUSSIAN(Locale("ru"), "Русский (Россия)"),
    SIMPLIFIED_CHINESE(Locale.SIMPLIFIED_CHINESE, "简体中文 (中国)"),
    TRADITIONAL_CHINESE(Locale.TRADITIONAL_CHINESE, "繁体中文 (中国)"),
}

fun Context.createLocalContext(locale: Locale): Context {
    Locale.setDefault(locale)
    val conf = resources.configuration.apply {
        setLocale(locale)
        setLayoutDirection(locale)
    }
    return createConfigurationContext(conf)

}