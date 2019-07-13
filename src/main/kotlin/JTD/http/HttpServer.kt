package JTD.http

import JTD.infrastructure.GameManager
import io.ktor.application.*
import io.ktor.features.CallId
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.httpMethod
import io.ktor.request.path
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.coroutines.delay
import java.time.Duration
import kotlin.random.Random


fun <T> Application.httpServer(
        gameManager: GameManager<T>
) {
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

        route("/test") {
            get {
                delay(1000L)
                call.respond("Hello, World!")
            }
        }

        route("/games") {
            post {
                val game = gameManager.newGame()
                call.respond(GamePostResponse(game.id.toString()))
            }

            route("/{gameId}") {
                get {
                    val gameId = call.parameters["gameId"]
                    if (gameId == null) {
                        call.respond(HttpStatusCode.BadRequest)
                    } else {
                        try {
                            gameManager.getGame(gameId)
                            call.respond(HttpStatusCode.OK)
                        } catch (e: Exception) {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    }
                }
            }
        }

        webSocket("/ws/{gameId}") {
            val gameId = call.parameters["gameId"]
            if (gameId != null) {
                gameManager.handleConnection(gameId, this)
            }
        }
    }
}

data class GamePostResponse(val gameId: String)