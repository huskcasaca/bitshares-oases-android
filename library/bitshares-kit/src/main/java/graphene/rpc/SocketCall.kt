package graphene.rpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class SocketCall(
    @SerialName(KEY_ID)       val id: Int,
    @SerialName(KEY_JSON_RPC) val jsonrpc: String = JSON_RPC_VERSION,
    @SerialName(KEY_METHOD)   val method: String = METHOD_CALL,
    @SerialName(KEY_PARAMS)   val params: JsonElement
) {
    companion object {
        const val KEY_ID = "id"
        const val KEY_JSON_RPC = "jsonrpc"
        const val KEY_METHOD = "method"
        const val KEY_RESULT = "result"
        const val KEY_NOTICE = "notice"
        const val KEY_ERROR = "error"
        const val KEY_PARAMS = "params"


        const val JSON_RPC_VERSION = "2.0"

        const val METHOD_CALL = "call"
        const val METHOD_NOTICE = "notice"
    }


}


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

