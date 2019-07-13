package JTD.cards_server.state.players

import com.fasterxml.jackson.annotation.JsonIgnore
import io.ktor.websocket.WebSocketServerSession
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.actor


interface Player {
    val name: String

    var conn: WebSocketServerSession?
    val connected
        get() = conn != null
}


class PlayerImpl(
        override val name: String,
        @JsonIgnore override var conn: WebSocketServerSession?) : Player

fun newPlayer(name: String, conn: WebSocketServerSession) = PlayerImpl(name, conn)

fun Player?.revive(newConn: WebSocketServerSession): Player? {
    if (this == null) {
        return null
    }
    return PlayerImpl(name, newConn)
}


class DefaultPlayersManager {
    private val playersActor = GlobalScope.playersActor()

    suspend fun addPlayer(name: String, conn: WebSocketServerSession) {
        playersActor.send(AddPlayerMessage(name, conn))
    }

    suspend fun removeConnection(conn: WebSocketServerSession) {
        playersActor.send(RemoveConnectionMessage(conn))
    }

    suspend fun getPlayers(): Collection<Player> {
        val players = CompletableDeferred<Collection<Player>>()
        playersActor.send(GetPlayersMessage(players))
        return players.await()
    }
}


fun CoroutineScope.playersActor() = actor<PlayersMessage> {
    val players = mutableMapOf<String, Player>()

    for (mssg in channel) {
        when (mssg) {

            is AddPlayerMessage -> {
                players[mssg.name] = players[mssg.name].revive(mssg.conn) ?: newPlayer(mssg.name, mssg.conn)
            }

            is GetPlayersMessage -> { mssg.players.complete(players.values) }

            is RemoveConnectionMessage -> {
                for ((_, player) in players) {
                    if (player.conn == mssg.conn) {
                        player.conn = null
                        break
                    }
                }
            }
        }
    }
}