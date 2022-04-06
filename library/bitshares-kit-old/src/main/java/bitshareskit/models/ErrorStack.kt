package bitshareskit.models

import bitshareskit.errors.ErrorCode
import bitshareskit.errors.GrapheneException
import org.java_json.JSONObject

data class ErrorStack(
    val code: Int,
    val name: String,
    val message: String,
//    val stack: JSONArray,
) {

    companion object {
        const val KEY_CODE = "code"
        const val KEY_NAME = "name"
        const val KEY_MESSAGE = "message"
        const val KEY_DATA = "data"
        const val KEY_STACK = "stack"
        const val DEFAULT_NAME = "unknown_exception"

        fun fromJson(rawJson: JSONObject): ErrorStack {
            return ErrorStack(
                rawJson.optInt(KEY_CODE, ErrorCode.UNKNOWN_ERROR.code),
                rawJson.optJSONObject(KEY_DATA).optString(KEY_NAME, DEFAULT_NAME),
                rawJson.optString(KEY_MESSAGE),
//                JSONArray(),
            )
        }
    }

    val exception: GrapheneException get() = GrapheneException.resolve(code, message)

}

