package graphene.chain

import graphene.protocol.*
import graphene.serializers.TimePointSecSerializer
import kotlinx.datetime.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class K118_TicketObject(
    @SerialName("id")
    override val id: TicketId,
    @SerialName("account")
    val account: AccountIdType, //< The account who owns the ticket
    @SerialName("target_type")
    val targetType: TicketType, //< The target type of the ticket
    @SerialName("amount")
    val amount : Asset, //< The token type and amount in the ticket
    @SerialName("current_type")
    val currentType: TicketType, //< The current type of the ticket
    @SerialName("status")
    val status: TicketStatus, //< The status of the ticket
    @SerialName("value")
    val value: ShareType, //< The current value of the ticket
    @SerialName("next_auto_update_time") @Serializable(TimePointSecSerializer::class)
    val nextAutoUpdateTime: Instant,///< The next time that the ticket will be automatically updated
    // When the account has ever started a downgrade or withdrawal, the scheduled auto-update time is stored here
    @SerialName("next_type_downgrade_time") @Serializable(TimePointSecSerializer::class)
    val nextTypeDowngradeTime: Instant,
) : AbstractObject(), TicketIdType {

    companion object {

        val lockForeverUpdateSteps: UInt32 = 4U
        val secondsPerLockForeverUpdateStep: UInt32 = 180U * 86400U
        val secondsPerChargingStep: UInt32 = 15U * 86400U
        val secondsToCancelCharging: UInt32 = 7U * 86400U
        //static uint32_t seconds_to_downgrade( ticket_type i ) {
        //    static constexpr uint32_t _seconds_to_downgrade[] = { 180 * 86400, 180 * 86400, 360 * 86400 };
        //    return _seconds_to_downgrade[ static_cast<uint8_t>(i) ];
        //}
        //static uint8_t value_multiplier( ticket_type i, ticket_version version ) {
        //    static constexpr uint32_t _value_multiplier_v1[] = { 1, 2, 4, 8, 8, 0 };
        //    static constexpr uint32_t _value_multiplier_v2[] = { 0, 2, 4, 8, 8, 0 };
        //    return ( version == ticket_v1 ? _value_multiplier_v1[ static_cast<uint8_t>(i) ]
        //    : _value_multiplier_v2[ static_cast<uint8_t>(i) ] );
        //}
    }

    fun a() {
        "2010-06-01T22:19:44.475Z".toInstant()
        "2010-06-01T22:19:44".toLocalDateTime()
        "2010-06-01".toLocalDate()
    }

    //// Configurations

    //
    ///// Initialize member variables for a ticket newly created from account balance
    //void init_new( time_point_sec now, account_id_type new_account,
    //ticket_type new_target_type, const asset& new_amount, ticket_version version );
    //
    ///// Initialize member variables for a ticket split from another ticket
    //void init_split( time_point_sec now, const ticket_object& old_ticket,
    //ticket_type new_target_type, const asset& new_amount, ticket_version version );
    //
    ///// Set a new target type and update member variables accordingly
    //void update_target_type( time_point_sec now, ticket_type new_target_type, ticket_version version );
    //
    ///// Adjust amount and update member variables accordingly
    //void adjust_amount( const asset& delta_amount, ticket_version version );
    //
    ///// Update the ticket when it's time
    //void auto_update( ticket_version version );
    //
    //private:
    ///// Recalculate value of the ticket
    //void update_value( ticket_version version );

}

@Serializable
enum class TicketStatus {
    @SerialName("charging") CHARGING,
    @SerialName("stable") STABLE,
    @SerialName("withdrawing") WITHDRAWING,
}


// Version of a ticket
enum class TicketVersion(val version: Int32) {
    TICKET_V1(1),
    TICKET_V2(2),
}
