package JTD.game

import JTD.game.state.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.features.callId
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readBytes
import io.ktor.http.cio.websocket.send
import io.ktor.websocket.WebSocketServerSession
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.selects.select
import org.slf4j.LoggerFactory


class Game(val gameId: Int) {
    private val logger = LoggerFactory.getLogger(Game::class.java)
    private val state = GlobalScope.gameStateActor()
    private val objectMapper = ObjectMapper().registerModule(KotlinModule())

    suspend fun getCardsOnTable(): List<CardOnTable> {
        val cardsOnTable = CompletableDeferred<List<CardOnTable>>()
        state.send(GetCardsOnTable(cardsOnTable))
        return cardsOnTable.await()
    }

    suspend fun WebSocketServerSession.HandlePlayer() {
        logger.debug(gameId, call.callId, "Attempting join")

        val nameMessage = objectMapper.readValue<NameMessage>(incoming.receive().readBytes())
        val name = nameMessage.data.name

        val stateUpdates = CompletableDeferred<ReceiveChannel<ServerMessage>>()
        state.send(PlayerConnected(name, stateUpdates))

        val updates = stateUpdates.await()

        while (true) {
            try {
                select<Unit> {
                    incoming.onReceive { processMessageFromPlayer(it.readBytes()) }

                    updates.onReceive {
                        when (it) {
                            is PrintMessage -> {
                                logger.debug(gameId, call.callId, it.message)
                            }

                            is PlayerStatesUpdate -> {
                                send(objectMapper.writeValueAsBytes(it))
                            }
                        }
                    }
                }
            } catch (e: ClosedReceiveChannelException) {
                break
            }
        }

        state.send(PlayerDisconneced(name))

        logger.debug(gameId, call.callId, "Disconnected")
    }

    private fun processMessageFromPlayer(mssgBytes: ByteArray) {
        val mssg = objectMapper.readValue<PlayerMessage>(mssgBytes)

        when (mssg.action) {
            "SET_NAME" -> { }
            else -> { }
        }
    }

    suspend fun includeConnection(conn: WebSocketServerSession) = conn.HandlePlayer()
}

open class PlayerMessage(open val action: String, open val data: Any)

data class NameMessage(override val action: String, override val data: NameData) : PlayerMessage(action, data)
data class NameData(val name: String)
