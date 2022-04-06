package bitshareskit.models

import org.java_json.JSONObject

data class SocketResponseImpl(
    val rawJson: JSONObject
) {

    companion object {
        private const val KEY_CALLBACK_ID = "id"
        private const val KEY_JSON_RPC = "jsonrpc"
        private const val KEY_METHOD = "method"
        private const val KEY_RESULT = "result"
        private const val KEY_NOTICE = "notice"
        private const val KEY_ERROR = "error"
        private const val KEY_PARAMS = "params"
    }
    
    enum class Response {
        EMPTY, CALLBACK, ERROR, NOTICE;
    }

    val id: Int?
    val type: Response
    val jsonrpc: String?
    val data: Any?

    init {
        jsonrpc = if (rawJson.has(KEY_JSON_RPC)) rawJson.getString(KEY_JSON_RPC) else null

        type = when {
            rawJson.has(KEY_CALLBACK_ID) && rawJson.has(KEY_RESULT) -> Response.CALLBACK
            rawJson.has(KEY_CALLBACK_ID) && rawJson.has(KEY_ERROR) -> Response.ERROR
            rawJson.has(KEY_METHOD) && rawJson.optString(KEY_METHOD) == KEY_NOTICE -> Response.NOTICE
            else -> Response.EMPTY
        }

        id = when (type) {
            Response.CALLBACK -> rawJson.optInt(KEY_CALLBACK_ID)
            Response.ERROR -> rawJson.optInt(KEY_CALLBACK_ID)
            Response.NOTICE -> rawJson.getJSONArray(KEY_PARAMS).optInt(0)
            Response.EMPTY -> null
        }

        data = when (type) {
            Response.CALLBACK -> rawJson.get(KEY_RESULT).takeIf { it != JSONObject.NULL }
            Response.ERROR -> rawJson.optJSONObject(KEY_ERROR, null)
            Response.NOTICE -> rawJson.getJSONArray(KEY_PARAMS)[1]
            Response.EMPTY -> null
        }
    }
}


