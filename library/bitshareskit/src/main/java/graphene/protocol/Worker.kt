package graphene.protocol

import graphene.serializers.StaticVarSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(with = WorkerInitializerSerializer::class)
sealed class WorkerInitializer

@Serializable
data class  RefundWorkerInitializer(
    @Transient val reserved: Unit = Unit
) : WorkerInitializer()

@Serializable
data class VestingBalanceWorkerInitializer(
    @SerialName("pay_vesting_period_days") val payVestingPeriodDays: uint16_t, // = 0
) : WorkerInitializer()

@Serializable
data class  BurnWorkerInitializer(
    @Transient val reserved: Unit = Unit
) : WorkerInitializer()


object WorkerInitializerSerializer : StaticVarSerializer<WorkerInitializer>(
    listOf(
        RefundWorkerInitializer::class,
        VestingBalanceWorkerInitializer::class,
        BurnWorkerInitializer::class,
    )
)

