package com.bitshares.oases.preference.old

import bitshareskit.chain.ChainConfig
import bitshareskit.objects.AssetObject

object Graphene {

    val KEY_SYMBOL = liveEncryptedPreference("graphene_symbol", ChainConfig.GRAPHENE_SYMBOL)

    val KEY_CORE_ASSET = liveEncryptedPreference("graphene_core_asset", AssetObject.CORE_ASSET)

    val KEY_CHAIN_ID = liveEncryptedPreference("graphene_chain_id", ChainConfig.Chain.CHAIN_ID_MAIN_NET)

    val KEY_BLOCK_INTERVAL = liveEncryptedPreference("graphene_block_interval", ChainConfig.GRAPHENE_DEFAULT_BLOCK_INTERVAL)

    val KEY_MAXIMUM_TIME_UNTIL_EXPIRATION = liveEncryptedPreference("graphene_maximum_time_until_expiration", ChainConfig.GRAPHENE_DEFAULT_MAX_TIME_UNTIL_EXPIRATION)

    val KEY_CHAIN_CONFIG = liveEncryptedPreference("graphene_chain_config",
        mapOf(
            ChainConfig.Chain.CHAIN_ID_MAIN_NET to ChainConfig.Asset.CORE_ASSET_SYMBOL,
            ChainConfig.Chain.CHAIN_ID_TEST_NET to ChainConfig.Asset.CORE_ASSET_SYMBOL_TEST,
        )
    )

}