package bitshareskit.errors

@Suppress("ThrowableNotThrown")
open class GrapheneException(
    val code: ErrorCode = ErrorCode.UNKNOWN_ERROR,
    override val message: String = ""
) : Exception(code.des) {

    companion object {

        val TX_303000X = ErrorCode.MISSING_ACTIVE_AUTH.code..ErrorCode.INSUFFICIENT_FEE.code
        val TX_LOCAL = ErrorCode.MISSING_ACTIVE_AUTH.code..ErrorCode.INSUFFICIENT_FEE.code
        val FC_GRAPHENE = ErrorCode.UNSPECIFIED_EXCEPTION.code..ErrorCode.METHOD_NOT_FOUND_EXCEPTION.code

        fun resolveCode(code: Int): ErrorCode {
            return ErrorCode.values().find { it.code == code } ?: ErrorCode.UNKNOWN_ERROR
        }

        fun resolve(code: Int, message: String = ""): GrapheneException {
            val resolved = resolveCode(code)
            return when (code) {
                in TX_LOCAL -> TransactionBroadcastException(resolved, message)
                in TX_303000X -> TransactionBroadcastException(resolved, message)
                in FC_GRAPHENE -> FcDeclareException(resolved, message)
                else -> GrapheneException()
            }
        }

    }

}

class FcDeclareException(code: ErrorCode, message: String) : GrapheneException(code, message)