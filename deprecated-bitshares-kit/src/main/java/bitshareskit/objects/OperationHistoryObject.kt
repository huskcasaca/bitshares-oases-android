package bitshareskit.objects

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import bitshareskit.chain.ChainConfig
import bitshareskit.operations.Operation
import org.java_json.JSONObject

@Entity(tableName = OperationHistoryObject.TABLE_NAME)
data class OperationHistoryObject(@ColumnInfo(name = "data") val rawJson: JSONObject) : GrapheneObject(rawJson) {

    /*{
        "id":"1.11.1084674084",
        "op":[
            2,
            {
                "fee":{
                    "amount":482,
                    "asset_id":"1.3.0"
                },
                "fee_paying_account":"1.2.1073373",
                "order":"1.7.446767158",
                "extensions":[
                ]
            }],
        "result":[
            0,
            {
            }],
        "block_num":49847573,
        "trx_in_block":1,
        "op_in_trx":0,
        "virtual_op":1
    }*/

    companion object {
        @Ignore const val TABLE_NAME = "operation_history_object"
        @Ignore const val KEY_OP = "op"
        @Ignore const val KEY_RESULT = "result"
        @Ignore const val KEY_BLOCK_NUM = "block_num"
        @Ignore const val KEY_TRX_IN_BLOCK = "trx_in_block"
        @Ignore const val KEY_OP_IN_TRX = "op_in_trx"
        @Ignore const val KEY_VIRTUAL_OP = "virtual_op"
    }

    @delegate:Ignore val operation: Operation by lazy {
        Operation.fromJsonPair(rawJson.optJSONArray(KEY_OP), rawJson.optJSONArray(KEY_RESULT)).apply {
            isVirtual = rawJson.optInt(KEY_VIRTUAL_OP) != 0
            blockHeight = blockNum
        }
    }

    @delegate:Ignore val blockNum: Long by lazy { rawJson.optLong(KEY_BLOCK_NUM) }
    @delegate:Ignore val transactionCount: Int by lazy { rawJson.optInt(KEY_TRX_IN_BLOCK) }
    @delegate:Ignore val operationCount: Int by lazy { rawJson.optInt(KEY_OP_IN_TRX) }
    @ColumnInfo(name = COLUMN_OWNER_UID) var ownerUid: Long = ChainConfig.EMPTY_INSTANCE

    override fun equals(other: Any?): Boolean = super.equals(other)
    override fun hashCode(): Int = super.hashCode()

}
