package bitshareskit.objects

enum class Namespace(val id: Int) {
    NULL_SPACE(-1),
    RELATED_SPACE(0),
    PROTOCOL_SPACE(1),
    IMPLEMENTATION_SPACE(2),
    ACCOUNT_HISTORY_SPACE(5),
}