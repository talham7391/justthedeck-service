package JTD.game

import io.ktor.websocket.WebSocketServerSession


class Game(val gameId: Int) {

    fun getState(): GameState {
        return GameState(gameId)
    }

    suspend fun WebSocketServerSession.HandlePlayer() {
        for (frame in incoming) {
        }
    }

    suspend fun includeConnection(conn: WebSocketServerSession) = conn.HandlePlayer()
}

data class GameState(val gameId: Int)
