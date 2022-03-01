package bitshareskit.models

import bitshareskit.extensions.*
import bitshareskit.objects.GrapheneSerializable
import bitshareskit.security.NonceGenerator
import bitshareskit.serializer.writeGrapheneULong
import bitshareskit.serializer.writeSerializable
import bitshareskit.serializer.writeVarInt
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import kotlinx.io.core.writeFully
import org.java_json.JSONObject

data class Memo(
    val from: PublicKey,
    val to: PublicKey,
    var message: String? = null,
    val nonce: ULong = NonceGenerator.INSTANCE.generateNonce().toULong(),
    var isEncrypted: Boolean = false
): GrapheneSerializable {

    companion object {
        private const val KEY_FROM = "from"
        private const val KEY_TO = "to"
        private const val KEY_NONCE = "nonce"
        private const val KEY_MESSAGE = "message"

        fun fromJson(rawJson: JSONObject): Memo {
            return Memo(
                rawJson.optPublicKey(KEY_FROM),
                rawJson.optPublicKey(KEY_TO),
                rawJson.optString(KEY_MESSAGE),
                rawJson.optULong(KEY_NONCE),
                true
            )
        }
    }

    fun encryptMessage(key: PrivateKey, msg: String = message.orEmpty()){
        if (isEncrypted) return
        val pubKey = to.ecKey
        val privKey = key.ecKey
        if (pubKey == null || privKey == null || msg.isEmpty()) return
        runCatching {
            val secret = pubKey.pubKeyPoint.multiply(privKey.privKey).normalize().xCoord.encoded
            val seed = nonce.toString().toUnicodeByteArray() + secret.sha512().toHexString().toUnicodeByteArray()
            val checksumMessage = msg.sha256().copyOfRange(0, 4) + msg.toByteArray()
            aesEncrypt(seed, checksumMessage)?.toHexString()!!
        }.onSuccess {
            message = it
            isEncrypted = true
        }
    }

    fun decryptMessage(key: PrivateKey){
        if (!isEncrypted) return
        val pubKey = from.ecKey
        val privKey = key.ecKey
        if (pubKey == null || privKey == null || message.isNullOrEmpty()) return
        runCatching {
            val secret = pubKey.pubKeyPoint.multiply(privKey.privKey).normalize().xCoord.encoded
            val seed = nonce.toString().toUnicodeByteArray() + secret.sha512().toHexString().toUnicodeByteArray()
            val decrypted = aesDecrypt(seed, message.toHexByteArrayOrEmpty())!!
            val message = String(decrypted.copyOfRange(4, decrypted.size))
            val checksum = message.sha256().copyOfRange(0, 4)
            check(checksum.contentEquals(decrypted.copyOfRange(0, 4)))
            message
        }.onSuccess {
            message = it
            isEncrypted = false
        }
    }

    val isValid get() = from.isValid && to.isValid && nonce != 0UL && message != null

    override fun toByteArray(): ByteArray {
        return buildPacket {
            if (from.isInvalid || to.isInvalid || !isEncrypted) return@buildPacket
            writeSerializable(from)
            writeSerializable(to)
            writeGrapheneULong(nonce)
            writeVarInt(message.toHexByteArrayOrEmpty().size)
            writeFully(message.toHexByteArrayOrEmpty())
        }.readBytes()
    }

    override fun toJsonElement(): JSONObject {
        return buildJsonObject {
            if (from.isInvalid || to.isInvalid || !isEncrypted) return@buildJsonObject
            putItem(KEY_FROM, from.address.orEmpty())
            putItem(KEY_TO, to.address.orEmpty())
            putItem(KEY_NONCE, nonce.toString())
            putItem(KEY_MESSAGE, message.orEmpty())
        }
    }

}