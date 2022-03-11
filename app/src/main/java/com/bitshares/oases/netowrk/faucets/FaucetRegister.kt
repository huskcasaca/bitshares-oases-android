package com.bitshares.oases.netowrk.faucets

import bitshareskit.chain.ChainConfig
import com.bitshares.oases.chain.KeyCreator

data class FaucetRegister(
    val faucet: Faucet,
    val name: String,
    val password: String
) {
    val ownerKey = KeyCreator.createOwnerFromSeed(name, password, ChainConfig.Asset.CORE_ASSET_SYMBOL)
    val activeKey = KeyCreator.createActiveFromSeed(name, password, ChainConfig.Asset.CORE_ASSET_SYMBOL)
    val memoKey = KeyCreator.createMemoFromSeed(name, password, ChainConfig.Asset.CORE_ASSET_SYMBOL)

    val accountInfo = AccountInfo(
        name,
        ownerKey.publicKey,
        activeKey.publicKey,
        memoKey.publicKey,
    )
}