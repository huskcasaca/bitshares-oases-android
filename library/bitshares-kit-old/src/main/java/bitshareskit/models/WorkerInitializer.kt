package bitshareskit.models

import bitshareskit.extensions.buildJsonObject
import bitshareskit.objects.ByteSerializable
import bitshareskit.objects.JsonSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

enum class WorkerInitializerType {
    REFUND_WORKER_INITIALIZER,
    VESTING_BALANCE_WORKER_INITIALIZER,
    BURN_WORKER_INITIALIZER
}

sealed class WorkerInitializer : JsonSerializable, ByteSerializable {

    companion object {
        fun fromJsonPair(rawJson: JSONArray): WorkerInitializer{
            return fromJson(rawJson.optJSONObject(1), getWorkerInitializerType(rawJson.optInt(0)))
        }

        fun fromJson(rawJson: JSONObject, type: WorkerInitializerType): WorkerInitializer {
            return when (type) {
                WorkerInitializerType.REFUND_WORKER_INITIALIZER -> RefundWorkerInitializer.fromJson(rawJson)
                WorkerInitializerType.VESTING_BALANCE_WORKER_INITIALIZER -> VestingBalanceWorkerInitializer.fromJson(rawJson)
                WorkerInitializerType.BURN_WORKER_INITIALIZER -> BurnWorkerInitializer.fromJson(rawJson)
            }
        }

        private fun getWorkerInitializerType(type: Int): WorkerInitializerType {
            return WorkerInitializerType.values()[type]
        }
    }

    override fun toByteArray(): ByteArray = buildPacket {
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
    }
}

class RefundWorkerInitializer(
): WorkerInitializer(){

    companion object {
        const val KEY_ = ""
        fun fromJson(rawJson: JSONObject): RefundWorkerInitializer {
            return RefundWorkerInitializer()
        }
    }
}

data class VestingBalanceWorkerInitializer(
    val payVestingPeriodDays: UShort
): WorkerInitializer(){

    companion object {
        const val KEY_PAY_VESTING_PERIOD_DAYS = "pay_vesting_period_days"
        fun fromJson(rawJson: JSONObject): RefundWorkerInitializer {
            return RefundWorkerInitializer()
        }
    }

    override fun toByteArray(): ByteArray = buildPacket {
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
    }
}

class BurnWorkerInitializer(
): WorkerInitializer() {

    companion object {
        const val KEY_ = ""
        fun fromJson(rawJson: JSONObject): RefundWorkerInitializer {
            return RefundWorkerInitializer()
        }
    }

    override fun toByteArray(): ByteArray = buildPacket {
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
    }

}

