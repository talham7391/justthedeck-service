package JTD.cards_server

import JTD.cards_server.state.players.CardOnTable
import JTD.cards_server.state.players.DefaultPlayersManager
import JTD.cards_server.state.players.Player
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.http.cio.websocket.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.actor


val objectMapper = ObjectMapper().registerModule(KotlinModule())


fun CoroutineScope.cardGameActor(playersManager: DefaultPlayersManager) = actor<CardGameMessage> {

    val cardsOnTable = mutableListOf<CardOnTable>()

    for (mssg in channel) {
        when (mssg) {
            is SharePlayers -> {
                val players = playersManager.getPlayers()
                players.send(PlayersServerAction(players))
            }

            is PutCardsOnTable -> {
                cardsOnTable.addAll(mssg.cards)
                playersManager.sendToPlayers(CardsOnTableServerAction(mssg.cards))
            }

            is PlayerPutCardsOnTable -> {
                cardsOnTable.addAll(mssg.cards)
                playersManager.sendToPlayers(PlayerCardsOnTableServerAction(mssg.player.name, mssg.cards))
            }

            is ShareCardsOnTable -> {
                playersManager.sendToPlayers(CardsOnTableServerAction(cardsOnTable))
            }
        }
    }
}

suspend fun Collection<Player>.send(action: ServerAction) {
    forEach {
        it.conn?.send(objectMapper.writeValueAsBytes(action))
    }
}

suspend fun DefaultPlayersManager.sendToPlayers(action: ServerAction) {
    val players = getPlayers()
    players.send(action)
}