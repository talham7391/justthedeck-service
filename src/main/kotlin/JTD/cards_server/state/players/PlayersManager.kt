package JTD.cards_server.state.players

import com.fasterxml.jackson.annotation.JsonIgnore
import io.ktor.websocket.WebSocketServerSession
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.actor


interface Player {
    val name: String
    val cardsInHand: Collection<Card>
    val joinOrder: Int

    var conn: WebSocketServerSession?
    val connected
        get() = conn != null
}


class PlayerImpl(
        override val name: String,
        override val joinOrder: Int,
        override val cardsInHand: Collection<Card>,
        @JsonIgnore override var conn: WebSocketServerSession?) : Player


fun newPlayer(
        name: String,
        joinOrder: Int,
        conn: WebSocketServerSession
): Player = PlayerImpl(name, joinOrder, emptyList(), conn)


fun Player?.revive(newConn: WebSocketServerSession): Player? {
    if (this == null) {
        return null
    }
    return PlayerImpl(name, joinOrder, cardsInHand, newConn)
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
                val joinOrder = players.size
                players[mssg.name] =
                        players[mssg.name].revive(mssg.conn) ?: newPlayer(mssg.name, joinOrder, mssg.conn)
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
