package graphene.rpc

import graphene.app.API
import graphene.app.DatabaseAPI
import graphene.chain.AbstractObject
import graphene.protocol.AbstractIdType
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.decodeFromJsonElement

class DatabaseClientAPI(private val client: AbstractClient) {


    suspend fun getObjects(ids: List<AbstractIdType>): List<AbstractObject?> {
        return client.sendForResult(DatabaseAPI.GET_OBJECTS, ids)
    }
    suspend fun getObjects(vararg ids: AbstractIdType): List<AbstractObject?> {
        return client.sendForResult(DatabaseAPI.GET_OBJECTS, ids.toList())
    }

    suspend fun getObjects(ids: List<AbstractIdType>, subscribe: Boolean): List<AbstractObject?> {
        return client.sendForResult(DatabaseAPI.GET_OBJECTS, ids)
    }


}