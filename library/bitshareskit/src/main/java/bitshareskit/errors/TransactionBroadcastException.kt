package bitshareskit.errors

class TransactionBroadcastException(code: ErrorCode, message: String = code.des) : GrapheneException(code, message)