package com.bitshares.oases.preference.old

import bitshareskit.chain.ChainConfig
import bitshareskit.extensions.createGraphene
import bitshareskit.models.Market
import bitshareskit.objects.AccountObject
import bitshareskit.objects.AssetObject
import com.bitshares.oases.preference.DarkMode
import java.util.*

@Deprecated("removed", replaceWith = ReplaceWith("globalPreferenceManager", "com.bitshares.oases.globalPreferenceManager"))
object Settings {

    // node
    val KEY_AUTO_SELECT_NODE = livePreference("pref_auto_select_node", true)
    val KEY_CURRENT_NODE_ID = liveEncryptedPreference("pref_current_node_id", 1)


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