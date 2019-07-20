package JTD.cards_server.player

import JTD.cards_server.card.Card
import io.ktor.websocket.WebSocketServerSession
import kotlinx.coroutines.CompletableDeferred


sealed class PlayersMessage

data class AddPlayerMessage(
        val name: String,
        val conn: WebSocketServerSession,
        val player: CompletableDeferred<Player>
) : PlayersMessage()

data class GetPlayersMessage(val players: CompletableDeferred<Collection<Player>>) : PlayersMessage()

data class GetPlayerMessage(
        val player: CompletableDeferred<Player>,
        val conn: WebSocketServerSession
) : PlayersMessage()

data class RemoveConnectionMessage(val conn: WebSocketServerSession) : PlayersMessage()



sealed class PlayerMessage

object ShareCardsInHandWithSelf : PlayerMessage()

data class GetCardsInHandMessage(val cards: CompletableDeferred<Collection<Card>>) : PlayerMessage()

data class AddCardsToHandMessage(val cards: Collection<Card>) : PlayerMessage()

data class RemoveCardsFromHandMessage(val cards: Collection<Card>) : PlayerMessage()

data class GetCardsInCollectionMessage(val cards: CompletableDeferred<Collection<Card>>) : PlayerMessage()

data class AddCardsToCollectionMessage(val cards: Collection<Card>) : PlayerMessage()