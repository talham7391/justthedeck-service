package JTD.infrastructure.activegames

import JTD.infrastructure.Game
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.actor


interface ActiveGamesManager<T> {
    suspend fun set(id: T, game: Game<T>)
    suspend fun get(id: T): Game<T>
    suspend fun remove(id: T)
}


class DefaultActiveGamesManager<T> : ActiveGamesManager<T> {
    private val activeGamesActor = GlobalScope.activeGamesActor<T>()

    override suspend fun get(id: T): Game<T> {
        val game = CompletableDeferred<Game<T>>()
        activeGamesActor.send(GetGameFromId(id, game))
        return game.await()
    }

    override suspend fun set(id: T, game: Game<T>) {
        activeGamesActor.send(SetGameForId(id, game))
    }

    override suspend fun remove(id: T) {
        activeGamesActor.send(RemoveGame(id))
    }

}


fun <T> CoroutineScope.activeGamesActor() = actor<ActiveGamesMessage<T>> {
    val activeGames = mutableMapOf<T, Game<T>>()

    for (mssg in channel) {
        when (mssg) {

            is GetGameFromId -> {
                val game = activeGames[mssg.id]
                if (game == null) {
                    mssg.response.completeExceptionally(GameDoesNotExist(mssg.id))
                } else {
                    mssg.response.complete(game)
                }
            }

            is SetGameForId -> { activeGames[mssg.id] = mssg.game }

            is RemoveGame -> activeGames.remove(mssg.id)
        }
    }
}