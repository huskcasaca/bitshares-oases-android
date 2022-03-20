package graphene.rpc

import graphene.app.API
import graphene.protocol.GRAPHENE_JSON_PLATFORM_SERIALIZER
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.serialization.json.*
import kotlin.coroutines.suspendCoroutine

class MultiClient : AbstractClient() {

    private val channel: Channel<BroadcastStruct> = Channel(UNLIMITED)

    val clients = mutableListOf<GrapheneClient>()

    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun switch(node: Node) {
        clients.forEach {
            it.stop()
        }
        clients.clear()

        val client = GrapheneClient(node)
        client.start()
        clients.add(client)

        scope.launch { // sendingJob
            while (isActive) {
                val struct = channel.receive()
                try {
                    client.broadcast(struct)
                } catch (e: Exception) {
                    channel.send(struct)
                    break
                }
            }
        }
        scope.launch { // collectingJob
            while (isActive) {
                try {
                    val struct = client.fallbackChannel.receive()
                    if (struct.cont.context.isActive) {
                        channel.send(struct)
                    }
                } catch (e: Exception) {
                    break
                }
            }
//            throw Exception()
        }

    }

    fun stop() {
        clients.forEach {
            it.stop()
        }

        clients.clear()
    }

    override suspend fun broadcast(struct: BroadcastStruct) {
        channel.send(struct)
    }




}