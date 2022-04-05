package graphene.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OperationWrapper(
    @SerialName("op") val operation: Operation,
)