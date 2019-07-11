package JTD.game

import JTD.game.state.CardOnTable
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.close
import io.ktor.websocket.WebSocketServerSession
import org.slf4j.LoggerFactory


object GamesManager {
    private val logger = LoggerFactory.getLogger(GamesManager.javaClass)

    suspend fun createGame(): Int {
        val gameId = GameId.generate()
        ActiveGames.set(gameId, Game(gameId))
        logger.info(gameId, "Created")
        return gameId
    }

    suspend fun getCardsOnTable(gameId: Int): List<CardOnTable>? {
        try {
            val game = ActiveGames.get(gameId)
            return game.getCardsOnTable()
        } catch (e: GameDoesNotExist) {
            return null
        }
    }

    suspend fun deleteGame(gameId: Int) {
        ActiveGames.remove(gameId)
        GameId.free(gameId)
        logger.info(gameId, "Deleted")
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