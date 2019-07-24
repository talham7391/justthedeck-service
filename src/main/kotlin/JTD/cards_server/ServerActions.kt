package JTD.cards_server

import JTD.cards_server.card.Card
import JTD.cards_server.card.CardOnTable
import JTD.cards_server.player.Player


open class ServerAction(val name: String)

data class PlayersServerAction(val players: Collection<Player>) : ServerAction("PLAYERS")

data class CardsOnTableServerAction(val cards: Collection<CardOnTable>) : ServerAction("PUT_CARDS_ON_TABLE")

data class GiveCardsToPlayersHandServerAction(
        val cards: Collection<Card>
) : ServerAction("PUT_CARDS_IN_HAND")