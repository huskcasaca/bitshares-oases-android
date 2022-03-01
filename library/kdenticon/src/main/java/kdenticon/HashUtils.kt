package kdenticon

import java.security.MessageDigest

object HashUtils {

    const val MD5 = 0
    const val SHA1 = 1
    const val SHA256 = 2
    const val SHA512 = 3

    fun md5(input: String) = hashString("md5", input)

    fun sha1(input: String) = hashString("SHA-1", input)

    fun sha256(input: String) = hashString("SHA-256", input)

    fun sha512(input: String) = hashString("SHA-512", input)

    /**
     * Supported algorithms on Android:
     *
     * Algorithm	Supported API Levels
     * MD5          1+
     * SHA-1	    1+
     * SHA-224	    1-8,22+
     * SHA-256	    1+
     * SHA-384	    1+
     * SHA-512	    1+
     */

    private const val HEX_CHARS = "0123456789ABCDEF"

    private fun hashString(type: String, input: String): String {
        val bytes = MessageDigest
                .getInstance(type)
                .digest(input.toByteArray())
        val result = StringBuilder(bytes.size * 2)

        bytes.forEach {
            val i = it.toInt()
            result.append(HEX_CHARS[i shr 4 and 0x0f])
            result.append(HEX_CHARS[i and 0x0f])
        }

        return result.toString()
    }
}