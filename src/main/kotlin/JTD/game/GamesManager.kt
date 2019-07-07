package JTD.game

import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.close
import io.ktor.websocket.WebSocketServerSession


object GamesManager {
    suspend fun createGame(): Int {
        val gameId = GameId.generate()
        ActiveGames.set(gameId, Game(gameId))
        return gameId
    }

    suspend fun getGameState(gameId: Int): GameState? {
        try {
            val game = ActiveGames.get(gameId)
            return game.getState()
        } catch (e: GameDoesNotExist) {
            return null
        }
    }

    suspend fun deleteGame(gameId: Int) {
        ActiveGames.remove(gameId)
        GameId.free(gameId)
    }

    suspend fun relayConnection(gameId: Int, conn: WebSocketServerSession) {
        try {
            val game = ActiveGames.get(gameId)
            game.includeConnection(conn)
        } catch (e: GameDoesNotExist) {
            conn.close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, e.message ?: ""))
        }
    }
}


suspend fun WebSocketServerSession.handleConnection() {
    val gameId = call.parameters["game_id"]
    if (gameId == null) {
        close()
    } else {
        GamesManager.relayConnection(gameId.toInt(), this)
    }
}