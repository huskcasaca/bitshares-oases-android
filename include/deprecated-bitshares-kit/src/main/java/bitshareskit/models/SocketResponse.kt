package bitshareskit.models

import org.java_json.JSONObject

sealed class SocketResponse {

    companion object {
        private const val KEY_CALLBACK_ID = "id"
        private const val KEY_JSON_RPC = "jsonrpc"
        private const val KEY_METHOD = "method"
        private const val KEY_RESULT = "result"
        private const val KEY_NOTICE = "notice"
        private const val KEY_ERROR = "error"
        private const val KEY_PARAMS = "params"

        const val EMPTY_ID = -1

        fun fromJson(rawJson: JSONObject): SocketResponse {
            return when {
                rawJson.has(KEY_CALLBACK_ID) && rawJson.has(KEY_RESULT) -> {
                    CallbackResponse(
                        rawJson.optInt(KEY_CALLBACK_ID, EMPTY_ID),
                        rawJson.opt(KEY_RESULT).takeIf { it != JSONObject.NULL },
                        rawJson.optString(KEY_JSON_RPC)
                    )
                }
                rawJson.has(KEY_CALLBACK_ID) && rawJson.has(KEY_ERROR) -> {
                    ErrorResponse(
                        rawJson.optInt(KEY_CALLBACK_ID, EMPTY_ID),
                        ErrorStack.fromJson(rawJson.optJSONObject(KEY_ERROR)),
                        rawJson.optString(KEY_JSON_RPC)
                    )
                }
                rawJson.optString(KEY_METHOD) == KEY_NOTICE -> {
                    NoticeResponse(
                        rawJson.optJSONArray(KEY_PARAMS).optInt(0, EMPTY_ID),
                        rawJson.getJSONArray(KEY_PARAMS).opt(1).takeIf { it != JSONObject.NULL },
                        rawJson.optString(KEY_JSON_RPC)
                    )
                }
                else -> {
                    EmptyResponse(
                        rawJson
                    )
                }
            }
        }
    }
    
    enum class Response {
        EMPTY, CALLBACK, ERROR, NOTICE;
    }

    open val id: Int = EMPTY_ID
    open val type: Response = Response.EMPTY
    open val jsonrpc: String = ""

    abstract val data: Any?


    data class CallbackResponse(
        override val id: Int,
        override val data: Any?,
        override val jsonrpc: String
    ): SocketResponse() {
        override val type: Response = Response.CALLBACK

    }

    data class NoticeResponse(
        override val id: Int,
        override val data: Any?,
        override val jsonrpc: String
    ): SocketResponse() {
        override val type: Response = Response.NOTICE

    }

    data class ErrorResponse(
        override val id: Int,
        override val data: ErrorStack,
        override val jsonrpc: String
    ): SocketResponse() {
        override val type: Response = Response.ERROR

    }

    data class EmptyResponse(
        override val data: Any?
    ): SocketResponse() {
        override val type: Response = Response.EMPTY

    }
    
}


