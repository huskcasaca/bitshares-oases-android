package graphene.extension

import bitcoinkit.Utils
import java.security.MessageDigest

private const val KEY_ALGORITHM_MD5 = "MD5"
private const val KEY_ALGORITHM_SHA1 = "SHA-1"
private const val KEY_ALGORITHM_SHA256 = "SHA-256"
private const val KEY_ALGORITHM_SHA512 = "SHA-512"

private val md5Digest: MessageDigest by lazy { MessageDigest.getInstance(KEY_ALGORITHM_MD5) }
private val sha1Digest: MessageDigest by lazy { MessageDigest.getInstance(KEY_ALGORITHM_SHA1) }
private val sha256Digest: MessageDigest by lazy { MessageDigest.getInstance(KEY_ALGORITHM_SHA256) }
private val sha512Digest: MessageDigest by lazy { MessageDigest.getInstance(KEY_ALGORITHM_SHA512) }


fun String.md5(): ByteArray = md5Digest.digest(toByteArray())
fun String.sha1(): ByteArray = sha1Digest.digest(toByteArray())
fun String.sha256(): ByteArray = sha256Digest.digest(toByteArray())
fun String.sha512(): ByteArray = sha512Digest.digest(toByteArray())

fun ByteArray.md5(): ByteArray = md5Digest.digest(this)
fun ByteArray.sha1(): ByteArray = sha1Digest.digest(this)
fun ByteArray.sha256(): ByteArray = sha256Digest.digest(this)
fun ByteArray.sha512(): ByteArray = sha512Digest.digest(this)

fun ByteArray.ripemd160(): ByteArray = Utils.ripemd160(this)


fun String.sha256(iteration: Int): ByteArray {
    var holder = this.toByteArray()
    repeat(iteration) {
        holder = sha256Digest.digest(holder)
    }
    return holder
}

fun ByteArray.sha256(iteration: Int): ByteArray {
    var holder = this
    repeat(iteration) {
        holder = sha256Digest.digest(holder)
    }
    return holder
}


