package JTD.cards_server

import JTD.cards_server.card.*
import JTD.cards_server.player.DefaultPlayersManager
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory


class CardGame(override val id: Int) : BaseGame<Int>() {
    private val logger = LoggerFactory.getLogger(CardGame::class.java)
    private val objectMapper = ObjectMapper().registerModule(KotlinModule())

    private val playersManager = DefaultPlayersManager()
    private val cardGameActor = GlobalScope.cardGameActor(playersManager)

    private val cards = fullNormalDeck().toMutableList()

    override suspend fun WebSocketServerSession.setup() {
        val frame = incoming.receive()
        val action = objectMapper.readValue<ClientAction>(frame.readBytes())

        if (action.name == "SET_NAME") {
            val setNameAction = objectMapper.readValue<SetNameClientAction>(frame.readBytes())
            val name = setNameAction.data.name
            val player = playersManager.addPlayer(name, this)

            player.addCardsToHand(cards.randomlyTake(5))

            cardGameActor.send(SharePlayers)
            cardGameActor.send(ShareCardsOnTable)
            logger.info(id, call.callId, "Player Joined: $name.")

        } else {
            throw UnexpectedResponse(action.name, "SET_NAME")
        }
    }

    override suspend fun WebSocketServerSession.handleActionFromClient(frame: Frame) {
        val action = objectMapper.readValue<ClientAction>(frame.readBytes())

        when(action.name) {
            "PUT_CARDS_ON_TABLE" -> {
                val putCardOnTableAction = objectMapper.readValue<PutCardsOnTableClientAction>(frame.readBytes())
                cardGameActor.send(PutCardsOnTable(putCardOnTableAction.data.cards))
            }

            "ADD_CARDS_TO_HAND" -> {
                val addCardsToHandAction = objectMapper.readValue<AddCardsToHandClientAction>(frame.readBytes())
                val player = playersManager.getPlayer(this)
                player.addCardsToHand(addCardsToHandAction.data.cards)
            }

            "REMOVE_CARDS_FROM_HAND" -> {
                val removeCardsFromHandAction = objectMapper.readValue<RemoveCardsFromHandClientAction>(frame.readBytes())
                val player = playersManager.getPlayer(this)
                player.removeCardsFromHand(removeCardsFromHandAction.data.cards)
            }

            "REMOVE_CARDS_FROM_TABLE" -> {
                val removeCardsFromTableAction = objectMapper.readValue<RemoveCardsFromTableClientAction>(frame.readBytes())
                cardGameActor.send(RemoveCardsOnTable(removeCardsFromTableAction.data.cards))
            }
        }

    }

    override suspend fun WebSocketServerSession.teardown() {
        playersManager.removeConnection(this)

        cardGameActor.send(SharePlayers)
        logger.info(id, call.callId, "Player disconnected.")
    }
}
