package com.bitshares.android.netowrk.rpc

import com.bitshares.android.netowrk.rpc.WebsocketRpc.Companion.JSON_RPC_VERSION
import com.bitshares.android.netowrk.rpc.WebsocketRpc.Companion.KEY_ERROR
import com.bitshares.android.netowrk.rpc.WebsocketRpc.Companion.KEY_ID
import com.bitshares.android.netowrk.rpc.WebsocketRpc.Companion.KEY_JSON_RPC
import com.bitshares.android.netowrk.rpc.WebsocketRpc.Companion.KEY_METHOD
import com.bitshares.android.netowrk.rpc.WebsocketRpc.Companion.KEY_PARAMS
import com.bitshares.android.netowrk.rpc.WebsocketRpc.Companion.KEY_RESULT
import com.bitshares.android.netowrk.rpc.WebsocketRpc.Companion.METHOD_UNDEFINED
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable(with = SocketResultSerializer::class)
sealed class SocketResult : WebsocketRpc

@Serializable
data class SocketCallback(
    @SerialName(KEY_ID)         override val id: Int,
    @SerialName(KEY_JSON_RPC)   override val jsonrpc: String = JSON_RPC_VERSION,
    @SerialName(KEY_METHOD)     override val method: String = METHOD_UNDEFINED,
    @SerialName(KEY_RESULT)     val result: JsonElement
) : SocketResult()

@Serializable
data class SocketNotice(
//    @SerialName(KEY_ID)         override val id: Int,
    @SerialName(KEY_JSON_RPC)   override val jsonrpc: String = JSON_RPC_VERSION,
    @SerialName(KEY_METHOD)     override val method: String = METHOD_UNDEFINED,
    @SerialName(KEY_PARAMS)     val params: JsonElement
) : SocketResult()

@Serializable
data class SocketError(
    @SerialName(KEY_ID)         override val id: Int,
    @SerialName(KEY_JSON_RPC)   override val jsonrpc: String = JSON_RPC_VERSION,
//    @SerialName(KEY_METHOD)     override val method: String = METHOD_UNDEFINED,
    @SerialName(KEY_ERROR)      val error: JsonElement,
) : SocketResult()

// TODO: 2022/2/13 make all serializer objects
object SocketResultSerializer : JsonContentPolymorphicSerializer<SocketResult>(SocketResult::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out SocketResult> {
        return when {
            element.jsonObject.containsKey(KEY_ERROR) -> SocketError.serializer()
            element.jsonObject.containsKey(KEY_ID) -> SocketCallback.serializer()
            element.jsonObject.containsKey(KEY_PARAMS) -> SocketNotice.serializer()
            else -> throw SerializationException("Serializer not found for element $element")
        }
    }
}

//class ErrorStack()