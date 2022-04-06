package bitcoinkit.hd_wallet

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.xor
import kotlin.math.ceil
import kotlin.math.pow

/**
 * This is a clean-room implementation of PBKDF2 using RFC 2898 as a reference.
 *
 * RFC 2898: http://tools.ietf.org/html/rfc2898#section-5.2
 *
 * This code passes all RFC 6070 test vectors: http://tools.ietf.org/html/rfc6070
 *
 * http://cryptofreek.org/2012/11/29/pbkdf2-pure-java-implementation/<br></br>
 * Modified to use SHA-512 - Ken Sedgwick ken@bonsai.com
 */
object PBKDF2SHA512 {
    fun derive(P: String, S: String, c: Int, dkLen: Int): ByteArray {
        val outputStream = ByteArrayOutputStream()
        try {
            val hLen = 20
            if (dkLen > (2.0.pow(32.0) - 1) * hLen) {
                throw IllegalArgumentException("derived key too long")
            } else {
                val l = ceil(dkLen.toDouble() / hLen.toDouble()).toInt()

                for (i in 1..l) {
                    val bytes = function(P, S, c, i)
                    outputStream.write(bytes!!)
                }
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        val baDerived = ByteArray(dkLen)
        System.arraycopy(outputStream.toByteArray(), 0, baDerived, 0, baDerived.size)

        return baDerived
    }

    @Throws(Exception::class)
    private fun function(P: String, S: String, c: Int, i: Int): ByteArray? {
        var uLast: ByteArray? = null
        var uXor: ByteArray? = null

        val key = SecretKeySpec(P.toByteArray(charset("UTF-8")), "HmacSHA512")
        val mac = Mac.getInstance(key.algorithm)
        mac.init(key)

        for (j in 0 until c) {
            if (j == 0) {
                val baS = S.toByteArray(charset("UTF-8"))
                val baI = INT(i)
                val baU = ByteArray(baS.size + baI.size)

                System.arraycopy(baS, 0, baU, 0, baS.size)
                System.arraycopy(baI, 0, baU, baS.size, baI.size)

                uXor = mac.doFinal(baU)
                uLast = uXor
                mac.reset()
            } else {
                val baU = mac.doFinal(uLast)
                mac.reset()

                for (k in uXor!!.indices) {
                    uXor[k] = (uXor[k].xor(baU[k]))
                }

                uLast = baU
            }
        }

        return uXor
    }

    private fun INT(i: Int): ByteArray {
        val bb = ByteBuffer.allocate(4)
        bb.order(ByteOrder.BIG_ENDIAN)
        bb.putInt(i)

        return bb.array()
    }
}
