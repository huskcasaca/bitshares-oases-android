package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.models.WorkerInitializer
import bitshareskit.objects.AccountObject
import bitshareskit.objects.WorkerObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject
import java.util.*

data class WorkerCreateOperation(
    var owner: AccountObject,
    val workBeginDate: Date,
    val workEndDate: Date,
    val dailyPay: Long,
    val name: String,
    val url: String,
    val initializer: Set<WorkerInitializer>
): Operation() {

//    fee: asset,
//    owner: protocol_id_type("account"),
//    work_begin_date: time_point_sec,
//    work_end_date: time_point_sec,
//    daily_pay: int64,
//    name: string,
//    url: string,
//    initializer: worker_initializer

    companion object {

        const val KEY_OWNER = "owner"
        const val KEY_WORK_BEGIN_DATE = "work_begin_date"
        const val KEY_WORK_END_DATE = "work_end_date"
        const val KEY_DAILY_PAY = "daily_pay"
        const val KEY_NAME = "name"
        const val KEY_URL = "url"
        const val KEY_INITIALIZER = "initializer"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): WorkerCreateOperation {
            return WorkerCreateOperation(
                rawJson.optGrapheneInstance(KEY_OWNER),
                rawJson.optGrapheneTime(KEY_WORK_BEGIN_DATE),
                rawJson.optGrapheneTime(KEY_WORK_END_DATE),
                rawJson.optLong(KEY_DAILY_PAY),
                rawJson.optString(KEY_NAME),
                rawJson.optString(KEY_URL),
                rawJson.optIterable<JSONArray>(KEY_INITIALIZER).map { WorkerInitializer.fromJsonPair(it) }.toSet()
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
                result = createGraphene(rawJsonResult.optString(1))
            }
        }
    }
    var result: WorkerObject = createGrapheneEmptyInstance()

    override val operationType = OperationType.WORKER_CREATE_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}