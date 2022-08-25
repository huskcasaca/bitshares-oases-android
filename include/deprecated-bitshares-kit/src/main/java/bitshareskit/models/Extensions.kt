package bitshareskit.models

import bitshareskit.objects.GrapheneSerializable
import bitshareskit.objects.JsonSerializable
import org.java_json.JSONObject

class Extensions: GrapheneSerializable {

    companion object {
        const val KEY_EXTENSIONS = "extensions"
    }

    val extensions = mutableListOf<JsonSerializable>()

    override fun toByteArray(): ByteArray {
        return ByteArray(1)
    }

    override fun toJsonElement(): JSONObject {
        return JSONObject(mapOf(KEY_EXTENSIONS to extensions))
    }

}