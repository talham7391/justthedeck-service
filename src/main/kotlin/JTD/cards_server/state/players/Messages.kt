package JTD.cards_server.state.players

import io.ktor.websocket.WebSocketServerSession
import kotlinx.coroutines.CompletableDeferred


sealed class PlayersMessage

data class AddPlayerMessage(val name: String, val conn: WebSocketServerSession) : PlayersMessage()

data class GetPlayersMessage(val players: CompletableDeferred<Collection<Player>>) : PlayersMessage()

data class RemoveConnectionMessage(val conn: WebSocketServerSession) : PlayersMessage()