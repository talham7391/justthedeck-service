package JTD.infrastructure.activegames

import JTD.infrastructure.Game
import kotlinx.coroutines.CompletableDeferred


sealed class ActiveGamesMessage<T>

data class GetGameFromId<T>(
        val id: T,
        val response: CompletableDeferred<Game<T>>
) : ActiveGamesMessage<T>()

data class SetGameForId<T>(
        val id: T,
        val game: Game<T>
) : ActiveGamesMessage<T>()

data class RemoveGame<T>(
        val id: T
) : ActiveGamesMessage<T>()