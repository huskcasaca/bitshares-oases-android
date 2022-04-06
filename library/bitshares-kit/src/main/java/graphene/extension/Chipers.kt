package graphene.extension

import bitcoinkit.Base58
import org.bouncycastle.util.encoders.Base64
import org.bouncycastle.util.encoders.DecoderException
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

//const val KEY_ALGORITHM_AES_CBC_PKCS7 = "${KEY_ALGORITHM_AES}/${BLOCK_MODE_CBC}/${ENCRYPTION_PADDING_NONE}"
const val KEY_ALGORITHM_AES_CBC_PKCS5 = "AES/CBC/PKCS5Padding"
const val KEY_ALGORITHM_AES = "AES"

fun aesEncrypt(key: ByteArray, data: ByteArray): ByteArray {
    val hashed = key.sha512()
    val skSpec = SecretKeySpec(hashed, 0, 32, KEY_ALGORITHM_AES)
    val ivSpec = IvParameterSpec(hashed, 32, 16)
    Cipher.getInstance(KEY_ALGORITHM_AES_CBC_PKCS5).apply {
        init(Cipher.ENCRYPT_MODE, skSpec, ivSpec)
        return doFinal(data)
    }
}

fun aesDecrypt(key: ByteArray, data: ByteArray): ByteArray {
    val hashed = key.sha512()
    val skSpec = SecretKeySpec(hashed, 0, 32, KEY_ALGORITHM_AES)
    val ivSpec = IvParameterSpec(hashed, 32, 16)
    Cipher.getInstance(KEY_ALGORITHM_AES_CBC_PKCS5).apply {
        init(Cipher.DECRYPT_MODE, skSpec, ivSpec)
        return doFinal(data)
    }
}

fun aesEncryptCipher(seed: String): Cipher {
    val hashed = seed.toUnicodeByteArray().sha512()
    val skSpec = SecretKeySpec(hashed, 0, 32, KEY_ALGORITHM_AES)
    val ivSpec = IvParameterSpec(hashed, 32, 16)
    return Cipher.getInstance(KEY_ALGORITHM_AES_CBC_PKCS5).apply {
        init(Cipher.ENCRYPT_MODE, skSpec, ivSpec)
    }
}

fun aesDecryptCipher(seed: String): Cipher {
    val hashed = seed.toUnicodeByteArray().sha512()
    val skSpec = SecretKeySpec(hashed, 0, 32, KEY_ALGORITHM_AES)
    val ivSpec = IvParameterSpec(hashed, 32, 16)
    return Cipher.getInstance(KEY_ALGORITHM_AES_CBC_PKCS5).apply {
        init(Cipher.DECRYPT_MODE, skSpec, ivSpec)
    }
}


fun nextSecureRandomBytes(size: Int = 64): ByteArray = ByteArray(size.coerceAtLeast(0)).also { SecureRandom.getInstanceStrong().nextBytes(it) }
fun nextSecureRandomULong(): ULong = SecureRandom.getInstanceStrong().nextLong().toULong()


fun String.decodeBase58(): ByteArray = Base58.decode(this)
fun ByteArray.encodeBase58(): String = Base58.encode(this)
@Deprecated("") fun String.decodeBase58OrElse(onFailure: (exception: Throwable) -> ByteArray): ByteArray = runCatching { Base58.decode(this) }.getOrElse(onFailure)
@Deprecated("") fun ByteArray.encodeBase58OrElse(onFailure: (exception: Throwable) -> String): String = runCatching { Base58.encode(this) }.getOrElse(onFailure)
fun String.decodeBase58OrNull(): ByteArray? = runCatching { Base58.decode(this) }.getOrNull()
fun ByteArray.encodeBase58OrNull(): String? = runCatching { Base58.encode(this) }.getOrNull()


fun String.decodeBase64(): ByteArray = Base64.decode(this)
fun ByteArray.encodeBase64(): String = Base64.encode(this).toUnicodeString()
@Deprecated("") fun String.decodeBase64OrEmpty(): ByteArray = try { Base64.decode(this) } catch (e: DecoderException) { ByteArray(0) }
@Deprecated("") fun ByteArray.encodeBase64OrEmpty(): String = try { Base64.encode(this).toUnicodeString() } catch (e: DecoderException) { "" }

