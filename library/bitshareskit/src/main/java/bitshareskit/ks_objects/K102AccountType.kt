package bitshareskit.ks_objects

import bitshareskit.ks_models.KAuthority
import bitshareskit.ks_models.KGrapheneDateTime
import bitshareskit.ks_models.KOptions
import bitshareskit.ks_object_base.*

interface K102AccountType: K000AbstractType {
    override val id: K102AccountId                  get() = this as K102AccountId
    val membershipExpirationDate: KGrapheneDateTime get() = KGrapheneDateTime.STANDARD_EXPIRATION_DATE_TIME
    val registrar: K102AccountType                  get() = emptyIdType()
    val referrer: K102AccountType                   get() = emptyIdType()
    val lifetimeReferrer: K102AccountType           get() = emptyIdType()
    val networkFeePercentage: UInt                  get() = 0U
    val lifetimeReferrerFeePercentage: UInt         get() = 0U
    val referrerRewardsFeePercentage: UInt          get() = 0U
    val name: String                                get() = emptyString()
    val owner: KAuthority                           get() = emptyComponent()
    val active: KAuthority                          get() = emptyComponent()
    val options: KOptions                           get() = emptyComponent()
    val whiteListingAccounts: Set<K102AccountType>  get() = emptySet()
    val blackListingAccounts: Set<K102AccountType>  get() = emptySet()
    val whiteListedAccounts: Set<K102AccountType>   get() = emptySet()
    val blackListedAccounts: Set<K102AccountType>   get() = emptySet()
}