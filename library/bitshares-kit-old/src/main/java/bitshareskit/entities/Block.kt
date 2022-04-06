package bitshareskit.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import bitshareskit.chain.ChainConfig
import bitshareskit.extensions.optGrapheneInstance
import bitshareskit.extensions.optGrapheneTime
import bitshareskit.extensions.optIterable
import bitshareskit.models.Transaction
import bitshareskit.objects.JsonSerializable
import bitshareskit.objects.WitnessObject
import org.java_json.JSONObject
import java.util.*

@Entity(tableName = Block.TABLE_NAME)
data class Block(@ColumnInfo(name = COLUMN_DATA) val rawJson: JSONObject): JsonSerializable {

    /*{
        "previous":"02daad90f1a0bd9c482538c81ac60473d8c85e87",
        "timestamp":"2020-05-13T04:19:30",
        "witness":"1.6.105",
        "transaction_merkle_root":"b0970edf469154c5174af3723db5fa40e587ac03",
        "extensions":[
        ]
    }*/

    companion object {
        const val TABLE_NAME = "block"
        const val COLUMN_DATA = "data"
        const val COLUMN_HEIGHT = "height"

        private const val KEY_PREVIOUS = "previous"
        private const val KEY_TIMESTAMP = "timestamp"
        private const val KEY_WITNESS = "witness"
        private const val KEY_TRANSACTION_MERKLE_ROOT = "transaction_merkle_root"
        private const val KEY_WITNESS_SIGNATURE = "witness_signature"
        private const val KEY_TRANSACTIONS = "transactions"

        fun fromJson(rawJson: JSONObject): Block = Block(rawJson)

        private fun getBlockHeight(previousHash: String) = runCatching { previousHash.substring(0, 8).toLong(16) + 1 }.getOrDefault(ChainConfig.EMPTY_INSTANCE)

        val EMPTY by lazy { Block(JSONObject()) }
    }

    @delegate:Ignore val previousHash: String by lazy { rawJson.optString(KEY_PREVIOUS) }
    @delegate:Ignore val rootHash: String by lazy { rawJson.optString(KEY_TRANSACTION_MERKLE_ROOT) }
    @delegate:Ignore val timestamp: Date by lazy { rawJson.optGrapheneTime(KEY_TIMESTAMP) }
    @delegate:Ignore val witnessSignature: String by lazy { rawJson.optString(KEY_WITNESS_SIGNATURE) }
    @Ignore var hash: String = ""
    @Ignore var witness: WitnessObject = rawJson.optGrapheneInstance(KEY_WITNESS)

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = COLUMN_HEIGHT) var blockNum = getBlockHeight(previousHash)

    @delegate:Ignore val transactions: List<Transaction> by lazy {
        rawJson.optIterable<JSONObject>(KEY_TRANSACTIONS).map {
            Transaction.fromJsonObject(it).apply {
                operations.onEach {
                    it.blockHeight = blockNum
                    it.createTime = timestamp
                }
            }
        }
    }
    @Ignore val transactionCount: Int = rawJson.optJSONArray(KEY_TRANSACTIONS).length()
    @delegate:Ignore val operationCount: Int by lazy { transactions.sumOf { it.operations.size } }

    override fun toJsonElement(): Any? = blockNum

    /*{
    "id":171,
    "result":{
        "previous":"02f6e589 b009c26d ec54c3c3308e76dfad65742e",
        "timestamp":"2020-07-16T15:21:27",
        "witness":"1.6.59",
        "transaction_merkle_root":"e4b65decd220c4c71f0bbff2a63014adfed84eed",
        "extensions":[
        ],
        "witness_signature":"20426864296cda26b042d562c981b12735598dd2d7c1c5cd59336631d09e924b3e0f38679fa12d9b41762635bdde0afb8341b066496da4b8740a3d1751092c71bb",
        "transactions":[
            {
                "ref_block_num":58761,
                "ref_block_prefix":1841433008,
                "expiration":"2020-07-16T15:21:54",
                "operations":[
                    [
                        1,
                        {
                            "fee":{
                                "amount":4826,
                                "asset_id":"1.3.0"
                            },
                            "seller":"1.2.1073373",
                            "amount_to_sell":{
                                "amount":7774,
                                "asset_id":"1.3.4343"
                            },
                            "min_to_receive":{
                                "amount":708261,
                                "asset_id":"1.3.4344"
                            },
                            "expiration":"2020-07-16T16:21:25",
                            "fill_or_kill":false,
                            "extensions":[
                            ]
                        }]],
                "extensions":[
                ],
                "signatures":[
                    "200da139a3cc5a160af1667ea1b63402e515d6aad9d1e6b8c7b7e77a86d49b1e9761fae7b7f3f2f23c1159ee9eb78ecb55fde17e21290786ab0bd9caaf8d6db998"],
                "operation_results":[
                    [
                        1,
                        "1.7.446642920"]]
            },
            {
                "ref_block_num":58761,
                "ref_block_prefix":1841433008,
                "expiration":"2020-07-16T15:21:54",
                "operations":[
                    [
                        1,
                        {
                            "fee":{
                                "amount":4826,
                                "asset_id":"1.3.0"
                            },
                            "seller":"1.2.1073373",
                            "amount_to_sell":{
                                "amount":127,
                                "asset_id":"1.3.4905"
                            },
                            "min_to_receive":{
                                "amount":76707,
                                "asset_id":"1.3.4344"
                            },
                            "expiration":"2020-07-16T16:21:25",
                            "fill_or_kill":false,
                            "extensions":[
                            ]
                        }]],
                "extensions":[
                ],
                "signatures":[
                    "2078e9d2610d82a21fca89dabe1ddbc8948e72235f44138a1ed494f422651a32e67b637a529a40f40c396eb09d000101314190e142f78c23bf4c6e469b177c25cd"],
                "operation_results":[
                    [
                        1,
                        "1.7.446642921"]]
            },
            {
                "ref_block_num":58761,
                "ref_block_prefix":1841433008,
                "expiration":"2020-07-16T15:21:54",
                "operations":[
                    [
                        1,
                        {
                            "fee":{
                                "amount":4826,
                                "asset_id":"1.3.0"
                            },
                            "seller":"1.2.1073373",
                            "amount_to_sell":{
                                "amount":179916,
                                "asset_id":"1.3.4344"
                            },
                            "min_to_receive":{
                                "amount":299,
                                "asset_id":"1.3.4905"
                            },
                            "expiration":"2020-07-16T16:21:25",
                            "fill_or_kill":false,
                            "extensions":[
                            ]
                        }]],
                "extensions":[
                ],
                "signatures":[
                    "1f4118d7884623b0c8fcc89831a73b5c1721e9341ac213e03e98c092256c4273603792c6d0a135cad232b7944286cc5dc7072731d4d549d3e106f0a9f5f6ec9610"],
                "operation_results":[
                    [
                        1,
                        "1.7.446642922"]]
            }]
    }
}*/

}