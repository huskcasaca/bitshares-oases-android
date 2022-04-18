package com.bitshares.oases.netowrk.java_websocket

import androidx.lifecycle.LiveData
import com.bitshares.oases.chain.blockchainNetworkScope
import com.bitshares.oases.preference.old.Graphene
import bitshareskit.chain.CallMethod
import kotlinx.coroutines.launch
import org.java_json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

@Deprecated("use GrapheneClient")
class GrapheneSocketLiveData<T : Any?>(
    private val method: CallMethod,
    private val data: List<Any> = listOf(),
    private val period: Long? = null,
    private val transform: (Any) -> T
) : LiveData<T>() {
    private var timer = Timer()
    private var last: Any? = null

    // TODO: 2021/8/29 breaking changes
    override fun onActive() {
        try {
            timer.cancel()
            timer = Timer()
            timer.schedule(1000L, period ?: Graphene.KEY_BLOCK_INTERVAL.value * 1000L) {
                val timerHash = timer.hashCode()
                blockchainNetworkScope.launch {
                    val result = NetworkService.sendSuspend(method, data)
                    val lastResult = last
                    if (
                        (result is JSONObject && lastResult is JSONObject && !result.similar(lastResult)) || last == null || result == null || result != lastResult
                    ) {
                        last = result
                        val transformed = result?.let(transform)
                        if (timer.hashCode() == timerHash && (value != result || result == null)) postValue(transformed)
                    }
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            postValue(null)
        }
        super.onActive()
    }

    override fun onInactive() {
        timer.cancel()
        timer = Timer()
        super.onInactive()
    }

}