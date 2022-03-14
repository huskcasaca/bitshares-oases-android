package bitshareskit.errors

import graphene.app.BlockchainAPI

class UnsupportedCallException(api: BlockchainAPI) : GrapheneException(ErrorCode.UNSUPPORTED_API) {

}