package graphene.extension



@Deprecated("") fun String?.toByteArrayOrEmpty(): ByteArray = this?.toByteArray() ?: byteArrayOf()
@Deprecated("") fun ByteArray?.orEmpty(): ByteArray = this ?: byteArrayOf()
@Deprecated("") fun String?.toHexByteArrayOrEmpty() = this?.toHexByteArray() ?: byteArrayOf()

fun String.toUnicodeByteArray() = this.toByteArray(Charsets.UTF_8)
fun ByteArray.toUnicodeString() = this.toString(Charsets.UTF_8)
fun String.toHexByteArray() = chunked(2).map { it.toInt(16).toByte() }.toByteArray()
fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

//internal const val EMPTY_SPACE = ""
internal const val BLANK_SPACE = " "
internal const val ZERO_WIDTH_BLANK_SPACE = "\u200b"
internal const val NEWLINE = "\n"








