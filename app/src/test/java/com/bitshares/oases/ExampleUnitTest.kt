package com.bitshares.oases

import com.bitshares.oases.database.entities.BitsharesNode
import com.bitshares.oases.netowrk.socket.WebsocketManager
import com.bitshares.oases.netowrk.socket.WebsocketManagerInternal
import graphene.extension.info
import graphene.protocol.AccountId
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @Test
    fun test() = runBlocking {

        with(WebsocketManagerInternal()) {
            switch(
                BitsharesNode(url = "wss://api.btslebin.com/ws")
            )
            val list = mutableListOf<Int>()
            val a = GlobalScope.launch {
                (10000..10100).map {
                    "$it".info()
//                    withTimeoutOrNull(5000) {
                        getObject(AccountId(it.toULong()))?.run {
                            list.add(it)
                        }
//                    }
                }
            }
            delay(3.seconds)
            switch(
                BitsharesNode(url = "wss://ws.gdex.top/")
            )

//            delay(3.seconds)
//            startClient(lastClient)

            a.join()
            list.size.info()
        }


        Unit
    }

    @Test
    fun test1() = runBlocking {

        launch(SupervisorJob()) {
            val a = launch {
                delay(1000)
                throw Error("T")
            }

            try {
                a.cancel()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }


        delay(10000)
        Unit
    }

    @Test
    fun test2() = runBlocking {

        null.toString().info()


        Unit

    }

}
