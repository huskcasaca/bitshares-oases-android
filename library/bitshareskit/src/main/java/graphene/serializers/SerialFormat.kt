package graphene.serializers

import kotlinx.serialization.json.*

var GRAPHENE_JSON_PLATFORM_SERIALIZER = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}
