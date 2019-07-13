package JTD.infrastructure.activegames


open class GameDoesNotExist(id: Any?) : Exception("No game with game id: $id exists.")