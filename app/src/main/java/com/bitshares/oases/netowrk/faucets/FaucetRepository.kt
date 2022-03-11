package com.bitshares.oases.netowrk.faucets

import okhttp3.OkHttpClient
import retrofit2.Retrofit

object FaucetRepository {

    suspend fun registerAccountInFaucet(info: FaucetRegister): RegisterResult = buildRetrofit {
        baseUrl(info.faucet.url)
        addConverterFactory(FAUCET_JSON_CONFIG)
    }.run { create(FaucetService::class.java).register(RegisterRequest(info.accountInfo)) }

}

// TODO: 2022/2/19 move to extensions
inline fun buildRetrofit(block: Retrofit.Builder.() -> Unit): Retrofit = Retrofit.Builder().apply(block).build()
inline fun buildOkhttpClient(block: OkHttpClient.Builder.() -> Unit): OkHttpClient = OkHttpClient.Builder().apply(block).build()

