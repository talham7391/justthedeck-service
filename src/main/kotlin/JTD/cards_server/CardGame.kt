package JTD.cards_server

import JTD.cards_server.state.players.DefaultPlayersManager
import JTD.infrastructure.BaseGame
import JTD.infrastructure.info
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.features.callId
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readBytes
import io.ktor.websocket.WebSocketServerSession
import kotlinx.coroutines.GlobalScope
import org.slf4j.LoggerFactory


class CardGame(override val id: Int) : BaseGame<Int>() {
    private val logger = LoggerFactory.getLogger(CardGame::class.java)
    private val objectMapper = ObjectMapper().registerModule(KotlinModule())
    private val playersManager = DefaultPlayersManager()
    private val cardGameActor = GlobalScope.cardGameActor(playersManager)

    override suspend fun WebSocketServerSession.setup() {
        val frame = incoming.receive()
        val action = objectMapper.readValue<ClientAction>(frame.readBytes())

        if (action.name == "SET_NAME") {
            val setNameAction = objectMapper.readValue<SetNameClientAction>(frame.readBytes())
            val name = setNameAction.data.name
            playersManager.addPlayer(name, this)

            cardGameActor.send(SharePlayers)
            logger.info(id, call.callId, "Player Joined: $name.")

        } else {
            throw UnexpectedResponse(action.name, "SET_NAME")
        }
    }

    override suspend fun WebSocketServerSession.handleActionFromClient(frame: Frame) {
    }

    override suspend fun WebSocketServerSession.teardown() {
        playersManager.removeConnection(this)

        cardGameActor.send(SharePlayers)
        logger.info(id, call.callId, "Player disconnected.")
    }
}
