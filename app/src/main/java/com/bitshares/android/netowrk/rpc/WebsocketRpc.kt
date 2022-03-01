package com.bitshares.android.netowrk.rpc

interface WebsocketRpc {
    val id: Int             get() = throw IllegalArgumentException()
    val jsonrpc: String     get() = throw IllegalArgumentException()
    val method: String      get() = throw IllegalArgumentException()

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