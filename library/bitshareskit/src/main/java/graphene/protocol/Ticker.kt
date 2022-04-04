package graphene.protocol

import kotlinx.serialization.SerialInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TicketType {
    @SerialName("liquid") LIQUID,
    @SerialName("lock_180_days") LOCK_180_DAYS,
    @SerialName("lock_360_days") LOCK_360_DAYS,
    @SerialName("lock_720_days") LOCK_720_DAYS,
    @SerialName("lock_forever") LOCK_FOREVER,
    @SerialName("ticket_type_count") TICKET_TYPE_COUNT,
}

@Serializable
enum class TicketStatus {
    @SerialName("charging") CHARGING,
    @SerialName("stable") STABLE,
    @SerialName("withdrawing") WITHDRAWING,
}