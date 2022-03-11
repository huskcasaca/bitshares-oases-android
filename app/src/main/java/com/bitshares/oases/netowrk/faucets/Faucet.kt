package com.bitshares.oases.netowrk.faucets

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType

enum class Faucet(val faucetName: String, val url: String) {
    BITSHARES_EUROPE("BitShares Europe", BITSHARES_EUROPE_FAUCET),
    BITSHARES_XBTS("XBTS.IO", BITSHARES_XBTS_FAUCET),
    BITSHARES_GDEX("GDEX", BITSHARES_GDEX_FAUCET),
    BITSHARES_RUDEX("Rudex", BITSHARES_RUDEX_FAUCET)
}

const val BITSHARES_EUROPE_FAUCET = "https://faucet.bitshares.eu/onboarding/"
const val BITSHARES_XBTS_FAUCET = "https://faucet.xbts.io/"
const val BITSHARES_GDEX_FAUCET = "https://faucet.gdex.io/"
const val BITSHARES_RUDEX_FAUCET = "https://faucet.rudex.org/"

val JSON_CONTENT_TYPE = "application/json".toMediaType()

val FAUCET_JSON_CONFIG = Json { ignoreUnknownKeys = true }.asConverterFactory(JSON_CONTENT_TYPE)
