package JTD.game

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.actor
import java.util.*
import kotlin.math.pow


val NUM_GAME_ID_DIGITS = 1


private sealed class GameIdMessage

private data class GetGameId(val response: CompletableDeferred<Int>) : GameIdMessage()

private data class FreeGameId(val gameId: Int) : GameIdMessage()


private fun CoroutineScope.gameIdActor() = actor<GameIdMessage> {
    val availableIds = LinkedList<Int>()
    repeat((10f.pow(NUM_GAME_ID_DIGITS)).toInt()) {
        availableIds.add(it)
    }

    for (mssg in channel) {
        when (mssg) {
            is GetGameId -> {
                if (availableIds.size == 0) {
                    mssg.response.completeExceptionally(GameIdsRanOut())
                } else {
                    mssg.response.complete(availableIds.remove())
                }
            }
            is FreeGameId -> availableIds.add(mssg.gameId)
        }
    }
}


object GameId {
    private val gameIdActor = GlobalScope.gameIdActor()

    suspend fun generate(): Int {
        val gameId = CompletableDeferred<Int>()
        gameIdActor.send(GetGameId(gameId))
        return gameId.await()
    }

    suspend fun free(gameId: Int) {
        gameIdActor.send(FreeGameId(gameId))
    }
}
