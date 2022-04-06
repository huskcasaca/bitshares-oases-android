package bitshareskit.models

import bitshareskit.chain.ChainConfig
import bitshareskit.extensions.*
import bitshareskit.objects.ByteSerializable
import bitshareskit.objects.JsonSerializable
import bitshareskit.operations.Operation
import bitshareskit.serializer.writeGrapheneIndexedArray
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeGrapheneString
import bitshareskit.serializer.writeSerializable
import graphene.extension.sha256
import graphene.extension.toHexString
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

class Transaction : ByteSerializable, JsonSerializable{

    companion object {
        private const val KEY_REF_BLOCK_NUM = "ref_block_num"
        private const val KEY_REF_BLOCK_PREFIX = "ref_block_prefix"
        private const val KEY_EXPIRATION = "expiration"
        private const val KEY_OPERATIONS = "operations"
        private const val KEY_EXTENSIONS = "extensions"
        private const val KEY_SIGNATURES = "signatures"
        private const val KEY_OPERATION_RESULT = "operation_results"

        fun fromJsonObject(rawJson: JSONObject): Transaction {
            return Transaction().apply {
                block = ReferenceBlock(
                    rawJson.optInt(KEY_REF_BLOCK_NUM),
                    rawJson.optLong(KEY_REF_BLOCK_PREFIX),
                    rawJson.optGrapheneTime(KEY_EXPIRATION)
                )
                val operationResults = rawJson.optIterable<JSONArray>(KEY_OPERATION_RESULT).toList()
                // FIXME: 2022/1/18 empty operationResults
//                if (operationResults.isNotEmpty()) {
                    operations.addAll(rawJson.optIterable<JSONArray>(KEY_OPERATIONS).mapIndexed { index, rawJson ->
                        Operation.fromJsonPair(rawJson, operationResults.getOrElse(index){ JSONArray() })
                    })
//                }
                signatures.addAll(rawJson.optIterable(KEY_SIGNATURES))
            }
        }
    }

    val operations = mutableListOf<Operation>()
    val signatures = mutableSetOf<String>()

    var chainId = ChainConfig.Chain.CHAIN_ID_MAIN_NET
        set(value) {
            signatures.clear()
            field = value
        }

    var block: ReferenceBlock? = null
        set(value) {
            signatures.clear()
            field = value
        }

    var extensions = emptySet<Extensions>()
        set(value) {
            signatures.clear()
            field = value
        }

    val isSigned
        get() = signatures.isNotEmpty()


    fun sign(keys: Set<PrivateKey>){
        signatures.clear()
        val hashed = this.toByteArray().sha256()
        val startTime = block!!.expiration
        keys.mapNotNull { it.ecKey }.forEach {
            val signature = it.signOnly(hashed)
            signatures.add(signature.toHexString())
        }
    }

    override fun toByteArray(): ByteArray {
        return buildPacket {
            writeGrapheneString(chainId)
            writeSerializable(block!!)
            writeGrapheneIndexedArray(operations)
            writeGrapheneSet(extensions)
        }.readBytes()
    }

    override fun toJsonElement(): JSONObject {
        return buildJsonObject {
            putItem(KEY_REF_BLOCK_NUM, block!!.refBlockNum)
            putItem(KEY_REF_BLOCK_PREFIX, block!!.refBlockPrefix)
            putGrapheneTime(KEY_EXPIRATION, block!!.expiration)
            putIndexedArray(KEY_OPERATIONS, operations)
            putArray(KEY_EXTENSIONS, extensions)
            putArray(KEY_SIGNATURES, signatures)
        }
    }


}