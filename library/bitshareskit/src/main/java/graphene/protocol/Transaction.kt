package graphene.protocol

import kotlinx.serialization.Serializable


@Serializable
open class Transaction {


//    public:
//    virtual ~transaction() = default;
//    /**
//     * Least significant 16 bits from the reference block number.
//     */
//    uint16_t           ref_block_num    = 0;
//    /**
//     * The first non-block-number 32-bits of the reference block ID. Recall that block IDs have 32 bits of block
//     * number followed by the actual block hash, so this field should be set using the second 32 bits in the
//     * @ref block_id_type
//     */
//    uint32_t           ref_block_prefix = 0;
//
//    /**
//     * This field specifies the absolute expiration for this transaction.
//     */
//    fc::time_point_sec expiration;
//
//    vector<operation>  operations;
//    extensions_type    extensions;
//
//    /// Calculate the digest for a transaction
//    digest_type                        digest()const;
//    virtual const transaction_id_type& id()const;
//    virtual void                       validate() const;
//
//    void set_expiration( fc::time_point_sec expiration_time );
//    void set_reference_block( const block_id_type& reference_block );
//
//    /// visit all operations
//    template<typename Visitor>
//    vector<typename Visitor::result_type> visit( Visitor&& visitor )
//    {
//        vector<typename Visitor::result_type> results;
//        for( auto& op : operations )
//        results.push_back(op.visit( std::forward<Visitor>( visitor ) ));
//        return results;
//    }
//    template<typename Visitor>
//    vector<typename Visitor::result_type> visit( Visitor&& visitor )const
//{
//    vector<typename Visitor::result_type> results;
//    for( auto& op : operations )
//    results.push_back(op.visit( std::forward<Visitor>( visitor ) ));
//    return results;
//}
//
//    void get_required_authorities( flat_set<account_id_type>& active,
//    flat_set<account_id_type>& owner,
//    vector<authority>& other,
//    bool ignore_custom_operation_required_auths )const;
//
//    virtual uint64_t get_packed_size()const;
//
//    protected:
//    // Calculate the digest used for signature validation
//    digest_type sig_digest( const chain_id_type& chain_id )const;
//    mutable transaction_id_type _tx_id_buffer;
}






//class signed_transaction : public transaction
//{
//    public:
//    signed_transaction( const transaction& trx = transaction() )
//    : transaction(trx){}
//    virtual ~signed_transaction() = default;
//
//    /** signs and appends to signatures */
//    const signature_type& sign( const private_key_type& key, const chain_id_type& chain_id );
//
//    /** returns signature but does not append */
//    signature_type sign( const private_key_type& key, const chain_id_type& chain_id )const;
//
//    /**
//     *  The purpose of this method is to identify some subset of
//     *  @p available_keys that will produce sufficient signatures
//     *  for a transaction.  The result is not always a minimal set of
//     *  signatures, but any non-minimal result will still pass
//     *  validation.
//     */
//    set<public_key_type> get_required_signatures(
//            const chain_id_type& chain_id,
//    const flat_set<public_key_type>& available_keys,
//    const std::function<const authority*(account_id_type)>& get_active,
//    const std::function<const authority*(account_id_type)>& get_owner,
//    bool allow_non_immediate_owner,
//    bool ignore_custom_operation_required_authorities,
//    uint32_t max_recursion = GRAPHENE_MAX_SIG_CHECK_DEPTH )const;
//
//    /**
//     * Checks whether signatures in this signed transaction are sufficient to authorize the transaction.
//     *   Throws an exception when failed.
//     *
//     * @param chain_id the ID of a block chain
//     * @param get_active callback function to retrieve active authorities of a given account
//     * @param get_owner  callback function to retrieve owner authorities of a given account
//     * @param get_custom callback function to retrieve viable custom authorities for a given account and operation
//     * @param allow_non_immediate_owner whether to allow owner authority of non-immediately
//     *            required accounts to authorize operations in the transaction
//     * @param ignore_custom_operation_required_auths See issue #210; whether to ignore the
//     *            required_auths field of custom_operation or not
//     * @param max_recursion maximum level of recursion when verifying, since an account
//     *            can have another account in active authorities and/or owner authorities
//     */
//    void verify_authority(
//            const chain_id_type& chain_id,
//    const std::function<const authority*(account_id_type)>& get_active,
//    const std::function<const authority*(account_id_type)>& get_owner,
//    const custom_authority_lookup& get_custom,
//    bool allow_non_immediate_owner,
//    bool ignore_custom_operation_required_auths,
//    uint32_t max_recursion = GRAPHENE_MAX_SIG_CHECK_DEPTH )const;
//
//    /**
//     * This is a slower replacement for get_required_signatures()
//     * which returns a minimal set in all cases, including
//     * some cases where get_required_signatures() returns a
//     * non-minimal set.
//     */
//    set<public_key_type> minimize_required_signatures(
//            const chain_id_type& chain_id,
//    const flat_set<public_key_type>& available_keys,
//    const std::function<const authority*(account_id_type)>& get_active,
//    const std::function<const authority*(account_id_type)>& get_owner,
//    const custom_authority_lookup& get_custom,
//    bool allow_non_immediate_owner,
//    bool ignore_custom_operation_required_auths,
//    uint32_t max_recursion = GRAPHENE_MAX_SIG_CHECK_DEPTH) const;
//
//    /**
//     * @brief Extract public keys from signatures with given chain ID.
//     * @param chain_id A chain ID
//     * @return Public keys
//     * @note If @ref _signees is empty, E.G. when it's the first time calling
//     *       this function for the signed transaction, public keys will be
//     *       extracted with given chain ID, and be stored into the mutable
//     *       @ref _signees field, then @ref _signees will be returned;
//     *       otherwise, the @p chain_id parameter will be ignored, and
//     *       @ref _signees will be returned directly.
//     */
//    virtual const flat_set<public_key_type>& get_signature_keys( const chain_id_type& chain_id )const;
//
//    /** Signatures */
//    vector<signature_type> signatures;
//
//    /** Removes all operations and signatures */
//    void clear() { operations.clear(); signatures.clear(); }
//
//    /** Removes all signatures */
//    void clear_signatures() { signatures.clear(); }
//    protected:
//    /** Public keys extracted from signatures */
//    mutable flat_set<public_key_type> _signees;
//};