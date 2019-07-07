package JTD.game

import io.ktor.http.cio.websocket.send
import io.ktor.websocket.WebSocketServerSession


class Game(val gameId: Int) {

    fun getState(): GameState {
        return GameState(gameId)
    }

    suspend fun WebSocketServerSession.HandlePlayer() {
        println("Connection started")

        send("Welcome to the game!")

        for (frame in incoming) {
            println("Message received")
        }

        println("Connection closing")
    }

    suspend fun includeConnection(conn: WebSocketServerSession) = conn.HandlePlayer()
}

data class GameState(val gameId: Int)
