package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K201_DynamicGlobalPropertyObject(
    @SerialName("id")
    override val id: K201_DynamicGlobalPropertyIdType,
    @SerialName("head_block_number")      val head_block_number    : UInt32, //  = 0
    @SerialName("head_block_id")          val head_block_id        : BlockIdType,
    @SerialName("time")                   val time                 : ChainTimePoint,
    @SerialName("current_witness")        val current_witness      : K106_WitnessType,
    @SerialName("next_maintenance_time")  val next_maintenance_time: ChainTimePoint,
    @SerialName("last_vote_tally_time")   val last_vote_tally_time : ChainTimePoint,
    @SerialName("last_budget_time")       val last_budget_time     : ChainTimePoint,
    @SerialName("witness_budget")         val witness_budget       : ShareType,
    @SerialName("total_pob")              val total_pob            : ShareType,
    @SerialName("total_inactive")         val total_inactive       : ShareType,
    @SerialName("accounts_registered_this_interval")
    val accounts_registered_this_interval: UInt32, //  = 0
    /**
     *  Every time a block is missed this increases by
     *  RECENTLY_MISSED_COUNT_INCREMENT,
     *  every time a block is found it decreases by
     *  RECENTLY_MISSED_COUNT_DECREMENT.  It is
     *  never less than 0.
     */
    @SerialName("recently_missed_count")
    val recently_missed_count: UInt32, // = 0
    /**
     * The current absolute slot number.  Equal to the total
     * number of slots since genesis.  Also equal to the total
     * number of missed slots plus head_block_number.
     */
    @SerialName("current_aslot")
    val current_aslot: UInt64, // = 0
    /**
     * used to compute witness participation.
     */
    @SerialName("recent_slots_filled")
    val recent_slots_filled: UInt64, // fc::uint128_t TODO
    /**
     * dynamic_flags specifies chain state properties that can be
     * expressed in one bit.
     */
    @SerialName("dynamic_flags")
    val dynamic_flags: UInt32, // = 0
    @SerialName("last_irreversible_block_num")
    val last_irreversible_block_num: UInt32, // = 0


) : AbstractObject(), K201_DynamicGlobalPropertyType {

//    public:
//    static constexpr uint8_t space_id = implementation_ids;
//    static constexpr uint8_t type_id  = impl_dynamic_global_property_object_type;

    enum class DynamicFlagBits {
        /**
         * If maintenance_flag is set, then the head block is a
         * maintenance block.  This means
         * get_time_slot(1) - head_block_time() will have a gap
         * due to maintenance duration.
         *
         * This flag answers the question, "Was maintenance
         * performed in the last call to apply_block()?"
         */
        MAINTENANCE_FLAG //  = 0x01
    }

}