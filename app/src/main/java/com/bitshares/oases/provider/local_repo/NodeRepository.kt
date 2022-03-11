package com.bitshares.oases.provider.local_repo

import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import bitshareskit.chain.ChainConfig
import com.bitshares.oases.database.LocalDatabase
import com.bitshares.oases.database.entities.Node
import com.bitshares.oases.netowrk.java_websocket.NetworkService
import com.bitshares.oases.preference.old.Settings
import modulon.extensions.livedata.withDefault

object NodeRepository {

    private val nodeDao = LocalDatabase.INSTANCE.nodeDao()
    fun getLiveList() = nodeDao.getLiveList()
    fun getLiveNode(id: Int) = nodeDao.getLiveNode(id)

    suspend fun getNode(id: Int) = nodeDao.getNode(id)
    suspend fun getList() = nodeDao.getList()
    suspend fun add(node: Node) = nodeDao.addNode(node)
    suspend fun update(node: Node) = nodeDao.update(node.id, node.name, node.url, node.username, node.password)
    suspend fun updateLatency(node: Node) = nodeDao.updateLatency(node.id, node.latency, node.lastUpdate)
    suspend fun updateApis(node: Node) = nodeDao.updateApis(node.id, node.apis.toString())
    suspend fun updateChainInfo(node: Node) = nodeDao.updateChainInfo(node.id, node.chainId, node.coreSymbol)
    suspend fun remove(node: Node) = nodeDao.remove(node)
    suspend fun removeAll() = nodeDao.removeAll()

//    val currentNode = Settings.KEY_CURRENT_NODE_ID.switchMap { getLiveNode(it) }

    val currentNode = NetworkService.connectionLive.switchMap { getLiveNode(it.node.id) }
    val currentNodeChainId = currentNode.map { it.chainId }.distinctUntilChanged().withDefault { ChainConfig.Chain.CHAIN_ID_MAIN_NET }
    val currentSelectedNodeId = Settings.KEY_CURRENT_NODE_ID
    val currentNodeId = NetworkService.connectionLive.map { it.node.id }.distinctUntilChanged().withDefault { ChainConfig.EMPTY_INSTANCE.toInt() }



}