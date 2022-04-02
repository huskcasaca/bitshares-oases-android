package graphene.extension

import graphene.protocol.GRAPHENE_PROXY_TO_SELF_ACCOUNT
import graphene.protocol.AccountType
import graphene.protocol.PublicKeyType

val AccountType.isVoting: Boolean
    get() = options.votingAccount.id != GRAPHENE_PROXY_TO_SELF_ACCOUNT.id || !options.vote.isEmpty()


val AccountType.memoKey: PublicKeyType
    get() = options.memoKey
