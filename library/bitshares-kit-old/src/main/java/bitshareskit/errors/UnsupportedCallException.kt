package bitshareskit.errors

import bitshareskit.chain.BlockchainAPI

class UnsupportedCallException(api: BlockchainAPI) : GrapheneException(ErrorCode.UNSUPPORTED_API) {

}