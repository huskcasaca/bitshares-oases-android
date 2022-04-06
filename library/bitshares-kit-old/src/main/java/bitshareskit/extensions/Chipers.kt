package bitshareskit.extensions

import android.security.keystore.KeyProperties
import bitcoinkit.Base58
import bitshareskit.models.PrivateKey
import bitshareskit.models.PublicKey
import graphene.extension.*
import org.bouncycastle.util.encoders.Base64
import org.bouncycastle.util.encoders.DecoderException
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


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


//const val KEY_ALGORITHM_AES_CBC_PKCS7 = "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_NONE}"
//const val KEY_ALGORITHM_AES_CBC_PKCS5 = "AES/CBC/PKCS5Padding"
//
//fun aesEncrypt(key: ByteArray, data: ByteArray): ByteArray {
//    val hashed = key.sha512()
//    val skSpec = SecretKeySpec(hashed, 0, 32, KeyProperties.KEY_ALGORITHM_AES)
//    val ivSpec = IvParameterSpec(hashed, 32, 16)
//    Cipher.getInstance(KEY_ALGORITHM_AES_CBC_PKCS5).apply {
//        init(Cipher.ENCRYPT_MODE, skSpec, ivSpec)
//        return doFinal(data)
//    }
//}
//
//fun aesDecrypt(key: ByteArray, data: ByteArray): ByteArray {
//    val hashed = key.sha512()
//    val skSpec = SecretKeySpec(hashed, 0, 32, KeyProperties.KEY_ALGORITHM_AES)
//    val ivSpec = IvParameterSpec(hashed, 32, 16)
//    Cipher.getInstance(KEY_ALGORITHM_AES_CBC_PKCS5).apply {
//        init(Cipher.DECRYPT_MODE, skSpec, ivSpec)
//        return doFinal(data)
//    }
//}
//
//fun aesEncryptCipher(seed: String): Cipher {
//    val hashed = seed.toUnicodeByteArray().sha512()
//    val skSpec = SecretKeySpec(hashed, 0, 32, KeyProperties.KEY_ALGORITHM_AES)
//    val ivSpec = IvParameterSpec(hashed, 32, 16)
//    return Cipher.getInstance(KEY_ALGORITHM_AES_CBC_PKCS5).apply {
//        init(Cipher.ENCRYPT_MODE, skSpec, ivSpec)
//    }
//}
//
//fun aesDecryptCipher(seed: String): Cipher {
//    val hashed = seed.toUnicodeByteArray().sha512()
//    val skSpec = SecretKeySpec(hashed, 0, 32, KeyProperties.KEY_ALGORITHM_AES)
//    val ivSpec = IvParameterSpec(hashed, 32, 16)
//    return Cipher.getInstance(KEY_ALGORITHM_AES_CBC_PKCS5).apply {
//        init(Cipher.DECRYPT_MODE, skSpec, ivSpec)
//    }
//}
//
//fun aesEncryptWithChecksum(privateKey: PrivateKey, publicKey: PublicKey, message: ByteArray): ByteArray {
//    val secret: ByteArray = publicKey.ecKey!!.pubKeyPoint.multiply(privateKey.ecKey!!.privKey).normalize().xCoord.encoded
//    val seed: ByteArray = secret.sha512().toHexString().toUnicodeByteArray()
//    val checksumMessage: ByteArray = message.sha256().copyOfRange(0, 4) + message
//    return aesEncrypt(seed, checksumMessage)
//}
//
//fun aesDecryptWithChecksum(privateKey: PrivateKey, publicKey: PublicKey, encrypted: ByteArray): ByteArray {
//    require(encrypted.size > 4)
//    val secret: ByteArray = publicKey.ecKey!!.pubKeyPoint.multiply(privateKey.ecKey!!.privKey).normalize().xCoord.encoded
//    val seed: ByteArray = secret.sha512().toHexString().toUnicodeByteArray()
//    val checksumMessage: ByteArray = aesDecrypt(seed, encrypted)
//    val message = checksumMessage.copyOfRange(4, checksumMessage.size)
//    require(message.sha256().copyOfRange(0, 4).contentEquals(checksumMessage.copyOfRange(0, 4)))
//    return message
//}
//
//
//
//fun nextSecureRandomBytes(size: Int = 64): ByteArray = ByteArray(size.coerceAtLeast(0)).also { SecureRandom.getInstanceStrong().nextBytes(it) }
//
//fun nextSecureRandomULong(): ULong = SecureRandom.getInstanceStrong().nextLong().toULong()
//
//
//fun String.decodeBase58(): ByteArray = Base58.decode(this)
//fun ByteArray.encodeBase58(): String = Base58.encode(this)
//fun String.decodeBase58OrElse(onFailure: (exception: Throwable) -> ByteArray): ByteArray = runCatching { Base58.decode(this) }.getOrElse(onFailure)
//fun ByteArray.encodeBase58OrElse(onFailure: (exception: Throwable) -> String): String = runCatching { Base58.encode(this) }.getOrElse(onFailure)
//fun String?.decodeBase58OrNull(): ByteArray? = runCatching { Base58.decode(this!!) }.getOrNull()
//fun ByteArray?.encodeBase58OrNull(): String? = runCatching { Base58.encode(this!!) }.getOrNull()
//
//
//fun String.decodeBase64(): ByteArray = Base64.decode(this)
//fun ByteArray.encodeBase64(): String = Base64.encode(this).toUnicodeString()
//fun String.decodeBase64OrEmpty(): ByteArray = try { Base64.decode(this) } catch (e: DecoderException) { ByteArray(0) }
//fun ByteArray.encodeBase64OrEmpty(): String = try { Base64.encode(this).toUnicodeString() } catch (e: DecoderException) { EMPTY_SPACE }
//
//
//


