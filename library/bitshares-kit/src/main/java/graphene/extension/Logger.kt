package graphene.extension

import org.slf4j.LoggerFactory

fun Any?.info() = apply {
    kotlin.runCatching {
        LoggerFactory.getLogger("BitSharesKit Log").info(toString())
    }
}