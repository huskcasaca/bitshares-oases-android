package graphene.protocol

interface K000AbstractType {
    val id: K000AbstractId                          get() = this as K000AbstractId
}

interface K100NullType: K000AbstractType {
    override val id: K100NullId                     get() = this as K100NullId
}

interface K101BaseType: K000AbstractType {
    override val id: K101BaseId                     get() = this as K101BaseId
}

interface K102AccountType: K000AbstractType {
    override val id: K102AccountId                  get() = this as K102AccountId
    val membershipExpirationDate: ChainTimePoint    get() = ChainTimePoint.STANDARD_EXPIRATION_DATE_TIME
    val registrar: K102AccountType                  get() = emptyIdType()
    val referrer: K102AccountType                   get() = emptyIdType()
    val lifetimeReferrer: K102AccountType           get() = emptyIdType()
    val networkFeePercentage: UInt                  get() = 0U
    val lifetimeReferrerFeePercentage: UInt         get() = 0U
    val referrerRewardsFeePercentage: UInt          get() = 0U
    val name: String                                get() = emptyString()
    val owner: Authority                            get() = emptyComponent()
    val active: Authority                           get() = emptyComponent()
    val options: AccountOptions                     get() = emptyComponent()
    val whiteListingAccounts: Set<K102AccountType>  get() = emptySet()
    val blackListingAccounts: Set<K102AccountType>  get() = emptySet()
    val whiteListedAccounts: Set<K102AccountType>   get() = emptySet()
    val blackListedAccounts: Set<K102AccountType>   get() = emptySet()
}

interface K103AssetType: K000AbstractType {
    override val id: K103AssetId                    get() = this as K103AssetId
    val symbol: String                              get() = emptyString()
    val issuerId: K102AccountType                   get() = emptyIdType()
    val precision: UByte                            get() = 0U
    val options: AssetOptions                       get() = emptyComponent()
    val dynamicDataId: K203AssetDynamicType         get() = emptyIdType()
//    var bitassetDataId: KAssetBitassetIdType = emptyIdType(),
}

interface K203AssetDynamicType: K000AbstractType {
    override val id: K203AssetDynamicId             get() = this as K203AssetDynamicId
    val currentSupply: UInt64                       get() = 0U
    val confidentialSupply: UInt64                  get() = 0U
    val accumulatedFees: UInt64                     get() = 0U
    val accumulatedCollateralFees: UInt64           get() = 0U
    val feePool: UInt64                             get() = 0U
}

interface K204AssetBitassetType: K000AbstractType {
    override val id: K204AssetBitassetId            get() = this as K204AssetBitassetId
    val currentSupply: UInt64                       get() = 0U
    val confidentialSupply: UInt64                  get() = 0U
    val accumulatedFees: UInt64                     get() = 0U
    val accumulatedCollateralFees: UInt64           get() = 0U
    val feePool: UInt64                             get() = 0U
}