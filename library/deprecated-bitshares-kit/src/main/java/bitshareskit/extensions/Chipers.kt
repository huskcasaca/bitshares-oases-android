package bitshareskit.extensions

import bitshareskit.models.PrivateKey
import bitshareskit.models.PublicKey
import graphene.extension.*


fun aesEncryptWithChecksum(privateKey: PrivateKey, publicKey: PublicKey, message: ByteArray): ByteArray {
    val secret: ByteArray = publicKey.ecKey!!.pubKeyPoint.multiply(privateKey.ecKey!!.privKey).normalize().xCoord.encoded
    val seed: ByteArray = secret.sha512().toHexString().toUnicodeByteArray()
    val checksumMessage: ByteArray = message.sha256().copyOfRange(0, 4) + message
    return aesEncrypt(seed, checksumMessage)
}

fun aesDecryptWithChecksum(privateKey: PrivateKey, publicKey: PublicKey, encrypted: ByteArray): ByteArray {
    require(encrypted.size > 4)
    val secret: ByteArray = publicKey.ecKey!!.pubKeyPoint.multiply(privateKey.ecKey!!.privKey).normalize().xCoord.encoded
    val seed: ByteArray = secret.sha512().toHexString().toUnicodeByteArray()
    val checksumMessage: ByteArray = aesDecrypt(seed, encrypted)
    val message = checksumMessage.copyOfRange(4, checksumMessage.size)
    require(message.sha256().copyOfRange(0, 4).contentEquals(checksumMessage.copyOfRange(0, 4)))
    return message
}

