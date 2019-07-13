package JTD.infrastructure

import JTD.infrastructure.activegames.DefaultActiveGamesManager
import JTD.infrastructure.id.DefaultIdManager
import io.ktor.websocket.WebSocketServerSession


interface GameManager<T> {
    suspend fun createGame(id: T): Game<T>
    suspend fun newGame(): Game<T>
    suspend fun deleteGame(id: T)
    suspend fun getGame(id: T): Game<T>
    suspend fun getGame(id: String): Game<T>

    suspend fun handleConnection(id: String, conn: WebSocketServerSession)

    fun newId(i: Int): T
    fun String.getSupportedFormat(): T
}


abstract class BaseGameManager<T>
    : GameManager<T> {

    private val idManager = DefaultIdManager<T> { repeat(20) { add(newId(it)) } }
    private val activeGamesManager = DefaultActiveGamesManager<T>()

    override suspend fun newGame(): Game<T> {
        val id = idManager.generateId()
        val game = createGame(id)
        activeGamesManager.set(id, game)
        return game
    }

    override suspend fun deleteGame(id: T) {
        activeGamesManager.remove(id)
        idManager.freeId(id)
    }

    override suspend fun getGame(id: T): Game<T> {
        return activeGamesManager.get(id)
    }

    override suspend fun getGame(id: String) = getGame(id.getSupportedFormat())

    override suspend fun handleConnection(id: String, conn: WebSocketServerSession) {
        val game = getGame(id.getSupportedFormat())
        game.includeConnection(conn)
    }
}
