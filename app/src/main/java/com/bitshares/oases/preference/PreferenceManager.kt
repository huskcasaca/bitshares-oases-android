package com.bitshares.oases.preference

import android.content.Context
import bitshareskit.chain.ChainConfig
import com.bitshares.oases.preference.old.I18N
import com.bitshares.oases.preference.old.livePreference
import java.util.*

class PreferenceManager(
    context: Context
): AbstractPreferenceManager(context) {

    // TODO: 2022/4/18 use delegate

    val IS_INITIALIZED = livePreferenceInternal("pref_is_initialized", false)

    // node
    val AUTO_SELECT_NODE = livePreferenceInternal("pref_auto_select_node", true)
    val CURRENT_NODE_ID = liveEncryptedPreference("pref_current_node_id", 1)
    val NODE_ID = liveEncryptedPreference("pref_node_id", 1L)

    // secutiy
    val SECURITY_UUID = liveEncryptedPreference("pref_security_uuid", UUID.fromString("00000000-0000-0000-0000-000000000000"))
    val SECURITY_WALLET_ENC = liveEncryptedPreference("pref_security_wallet_enc", ByteArray(96))
    val SECURITY_BIO_ENC = liveEncryptedPreference("pref_security_bio_enc", ByteArray(96))

    val USE_PASSWORD = livePreference("pref_use_password", false)
    val USE_BIO = livePreference("pref_use_bio", false)


    // appearance
    val DARK_MODE = livePreferenceInternal("pref_dark_mode", DarkMode.FOLLOW_SYSTEM)
    val INDICATOR = livePreferenceInternal("pref_show_indicator", true)
    val INVERT_COLOR = livePreferenceInternal("pref_invert_color", false)
    val LANGUAGE = livePreferenceInternal("pref_language", I18N.DEFAULT)

    val CURRENT_ACCOUNT_ID = liveEncryptedPreference("pref_current_account_id", ChainConfig.EMPTY_INSTANCE)




}