package graphene.extension

import graphene.protocol.GRAPHENE_PROXY_TO_SELF_ACCOUNT
import graphene.protocol.AccountIdType
import graphene.protocol.PublicKeyType

val AccountIdType.isVoting: Boolean
    get() = options.votingAccount.id != GRAPHENE_PROXY_TO_SELF_ACCOUNT.id || !options.vote.isEmpty()


val AccountIdType.memoKey: PublicKeyType
    get() = options.memoKey
