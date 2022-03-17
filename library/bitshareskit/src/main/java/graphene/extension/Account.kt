package graphene.extension

import graphene.protocol.GRAPHENE_PROXY_TO_SELF_ACCOUNT
import graphene.protocol.K102_AccountType
import graphene.protocol.PublicKeyType

val K102_AccountType.isVoting: Boolean
    get() = options.votingAccount.id != GRAPHENE_PROXY_TO_SELF_ACCOUNT.id || !options.vote.isEmpty()


val K102_AccountType.memoKey: PublicKeyType
    get() = options.memoKey
