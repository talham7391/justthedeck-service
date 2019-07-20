package JTD.cards_server.player

import JTD.cards_server.ServerAction
import JTD.cards_server.card.Card


data class CardsInHand(val cards: Collection<Card>) : ServerAction("CARDS_IN_HAND")

data class PutCardsInHandServerAction(
        val cards: Collection<Card>
) : ServerAction("PUT_CARDS_IN_HAND")

data class RemoveCardsFromHandServerAction(
        val cards: Collection<Card>
) : ServerAction("REMOVE_CARDS_FROM_HAND")