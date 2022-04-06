package bitcoinkit

import bitcoinkit.Utils.doubleDigest
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * Provides Base-58 encoding and decoding
 */
object Base58 {
    /**
     * Alphabet used for encoding and decoding
     */
    private val ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray()

    /**
     * Lookup index for US-ASCII characters (code points 0-127)
     */
    private val INDEXES = IntArray(128)

    /**
     * Encodes a byte array as a Base58 string
     *
     * @param bytes Array to be encoded
     * @return Encoded string
     */
    fun encode(bytes: ByteArray): String {
        //
        // Nothing to do for an empty array
        //
        if (bytes.isEmpty()) return ""
        //
        // Make a copy of the input since we will be modifying it as we go along
        //
        val input = bytes.copyOf(bytes.size)
        //
        // Count the number of leading zeroes (we will need to prefix the encoded result
        // with this many zero characters)
        //
        var zeroCount = 0
        while (zeroCount < input.size && input[zeroCount] == 0.toByte()) zeroCount++
        //
        // Encode the input starting with the first non-zero byte
        //
        var offset = zeroCount
        val encoded = ByteArray(input.size * 2)
        var encodedOffset = encoded.size
        while (offset < input.size) {
            val mod = divMod58(input, offset)
            if (input[offset] == 0.toByte()) offset++
            encoded[--encodedOffset] = ALPHABET[mod.toInt()].toByte()
        }
        //
        // Strip any leading zero values in the encoded result
        //
        while (encodedOffset < encoded.size && encoded[encodedOffset] == ALPHABET[0].toByte()) encodedOffset++
        //
        // Now add the number of leading zeroes that we found in the input array
        //
        for (i in 0 until zeroCount) encoded[--encodedOffset] = ALPHABET[0].toByte()
        //
        // Create the return string from the encoded bytes
        //
        val encodedResult: String
        val stringBytes = encoded.copyOfRange(encodedOffset, encoded.size)
        encodedResult = String(stringBytes, StandardCharsets.US_ASCII)
        return encodedResult
    }

    /**
     * Decodes a Base58 string
     *
     * @param string Encoded string
     * @return Decoded bytes
     * @throws IllegalArgumentException Invalid Base-58 encoded string
     */
    @Throws(IllegalArgumentException::class)
    fun decode(string: String): ByteArray {
        //
        // Nothing to do if we have an empty string
        //
        if (string.isEmpty()) return ByteArray(0)
        //
        // Convert the input string to a byte sequence
        //
        val input = ByteArray(string.length)
        string.indices.forEach { i ->
            val codePoint = string.codePointAt(i)
            var digit = -1
            if (codePoint >= 0 && codePoint < INDEXES.size) digit = INDEXES[codePoint]
            require(digit >= 0) { String.format("Illegal character %c at index %d", string[i], i) }
            input[i] = digit.toByte()
        }
        //
        // Count the number of leading zero characters
        //
        var zeroCount = 0
        while (zeroCount < input.size && input[zeroCount] == 0.toByte()) zeroCount++
        //
        // Convert from Base58 encoding starting with the first non-zero character
        //
        val decoded = ByteArray(input.size)
        var decodedOffset = decoded.size
        var offset = zeroCount
        while (offset < input.size) {
            val mod = divMod256(input, offset)
            if (input[offset] == 0.toByte()) offset++
            decoded[--decodedOffset] = mod
        }
        //
        // Strip leading zeroes from the decoded result
        //
        while (decodedOffset < decoded.size && decoded[decodedOffset] == 0.toByte()) decodedOffset++
        //
        // Return the decoded result prefixed with the number of leading zeroes
        // that were in the original string
        //
        return Arrays.copyOfRange(decoded, decodedOffset - zeroCount, decoded.size)
    }

    /**
     * Decode a Base58-encoded checksummed string and verify the checksum.  The
     * checksum will then be removed from the decoded value.
     *
     * @param string Base-58 encoded checksummed string
     * @return Decoded value
     * @throws IllegalArgumentException The string is not valid or the checksum is incorrect
     */
    @Throws(IllegalArgumentException::class)
    fun decodeChecked(string: String): ByteArray {
        //
        // Decode the string
        //
        val decoded = decode(string)
        require(decoded.size >= 4) { "Decoded string is too short" }
        //
        // Verify the checksum contained in the last 4 bytes
        //
        val bytes = decoded.copyOfRange(0, decoded.size - 4)
        val checksum = decoded.copyOfRange(decoded.size - 4, decoded.size)
        val hash: ByteArray = doubleDigest(bytes).copyOfRange(0, 4)
        require(hash.contentEquals(checksum)) { "Checksum is not correct" }
        //
        // Return the result without the checksum bytes
        //
        return bytes
    }

    /**
     * Divide the current number by 58 and return the remainder.  The input array
     * is updated for the next round.
     *
     * @param number Number array
     * @param offset Offset within the array
     * @return The remainder
     */
    private fun divMod58(number: ByteArray, offset: Int): Byte {
        var remainder = 0
        for (i in offset until number.size) {
            val digit = number[i].toInt() and 0xff
            val temp = remainder * 256 + digit
            number[i] = (temp / 58).toByte()
            remainder = temp % 58
        }
        return remainder.toByte()
    }

    /**
     * Divide the current number by 256 and return the remainder.  The input array
     * is updated for the next round.
     *
     * @param number Number array
     * @param offset Offset within the array
     * @return The remainder
     */
    private fun divMod256(number: ByteArray, offset: Int): Byte {
        var remainder = 0
        for (i in offset until number.size) {
            val digit = number[i].toInt() and 0xff
            val temp = remainder * 58 + digit
            number[i] = (temp / 256).toByte()
            remainder = temp % 256
        }
        return remainder.toByte()
    }

    init {
        Arrays.fill(INDEXES, -1)
        for (i in ALPHABET.indices) INDEXES[ALPHABET[i].toInt()] = i
    }
}