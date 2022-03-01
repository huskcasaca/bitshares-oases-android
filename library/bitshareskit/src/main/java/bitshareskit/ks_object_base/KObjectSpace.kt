package bitshareskit.ks_object_base

enum class KObjectSpace(val space: UInt8) {
    RELATED_SPACE(0U),
    PROTOCOL_SPACE(1U),
    IMPLEMENTATION_SPACE(2U),
    ACCOUNT_HISTORY_SPACE(5U),
}