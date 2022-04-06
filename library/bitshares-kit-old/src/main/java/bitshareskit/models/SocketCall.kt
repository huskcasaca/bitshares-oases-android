package bitshareskit.models

import bitshareskit.extensions.buildJsonArray
import bitshareskit.extensions.buildJsonObject
import bitshareskit.objects.JsonSerializable
import bitshareskit.chain.BlockchainAPI
import bitshareskit.chain.CallMethod
import org.java_json.JSONArray
import org.java_json.JSONObject

data class SocketCall(
    val method: String = RPC_METHOD,
    val api: BlockchainAPI,
    val rpc: String,
    val params: List<Any?> = emptyList(),
    val jsonrpc: String = JSONRPC_VERSION,
    val subscribe: Boolean = false
): JsonSerializable {

    constructor(
        method: CallMethod,
        params: List<Any?> = emptyList(),
        subscribe: Boolean = false
    ) : this(
        api = method.api,
        rpc = method.nameString,
        params = params,
        subscribe = subscribe
    )

    companion object {
        const val KEY_CALLBACK_ID = "id"
        const val KEY_METHOD = "method"
        const val KEY_PARAMS = "params"
        const val KEY_JSONRPC = "jsonrpc"
        const val RPC_METHOD = "call"
        const val JSONRPC_VERSION = "2.0"
    }

    var id: Int = -1
    var apiId: Int = -1
//    val timestamp = System.currentTimeMillis()

    override fun toJsonElement(): JSONObject {
        val ordered = if (subscribe)
            buildJsonArray {
                put(id)
                putAll(JSONArray(params))
            } else JSONArray(params)
        val paramsArray = buildJsonArray {
            put(apiId)
            put(rpc)
            put(ordered)
        }
        return buildJsonObject {
            put(KEY_CALLBACK_ID, id)
            put(KEY_JSONRPC, jsonrpc)
            put(KEY_METHOD, method)
            put(KEY_PARAMS, paramsArray)
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is SocketCall && rpc == other.rpc && params == other.params
    }

    override fun hashCode(): Int {
        var result = method.hashCode()
        result = 31 * result + api.hashCode()
        result = 31 * result + rpc.hashCode()
        result = 31 * result + params.hashCode()
        result = 31 * result + jsonrpc.hashCode()
        result = 31 * result + subscribe.hashCode()
        result = 31 * result + (id ?: 0)
        result = 31 * result + (apiId ?: 0)
        return result
    }

}