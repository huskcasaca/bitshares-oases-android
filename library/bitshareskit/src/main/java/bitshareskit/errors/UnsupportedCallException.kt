package bitshareskit.errors

import bitshareskit.ks_chain.BlockchainAPI

class UnsupportedCallException(api: BlockchainAPI) : GrapheneException(ErrorCode.UNSUPPORTED_API) {

}