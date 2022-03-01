package com.bitshares.android.preference.old

import bitshareskit.chain.ChainConfig
import bitshareskit.extensions.createGraphene
import bitshareskit.models.Market
import bitshareskit.objects.AccountObject
import bitshareskit.objects.AssetObject
import com.bitshares.android.preference.DarkMode
import java.util.*

object Settings {

    val KEY_IS_INITIALIZED = livePreference("pref_is_initialized", false)

    // node
    val KEY_AUTO_SELECT_NODE = livePreference("pref_auto_select_node", true)
    val KEY_CURRENT_NODE_ID = liveEncryptedPreference("pref_current_node_id", 1)

    // wallet
    val KEY_UUID = liveEncryptedPreference("pref_uuid", UUID.fromString("00000000-0000-0000-0000-000000000000"))
    val KEY_WALLET_SECURE = liveEncryptedPreference("pref_wallet_secure", ByteArray(112))
    val KEY_FINGERPRINT_SECURE = liveEncryptedPreference("pref_fingerprint_secure", ByteArray(96))
    val KEY_USE_PASSWORD = livePreference("pref_use_password", false)
    val KEY_USE_FINGERPRINT = livePreference("pref_use_fingerprint", false)

    // appearance1
    val KEY_DARK_MODE = livePreference("pref_dark_mode", DarkMode.ON)
    val KEY_SHOW_INDICATOR = livePreference("pref_show_indicator", true)
    val KEY_INVERT_COLOR = livePreference("pref_invert_color", false)
    val KEY_LANGUAGE = livePreference("pref_language", 0)

    val KEY_CURRENT_ACCOUNT_ID = liveEncryptedPreference("pref_current_account_id", ChainConfig.EMPTY_INSTANCE)

    val KEY_ACCOUNT_SEARCH_HISTORY = liveEncryptedPreference("pref_account_search_history",
        emptySet<AccountObject>()
    )

    val KEY_IS_VERTICAL_LAYOUT = livePreference("pref_is_vertical_layout", false)

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

    val KEY_LAST_MARKET_INDEX = liveEncryptedPreference("pref_last_market_index", 0)

    // price & fee
    val KEY_AUTO_RESERVE_FEE = livePreference("pref_auto_reserve_fee", false)
    val KEY_RESERVED_FEE = liveEncryptedPreference("pref_fee_reserved", 1200000L)

    val KEY_BALANCE_UNIT = liveEncryptedPreference("pref_price_unit", ChainConfig.GRAPHENE_SYMBOL)

    val KEY_ENABLE_BLOCK_UPDATES = livePreference("pref_enable_block_updates", false)

}