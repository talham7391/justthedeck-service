package JTD.game

import io.ktor.features.callId
import io.ktor.websocket.WebSocketServerSession
import org.slf4j.LoggerFactory


class Game(val gameId: Int) {
    private val logger = LoggerFactory.getLogger(Game::class.java)

    fun getState(): GameState {
        return GameState(gameId)
    }

    suspend fun WebSocketServerSession.HandlePlayer() {
        logger.debug(gameId, call.callId, "Attempting join")

        for (frame in incoming) {
        }

        logger.debug(gameId, call.callId, "Disconnected")
    }

    suspend fun includeConnection(conn: WebSocketServerSession) = conn.HandlePlayer()
}

data class GameState(val gameId: Int)
