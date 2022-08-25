package bitshareskit.operations

import bitshareskit.extensions.*
import bitshareskit.objects.AccountObject
import bitshareskit.objects.AssetObject
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

data class AssetUpdateIssuerOperation(
    var issuer: AccountObject,
    var asset: AssetObject,
    var newIssuer: AccountObject
): Operation() {

    companion object {

//        issuer: protocol_id_type("account"),
//        asset_to_update: protocol_id_type("asset"),
//        new_issuer: protocol_id_type("account"),

        const val KEY_ISSUER = "issuer"
        const val KEY_ASSET_TO_UPDATE = "asset_to_update"
        const val KEY_NEW_ISSUER = "new_issuer"

        fun fromJson(rawJson: JSONObject, rawJsonResult: JSONArray = JSONArray()): AssetUpdateIssuerOperation {
            return AssetUpdateIssuerOperation(
                rawJson.optGrapheneInstance(KEY_ISSUER),
                rawJson.optGrapheneInstance(KEY_ASSET_TO_UPDATE),
                rawJson.optGrapheneInstance(KEY_NEW_ISSUER)
            ).apply {
                fee = rawJson.optItem(KEY_FEE)
            }
        }
    }

    override val operationType = OperationType.ASSET_UPDATE_ISSUER_OPERATION

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(fee)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_FEE, fee)
        putArray(KEY_EXTENSIONS, extensions)
    }

}