package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K201_DynamicGlobalPropertyObject(
    @SerialName("id")
    override val id: K201_DynamicGlobalPropertyIdType,
    @SerialName("head_block_number")      val headBlockNumber     : uint32_t, //  = 0
    @SerialName("head_block_id")          val headBlockId         : BlockIdType,
    @SerialName("time")                   val time                : ChainTimePoint,
    @SerialName("current_witness")        val currentWitness      : K106_WitnessType,
    @SerialName("next_maintenance_time")  val nextMaintenanceTime : ChainTimePoint,
    @SerialName("last_vote_tally_time")   val lastVoteTallyTime   : ChainTimePoint,
    @SerialName("last_budget_time")       val lastBudgetTime      : ChainTimePoint,
    @SerialName("witness_budget")         val witnessBudget       : share_type,
    @SerialName("total_pob")              val totalPob            : share_type,
    @SerialName("total_inactive")         val totalInactive       : share_type,
    @SerialName("accounts_registered_this_interval")
    val accountsRegisteredThisInterval: uint32_t, //  = 0
    /**
     *  Every time a block is missed this increases by
     *  RECENTLY_MISSED_COUNT_INCREMENT,
     *  every time a block is found it decreases by
     *  RECENTLY_MISSED_COUNT_DECREMENT.  It is
     *  never less than 0.
     */
    @SerialName("recently_missed_count")
    val recentlyMissedCount: uint32_t, // = 0
    /**
     * The current absolute slot number.  Equal to the total
     * number of slots since genesis.  Also equal to the total
     * number of missed slots plus head_block_number.
     */
    @SerialName("current_aslot")
    val currentAslot: uint64_t, // = 0
    /**
     * used to compute witness participation.
     */
    @SerialName("recent_slots_filled")
    val recentSlotsFilled: UInt128, // fc::uint128_t TODO
    /**
     * dynamic_flags specifies chain state properties that can be
     * expressed in one bit.
     */
    @SerialName("dynamic_flags")
    val dynamicFlags: uint32_t, // = 0
    @SerialName("last_irreversible_block_num")
    val lastIrreversibleBlockNum: uint32_t, // = 0


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