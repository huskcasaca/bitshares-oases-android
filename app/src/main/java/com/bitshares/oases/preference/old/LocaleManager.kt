package com.bitshares.oases.preference.old

import android.content.Context
import com.bitshares.oases.MainApplication
import java.util.*


class LocaleManager(private val application: MainApplication) {

    fun updateLocale(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)
        val conf = context.resources.configuration.apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
        return context.createConfigurationContext(conf)
    }


}
