package JTD.cards_server.player

import io.ktor.websocket.WebSocketServerSession
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.actor


class DefaultPlayersManager {
    private val playersActor = GlobalScope.playersActor()

    suspend fun addPlayer(name: String, conn: WebSocketServerSession): Player {
        val player = CompletableDeferred<Player>()
        playersActor.send(AddPlayerMessage(name, conn, player))
        return player.await()
    }

    suspend fun removeConnection(conn: WebSocketServerSession) {
        playersActor.send(RemoveConnectionMessage(conn))
    }

    suspend fun getPlayers(): Collection<Player> {
        val players = CompletableDeferred<Collection<Player>>()
        playersActor.send(GetPlayersMessage(players))
        return players.await()
    }

    suspend fun getPlayer(conn: WebSocketServerSession): Player {
        val player = CompletableDeferred<Player>()
        playersActor.send(GetPlayerMessage(player, conn))
        return player.await()
    }
}


fun CoroutineScope.playersActor() = actor<PlayersMessage> {
    val players = mutableMapOf<String, Player>()

    for (mssg in channel) {
        when (mssg) {

            is AddPlayerMessage -> {
                if (mssg.name in players) {
                    players[mssg.name]?.conn = mssg.conn
                } else {
                    players[mssg.name] = newPlayer(mssg.name, players.size, mssg.conn)
                }
                mssg.player.complete(players[mssg.name]!!)
            }

            is GetPlayersMessage -> { mssg.players.complete(players.values) }

            is GetPlayerMessage -> {
                val player = players whoIsUsing mssg.conn
                if (player == null) {
                    mssg.player.completeExceptionally(PlayerNotFound())
                } else {
                    mssg.player.complete(player)
                }
            }

            is RemoveConnectionMessage -> {
                val player = players whoIsUsing mssg.conn
                player?.conn = null
            }
        }
    }
}


infix fun Map<String, Player>.whoIsUsing(conn: WebSocketServerSession): Player? {
    for ((_, player) in this) {
        if (player.conn == conn) {
            return player
        }
    }
    return null
}
