package graphene.protocol


private fun String.toVote(): VoteIdType {
    return VoteIdType.fromStringId(this)
}