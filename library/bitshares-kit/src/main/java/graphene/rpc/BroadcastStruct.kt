package graphene.rpc

import graphene.app.API
import kotlinx.serialization.json.JsonArray
import kotlin.coroutines.Continuation


data class BroadcastStruct(val method: API, val subscribe: Boolean, val params: JsonArray, val cont: Continuation<SocketResult>)
