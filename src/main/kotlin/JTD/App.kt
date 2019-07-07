package JTD

import JTD.server.httpServer
import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty


fun main() {
    val server =embeddedServer(Netty, port = 8000, module = Application::httpServer)
    server.start(wait = true)
}