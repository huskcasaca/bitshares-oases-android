package com.bitshares.oases.preference

import bitshareskit.chain.ChainConfig
import bitshareskit.extensions.createGraphene
import bitshareskit.models.Market
import bitshareskit.objects.AccountObject
import bitshareskit.objects.AssetObject
import com.bitshares.oases.MainApplication
import com.bitshares.oases.preference.old.livePreference
import modulon.union.ModulonApplication
import java.util.*

class PreferenceManager(
    application: MainApplication
): AbstractPreferenceManager(application) {

    val KEY_IS_INITIALIZED = livePreferenceInternal("pref_is_initialized", false)

    // node
    val KEY_AUTO_SELECT_NODE = livePreferenceInternal("pref_auto_select_node", true)
    val KEY_CURRENT_NODE_ID = liveEncryptedPreference("pref_current_node_id", 1)

    // wallet_new
    val N_KEY_UUID = com.bitshares.oases.preference.old.liveEncryptedPreference("npref_uuid", UUID.fromString("00000000-0000-0000-0000-000000000000"))
    val N_KEY_WALLET_SECURE = com.bitshares.oases.preference.old.liveEncryptedPreference("npref_wallet_secure", ByteArray(96))
    val N_KEY_FINGERPRINT_SECURE = com.bitshares.oases.preference.old.liveEncryptedPreference("npref_fingerprint_secure", ByteArray(96))
    val N_KEY_USE_PASSWORD = livePreference("npref_use_password", false)
    val N_KEY_USE_FINGERPRINT = livePreference("npref_use_fingerprint", false)


    // appearance1
    val KEY_DARK_MODE = livePreferenceInternal("pref_dark_mode", DarkMode.FOLLOW_SYSTEM)
    val KEY_SHOW_INDICATOR = livePreferenceInternal("pref_show_indicator", true)
    val KEY_INVERT_COLOR = livePreferenceInternal("pref_invert_color", false)
    val KEY_LANGUAGE = livePreferenceInternal("pref_language", 0)

    val KEY_CURRENT_ACCOUNT_ID = liveEncryptedPreference("pref_current_account_id", ChainConfig.EMPTY_INSTANCE)

    val KEY_ACCOUNT_SEARCH_HISTORY = liveEncryptedPreference("pref_account_search_history",
        emptySet<AccountObject>()
    )

    val KEY_IS_VERTICAL_LAYOUT = livePreferenceInternal("pref_is_vertical_layout", false)

    val KEY_ASSET_SEARCH_HISTORY = liveEncryptedPreference("pref_asset_search_history",
        setOf<AssetObject>(
            createGraphene(113L),
            createGraphene(121L),
            createGraphene(1325L),
        )
    )

    val KEY_MARKETS = liveEncryptedPreference("pref_markets",
        setOf(
            Market(createGraphene(113L), createGraphene(0L)),
            Market(createGraphene(121L), createGraphene(0L)),
            Market(createGraphene(1325L), createGraphene(0L)),
        )
    )

    val KEY_MARKET_GROUPS = liveEncryptedPreference(
        "pref_market_groups", setOf(
            AssetObject.CORE_ASSET_ID,
            "CNY",
            "USD",
            "EUR",
            "RUBLE",
        )
    )

    // price & fee
    val KEY_AUTO_RESERVE_FEE = livePreferenceInternal("pref_auto_reserve_fee", false)
    val KEY_RESERVED_FEE = liveEncryptedPreference("pref_fee_reserved", 1200000L)

    val KEY_BALANCE_UNIT = liveEncryptedPreference("pref_price_unit", ChainConfig.GRAPHENE_SYMBOL)

    val KEY_ENABLE_BLOCK_UPDATES = livePreferenceInternal("pref_enable_block_updates", false)

}