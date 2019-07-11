package JTD.server

import JTD.game.GamesManager
import JTD.game.handleConnection
import io.ktor.application.*
import io.ktor.features.CallId
import io.ktor.features.ContentNegotiation
import io.ktor.features.callId
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.httpMethod
import io.ktor.request.path
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
import java.util.*
import kotlin.random.Random


fun Application.httpServer() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(5)
    }
    install(ContentNegotiation) {
        jackson {

        }
    }
    install(CallId) {
        generate {
            return@generate Random.nextInt(10000).toString().padLeft('0', 4)
        }
    }

    intercept(ApplicationCallPipeline.Monitoring) {
        log.info("${call.request.httpMethod.value} ${call.request.path()}")
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
                call.respond(GamePostResponse(gameId))
            }

            route("/{game_id}") {
                get("/state") {
                    val gameId = call.parameters["game_id"]
                    if (gameId == null) {
                        call.respond(HttpStatusCode.BadRequest)
                    } else {
                        val gameState = GamesManager.getCardsOnTable(gameId.toInt())
                        call.respondOr404(gameState)
                    }
                }
            }
        }

        webSocket("/ws/{game_id}", handler = WebSocketServerSession::handleConnection)
    }
}

data class GamePostResponse(val gameId: Int)