package JTD.game

import org.slf4j.Logger

fun Logger.debug(gameId: Int, message: Any) =
        debug("Game ID: $gameId - $message")

fun Logger.debug(gameId: Int, callId: String?, message: Any) =
        debug("Game ID: $gameId - CID: $callId - $message")

fun Logger.info(gameId: Int, message: Any) =
        info("Game ID: $gameId - $message")

fun Logger.info(gameId: Int, callId: String?, message: Any) =
        info("Game ID: $gameId - CID: $callId - $message")
