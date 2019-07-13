package JTD.infrastructure

import io.ktor.http.cio.websocket.Frame
import io.ktor.websocket.WebSocketServerSession


interface Game<T> {
    val id: T
    suspend fun WebSocketServerSession.setup()
    suspend fun WebSocketServerSession.handleActionFromClient(frame: Frame)
    suspend fun WebSocketServerSession.teardown()
    suspend fun includeConnection(conn: WebSocketServerSession)
}

abstract class BaseGame<T>
    : Game<T> {

    suspend fun WebSocketServerSession.handleConnection() {
        setup()

        for (frame in incoming) {
            handleActionFromClient(frame)
        }

        teardown()
    }

    override suspend fun includeConnection(conn: WebSocketServerSession) = conn.handleConnection()

}
