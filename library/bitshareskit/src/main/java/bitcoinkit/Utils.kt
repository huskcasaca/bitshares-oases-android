package bitcoinkit

import org.bouncycastle.crypto.digests.RIPEMD160Digest
import org.bouncycastle.crypto.digests.SHA512Digest
import org.bouncycastle.crypto.macs.HMac
import org.bouncycastle.crypto.params.KeyParameter
import java.io.IOException
import java.io.InputStream
import java.lang.RuntimeException
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.*
import kotlin.experimental.and
import kotlin.experimental.or

/**
 * Static utility methods
 */
object Utils {
    /** Bit masks (Low-order bit is bit 0 and high-order bit is bit 7)  */
    private val bitMask = intArrayOf(0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80)

    /** Strong random number generator  */
    private val rnd: Random = SecureRandom()

    /** Instance of a SHA-256 digest which we will use as needed  */
    private val digest: MessageDigest =
        try {
            MessageDigest.getInstance("SHA-256")
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e) // Can't happen.
        }
    /**
     * Calculate the SHA-256 hash of the input and then hash the resulting hash again
     *
     * @param       input           Data to be hashed
     * @param       offset          Starting offset within the data
     * @param       length          Number of data bytes to hash
     * @return The hash digest
     */
    /**
     * Calculate the SHA-256 hash of the input and then hash the resulting hash again
     *
     * @param       input           Data to be hashed
     * @return The hash digest
     */
    @JvmOverloads
    fun doubleDigest(input: ByteArray, offset: Int = 0, length: Int = input.size): ByteArray {
        var bytes: ByteArray
        synchronized(digest!!) {
            digest.reset()
            digest.update(input, offset, length)
            val first = digest.digest()
            bytes = digest.digest(first)
        }
        return bytes
    }

    /**
     * Calculate RIPEMD160(SHA256(input)).  This is used in Address calculations.
     *
     * @param       input           The byte array to be hashed
     * @return The hashed result
     */
    fun sha256Hash160(input: ByteArray?): ByteArray {
        val out = ByteArray(20)
        synchronized(digest!!) {
            digest.reset()
            val sha256 = digest.digest(input)
            val rDigest = RIPEMD160Digest()
            rDigest.update(sha256, 0, sha256.size)
            rDigest.doFinal(out, 0)
        }
        return out
    }

    /**
     * Calculate RIPEMD160(input).  This is used in Address calculations.
     *
     * @param       input           The byte array to be hashed
     * @return The hashed result
     */
    fun ripemd160(input: ByteArray): ByteArray {
        val out = ByteArray(20)
        synchronized(digest!!) {
            digest.reset()
            val rDigest = RIPEMD160Digest()
            rDigest.update(input, 0, input.size)
            rDigest.doFinal(out, 0)
        }
        return out
    }

    /**
     * Calculate the HMAC-SHA512 digest for use with BIP 32
     *
     * @param       key             Key
     * @param       input           Bytes to be hashed
     * @return Hashed result
     */
    fun hmacSha512(key: ByteArray?, input: ByteArray): ByteArray {
        val hmac = HMac(SHA512Digest())
        hmac.init(KeyParameter(key))
        hmac.update(input, 0, input.size)
        val out = ByteArray(64)
        hmac.doFinal(out, 0)
        return out
    }

    /**
     * The regular [java.math.BigInteger.toByteArray] method isn't quite what we often need: it appends a
     * leading zero to indicate that the number is positive and may need padding.
     *
     * @param b the integer to format into a byte array
     * @param numBytes the desired size of the resulting byte array
     * @return numBytes byte long array.
     */
    fun bigIntegerToBytes(b: BigInteger?, numBytes: Int): ByteArray? {
        if (b == null) {
            return null
        }
        val bytes = ByteArray(numBytes)
        val biBytes = b.toByteArray()
        val start = if (biBytes.size == numBytes + 1) 1 else 0
        val length = Math.min(biBytes.size, numBytes)
        System.arraycopy(biBytes, start, bytes, numBytes - length, length)
        return bytes
    }

    /**
     * Checks if the specified bit is set
     *
     * @param       data            Byte array to check
     * @param       index           Bit position
     * @return TRUE if the bit is set
     */
    fun checkBitLE(data: ByteArray, index: Int): Boolean {
        return data[index ushr 3] and bitMask[7 and index].toByte() != 0.toByte()
    }

    /**
     * Sets the specified bit
     * @param       data            Byte array
     * @param       index           Bit position
     */
    fun setBitLE(data: ByteArray, index: Int) {
        data[index ushr 3] = data[index ushr 3] or bitMask[7 and index].toByte()
    }

    /**
     * Calculate SHA256(SHA256(byte range 1 + byte range 2)).
     *
     * @param       input1          First input byte array
     * @param       offset1         Starting position in the first array
     * @param       length1         Number of bytes to process in the first array
     * @param       input2          Second input byte array
     * @param       offset2         Starting position in the second array
     * @param       length2         Number of bytes to process in the second array
     * @return The SHA-256 digest
     */
    fun doubleDigestTwoBuffers(
        input1: ByteArray?, offset1: Int, length1: Int,
        input2: ByteArray?, offset2: Int, length2: Int
    ): ByteArray {
        var bytes: ByteArray
        synchronized(digest!!) {
            digest.reset()
            digest.update(input1, offset1, length1)
            digest.update(input2, offset2, length2)
            val first = digest.digest()
            bytes = digest.digest(first)
        }
        return bytes
    }

    /**
     * Form a long value from a 4-byte array in big-endian format
     *
     * @param       bytes           The byte array
     * @param       offset          Starting offset within the array
     * @return The long value
     */
    fun readUint32BE(bytes: ByteArray, offset: Int): Long {
        var offset = offset
        return bytes[offset++].toLong() and 0x00FFL shl 24 or
                (bytes[offset++].toLong() and 0x00FFL shl 16) or
                (bytes[offset++].toLong() and 0x00FFL shl 8) or
                (bytes[offset].toLong() and 0x00FFL)
    }

    /** Parse 2 bytes from the stream as unsigned 16-bit integer in little endian format.  */
    fun readUint16FromStream(`is`: InputStream): Int {
        return try {
            `is`.read() and 0xff or
                    (`is`.read() and 0xff shl 8)
        } catch (x: IOException) {
            throw RuntimeException(x)
        }
    }

    /** Parse 4 bytes from the stream as unsigned 32-bit integer in little endian format.  */
    fun readUint32FromStream(`is`: InputStream): Long {
        return try {
            (`is`.read() and 0xff or
                    (`is`.read() and 0xff shl 8) or
                    (`is`.read() and 0xff shl 16) or
                    (`is`.read() and 0xff shl 24)).toLong()
        } catch (x: IOException) {
            throw RuntimeException(x)
        }
    }

    /**
     * Write an unsigned 32-bit value to a byte array in little-endian format
     *
     * @param       val             Value to be written
     * @param       out             Output array
     * @param       offset          Starting offset
     */
    fun uint32ToByteArrayLE(`val`: Long, out: ByteArray, offset: Int) {
        var offset = offset
        out[offset++] = `val`.toByte()
        out[offset++] = (`val` shr 8).toByte()
        out[offset++] = (`val` shr 16).toByte()
        out[offset] = (`val` shr 24).toByte()
    }

    /**
     * Write an unsigned 64-bit value to a byte array in little-endian format
     *
     * @param       val             Value to be written
     * @param       out             Output array
     * @param       offset          Starting offset
     */
    fun uint64ToByteArrayLE(`val`: Long, out: ByteArray, offset: Int) {
        var offset = offset
        out[offset++] = `val`.toByte()
        out[offset++] = (`val` shr 8).toByte()
        out[offset++] = (`val` shr 16).toByte()
        out[offset++] = (`val` shr 24).toByte()
        out[offset++] = (`val` shr 32).toByte()
        out[offset++] = (`val` shr 40).toByte()
        out[offset++] = (`val` shr 48).toByte()
        out[offset] = (`val` shr 56).toByte()
    }

    /**
     * Encode the value in little-endian format
     *
     * @param       value           Value to encode
     * @return Byte array
     */
    fun encode(value: Long): ByteArray {
        val bytes: ByteArray
        if (value and -0x100000000L != 0L) {
            // 1 marker + 8 data bytes
            bytes = ByteArray(9)
            bytes[0] = 255.toByte()
            uint64ToByteArrayLE(value, bytes, 1)
        } else if (value and 0x00000000FFFF0000L != 0L) {
            // 1 marker + 4 data bytes
            bytes = ByteArray(5)
            bytes[0] = 254.toByte()
            uint32ToByteArrayLE(value, bytes, 1)
        } else if (value >= 253L) {
            // 1 marker + 2 data bytes
            bytes = byteArrayOf(253.toByte(), value.toByte(), (value shr 8).toByte())
        } else {
            // Single data byte
            bytes = byteArrayOf(value.toByte())
        }
        return bytes
    }

    fun intToByteArray(value: Int): ByteArray {
        return byteArrayOf((value shr 24).toByte(), (value shr 16).toByte(), (value shr 8).toByte(), value.toByte())
    }

    /** Generate random long number  */
    fun randomLong(): Long {
        return (rnd.nextDouble() * Long.MAX_VALUE).toLong()
    }

    /** Generate random number  */
    fun randomInt(): Int {
        return (rnd.nextDouble() * Int.MAX_VALUE).toInt()
    }
}