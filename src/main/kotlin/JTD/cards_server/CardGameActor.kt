package JTD.cards_server

import JTD.cards_server.state.players.DefaultPlayersManager
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.http.cio.websocket.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.actor


sealed class CardGameMessage

object SharePlayers : CardGameMessage()


fun CoroutineScope.cardGameActor(playersManager: DefaultPlayersManager) = actor<CardGameMessage> {
    val objectMapper = ObjectMapper().registerModule(KotlinModule())

    for (mssg in channel) {
        when (mssg) {
            is SharePlayers -> {
                val players = playersManager.getPlayers()
                players.forEach {
                    it.conn?.send(objectMapper.writeValueAsBytes(PlayersServerAction(players)))
                }
            }
        }
    }
}
