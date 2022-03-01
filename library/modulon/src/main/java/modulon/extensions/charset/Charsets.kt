package modulon.extensions.charset


fun String?.toByteArrayOrEmpty(): ByteArray = this?.toByteArray() ?: byteArrayOf()

fun ByteArray?.orEmpty(): ByteArray = this ?: byteArrayOf()

fun String.toUnicodeByteArray() = this.toByteArray(Charsets.UTF_8)

fun ByteArray.toUnicodeString() = this.toString(Charsets.UTF_8)

fun String.toHexByteArray() = ByteArray(length / 2) { substring(it * 2, it * 2 + 2).toInt(16).toByte() }
fun String?.toHexByteArrayOrEmpty() = this?.toHexByteArray() ?: byteArrayOf()

fun ByteArray.toHexString() = this.joinToString(separator = "") { String.format("%02x", (it.toInt() and 0xFF)) }

fun emptyString() = ""
fun blankString(count: Int = 1) = " ".repeat(count.coerceAtLeast(1))

const val EMPTY_SPACE = ""
const val BLANK_SPACE = " "
const val ZERO_WIDTH_BLANK_SPACE = "\u200b"
const val NEWLINE = "\n"

