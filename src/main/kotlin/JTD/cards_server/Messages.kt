package JTD.cards_server

import JTD.cards_server.card.Card
import JTD.cards_server.card.CardOnTable
import JTD.cards_server.player.Player


sealed class CardGameMessage

object SharePlayers : CardGameMessage()

data class PutCardsOnTable(val cards: Collection<CardOnTable>) : CardGameMessage()

data class RemoveCardsOnTable(val cards: Collection<CardOnTable>) : CardGameMessage()

object ShareCardsOnTable : CardGameMessage()

data class GiveCardsToPlayersHand(
        val player: Player,
        val cards: Collection<Card>
) : CardGameMessage()