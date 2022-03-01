package com.bitshares.android.netowrk.rpc

import com.bitshares.android.netowrk.rpc.WebsocketRpc.Companion.JSON_RPC_VERSION
import com.bitshares.android.netowrk.rpc.WebsocketRpc.Companion.KEY_ID
import com.bitshares.android.netowrk.rpc.WebsocketRpc.Companion.KEY_JSON_RPC
import com.bitshares.android.netowrk.rpc.WebsocketRpc.Companion.KEY_METHOD
import com.bitshares.android.netowrk.rpc.WebsocketRpc.Companion.KEY_PARAMS
import com.bitshares.android.netowrk.rpc.WebsocketRpc.Companion.METHOD_CALL
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class SocketCall(
    @SerialName(KEY_ID)       override val id: Int,
    @SerialName(KEY_JSON_RPC) override val jsonrpc: String = JSON_RPC_VERSION,
    @SerialName(KEY_METHOD)   override val method: String = METHOD_CALL,
    @SerialName(KEY_PARAMS)   val params: JsonElement
) : WebsocketRpc


data class CallParams(
    val method: Int,
    val subscribe: Boolean,
    val params: JsonElement
)

//class CallParamsBuilder {
//    var method: CallMethod = CallMethod.DEBUG
//    var subscribe: Boolean = false
//    var params: JsonElement = JsonNull
//}
//
//fun buildCallParams(block: CallParamsBuilder.() -> Unit): CallParams {
//    val builder = CallParamsBuilder().apply(block)
//    return CallParams(builder.method, builder.subscribe, builder.params)
//}

