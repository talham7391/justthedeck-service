package JTD.game

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.actor


private sealed class ActiveGamesMessage

private data class GetGameFromId(
        val gameId: Int,
        val response: CompletableDeferred<Game>
) : ActiveGamesMessage()

private data class SetGameForId(
        val gameId: Int,
        val game: Game
) : ActiveGamesMessage()

private data class RemoveGame(
        val gameId: Int
) : ActiveGamesMessage()


private fun CoroutineScope.activeGamesActor() = actor<ActiveGamesMessage> {
    val activeGames = mutableMapOf<Int, Game>()

    for (mssg in channel) {
        when (mssg) {
            is GetGameFromId -> {
                val game = activeGames[mssg.gameId]
                if (game == null) {
                    mssg.response.completeExceptionally(GameDoesNotExist(mssg.gameId))
                } else {
                    mssg.response.complete(game)
                }
            }
            is SetGameForId -> { activeGames[mssg.gameId] = mssg.game }
            is RemoveGame -> activeGames.remove(mssg.gameId)
        }
    }
}


object ActiveGames {
    private val activeGamesActor = GlobalScope.activeGamesActor()

    suspend fun get(gameId: Int): Game {
        val game = CompletableDeferred<Game>()
        activeGamesActor.send(GetGameFromId(gameId, game))
        return game.await()
    }

    suspend fun set(gameId: Int, game: Game) {
        activeGamesActor.send(SetGameForId(gameId, game))
    }

    suspend fun remove(gameId: Int) {
        activeGamesActor.send(RemoveGame(gameId))
    }
}
