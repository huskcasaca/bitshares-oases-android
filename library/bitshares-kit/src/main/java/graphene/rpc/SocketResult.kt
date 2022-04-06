package graphene.rpc

import graphene.rpc.SocketResult.Companion.KEY_ERROR
import graphene.rpc.SocketResult.Companion.KEY_ID
import graphene.rpc.SocketResult.Companion.KEY_PARAMS
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable(with = SocketResultSerializer::class)
sealed class SocketResult {
    open val id: Int = 0
    open val jsonrpc: String = JSON_RPC_VERSION
    open val method: String = METHOD_UNDEFINED

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
        const val METHOD_UNDEFINED = ""
    }
}

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
    // TODO: 2022/3/30 kotlinx.serialization.json.internal.JsonDecodingException: Failed to parse 'int'
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