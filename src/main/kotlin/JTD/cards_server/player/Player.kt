package JTD.cards_server.player

import JTD.cards_server.ServerAction
import JTD.cards_server.card.Card
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.http.cio.websocket.send
import io.ktor.websocket.WebSocketServerSession
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.actor


interface Player {
    val name: String
    val joinOrder: Int

    var conn: WebSocketServerSession?
    val connected
        get() = conn != null

    suspend fun shareCardsWithSelf()
    suspend fun getCardsInHand(): Collection<Card>
    suspend fun addCardsToHand(cards: Collection<Card>)
    suspend fun removeCardsFromHand(cards: Collection<Card>)

    suspend fun getCardsinCollection(): Collection<Card>
    suspend fun addCardsToCollection(cards: Collection<Card>)
}


private class PlayerImpl(
        override val name: String,
        override val joinOrder: Int,
        @JsonIgnore override var conn: WebSocketServerSession?
) : Player {

    private val playerActor = GlobalScope.playerActor(this)

    override suspend fun shareCardsWithSelf() {
        playerActor.send(ShareCardsInHandWithSelf)
    }

    override suspend fun getCardsInHand(): Collection<Card> {
        val cards = CompletableDeferred<Collection<Card>>()
        playerActor.send(GetCardsInHandMessage(cards))
        return cards.await()
    }

    override suspend fun addCardsToHand(cards: Collection<Card>) {
        playerActor.send(AddCardsToHandMessage(cards))
    }

    override suspend fun removeCardsFromHand(cards: Collection<Card>) {
        playerActor.send(RemoveCardsFromHandMessage(cards))
    }

    override suspend fun getCardsinCollection(): Collection<Card> {
        val cards = CompletableDeferred<Collection<Card>>()
        playerActor.send(GetCardsInCollectionMessage(cards))
        return cards.await()
    }

    override suspend fun addCardsToCollection(cards: Collection<Card>) {
        playerActor.send(AddCardsToCollectionMessage(cards))
    }
}


fun CoroutineScope.playerActor(player: Player) = actor<PlayerMessage> {
    val cardsInHand = mutableListOf<Card>()
    val cardsInCollection = mutableListOf<Card>()

    cardsInHand.add(Card("normal", "hearts", "6"))
    cardsInHand.add(Card("normal", "spades", "queen"))
    cardsInHand.add(Card("normal", "diamonds", "ace"))

    for (mssg in channel) {
        when (mssg) {
            is ShareCardsInHandWithSelf -> {
                player.send(CardsInHand(cardsInHand))
            }

            is GetCardsInHandMessage -> { mssg.cards.complete(cardsInHand) }

            is AddCardsToHandMessage -> {
                cardsInHand.addAll(mssg.cards)
                player.send(PutCardsInHandServerAction(mssg.cards))
            }

            is RemoveCardsFromHandMessage -> {
                cardsInHand.removeAll(mssg.cards)
                player.send(RemoveCardsFromHandServerAction(mssg.cards))
            }

            is GetCardsInCollectionMessage -> { mssg.cards.complete(cardsInCollection) }

            is AddCardsToCollectionMessage -> { cardsInCollection.addAll(mssg.cards) }
        }
    }
}

val objectMapper = ObjectMapper().registerModule(KotlinModule())

fun newPlayer(name: String, joinOrder: Int, conn: WebSocketServerSession?): Player {
    return PlayerImpl(name, joinOrder, conn)
}

suspend fun Player.send(action: ServerAction) {
    conn?.send(objectMapper.writeValueAsBytes(action))
}

suspend fun Collection<Player>.send(action: ServerAction) {
    forEach {
        it.conn?.send(objectMapper.writeValueAsBytes(action))
    }
}