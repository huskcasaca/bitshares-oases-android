package graphene.protocol

import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant

val DEFAULT_EXPIRATION_TIME: Instant = "1970-01-01T00:00:00Z".toInstant()
val MAXIMUM = "1969-12-31T23:59:59Z".toInstant()
