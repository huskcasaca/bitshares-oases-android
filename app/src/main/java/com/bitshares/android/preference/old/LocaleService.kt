package com.bitshares.android.preference.old

import android.content.Context
import java.util.*

enum class I18N(val locale: Locale, val localizedName: String) {
    DEFAULT(Locale.ROOT, "Default"),
    ENGLISH(Locale.ENGLISH, "English (United State)"),
    RUSSIAN(Locale("ru"), "Русский (Россия)"),
    SIMPLIFIED_CHINESE(Locale.SIMPLIFIED_CHINESE, "简体中文 (中国)"),
    TRADITIONAL_CHINESE(Locale.TRADITIONAL_CHINESE, "繁体中文 (中国)"),
}

object LocaleService {

    fun updateLocale(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)
        val conf = context.resources.configuration.apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
        return context.createConfigurationContext(conf)
    }

    fun setLocale(locale: Int) {
        Settings.KEY_LANGUAGE.value = locale
    }

}
