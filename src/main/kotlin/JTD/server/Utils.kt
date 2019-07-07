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

operator fun Any.times(t: Int): String {
    val sb = StringBuilder()
    repeat(t) { sb.append(this) }
    return sb.toString()
}

fun String.padLeft(c: Char, targetLength: Int): String {
    if (length >= targetLength) {
        return this
    } else {
        val sb = StringBuilder()
        sb.append(c * (targetLength - length))
        sb.append(this)
        return sb.toString()
    }
}