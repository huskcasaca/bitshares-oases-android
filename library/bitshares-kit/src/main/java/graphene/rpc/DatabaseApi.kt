package graphene.rpc

import graphene.app.DatabaseAPI
import graphene.chain.AbstractObject
import graphene.protocol.ObjectId

class DatabaseClientAPI(private val client: AbstractClient) {


    suspend fun getObjects(ids: List<ObjectId>): List<AbstractObject?> {
        return client.sendForResult(DatabaseAPI.GET_OBJECTS, ids) ?: emptyList()
    }
    suspend fun getObjects(vararg ids: ObjectId): List<AbstractObject?> {
        return client.sendForResult(DatabaseAPI.GET_OBJECTS, ids.toList()) ?: emptyList()
    }
    suspend fun getObject(id: ObjectId): AbstractObject? {
        return getObjects(id).firstOrNull()
    }




    suspend fun getObjects(ids: List<ObjectId>, subscribe: Boolean): List<AbstractObject?> {
        return client.sendForResult(DatabaseAPI.GET_OBJECTS, ids) ?: emptyList()
    }


}