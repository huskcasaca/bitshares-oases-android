package bitshareskit.models

import org.java_json.JSONObject

class TransactionBlock(rawJson: JSONObject) {

    companion object {
        private const val KEY_ID = "id"
        private const val KEY_BLOCK_NUM = "block_num"
        private const val KEY_TRX_NUM = "trx_num"
        private const val KEY_TRX = "operations"
    }

    val id: String
    val blockNum: Long
    val trxNum: Int
    val trx: Transaction


    init {
        id = rawJson.optString(KEY_ID)
        blockNum = rawJson.optLong(KEY_BLOCK_NUM)
        trxNum = rawJson.optInt(KEY_TRX_NUM)
        trx = Transaction.fromJsonObject(rawJson.optJSONObject(KEY_TRX))
    }

}