package JTD.cards_server

import JTD.cards_server.state.players.CardOnTable
import JTD.cards_server.state.players.Player


sealed class CardGameMessage

object SharePlayers : CardGameMessage()

data class PutCardsOnTable(val cards: Collection<CardOnTable>) : CardGameMessage()

data class PlayerPutCardsOnTable(
        val player: Player,
        val cards: Collection<CardOnTable>
) : CardGameMessage()

object ShareCardsOnTable : CardGameMessage()