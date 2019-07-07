package JTD.server

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond


suspend fun ApplicationCall.respondOr404(message: Any?) {
    if (message == null) {
        respond(HttpStatusCode.NotFound)
    } else {
        respond(message)
    }
}