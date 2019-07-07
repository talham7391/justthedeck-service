package JTD.game


open class GameIdsRanOut : Exception("No more game ids available.")

open class GameDoesNotExist(gameId: Int) : Exception("No game with game id: $gameId exists.")