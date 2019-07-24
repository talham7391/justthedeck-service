package JTD

import JTD.cards_server.CardGameManager
import JTD.http.httpServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty


fun main() {
    val gameManager = CardGameManager()
    val server = embeddedServer(
            Netty,
            host = "0.0.0.0",
            port = 8000
    ) { httpServer(gameManager) }
    server.start(wait = true)
}