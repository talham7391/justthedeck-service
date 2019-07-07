package JTD.server

import JTD.game.GamesManager
import JTD.game.handleConnection
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.websocket.WebSocketServerSession
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.coroutines.delay
import java.time.Duration


fun Application.httpServer() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(5)
    }
    install(ContentNegotiation) {
        jackson {

        }
    }

    routing {
        get {
            call.respond(HttpStatusCode.OK)
        }

        get("/test") {
            delay(1000L)
            call.respond("Hello, World!")
        }

        route("/games") {
            post {
                val gameId = GamesManager.createGame()
                call.respond("Game id: $gameId")
            }

            route("/{game_id}") {
                get {
                    val gameId = call.parameters["game_id"]
                    println("Incoming request for: $gameId")
                    if (gameId == null) {
                        call.respond(HttpStatusCode.BadRequest)
                    } else {
                        val gameState = GamesManager.getGameState(gameId.toInt())
                        call.respondOr404(gameState)
                    }
                }
            }
        }

        webSocket("/ws/{game_id}", handler = WebSocketServerSession::handleConnection)
    }
}