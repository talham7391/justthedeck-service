package JTD.cards_server

import JTD.cards_server.state.players.CardOnTable
import JTD.cards_server.state.players.Player


open class ServerAction(val name: String)

data class PlayersServerAction(val players: Collection<Player>) : ServerAction("PLAYERS")

data class CardsOnTableServerAction(val cards: Collection<CardOnTable>) : ServerAction("PUT_CARDS_ON_TABLE")

data class PlayerCardsOnTableServerAction(
        val playerName: String,
        val cards: Collection<CardOnTable>
) : ServerAction("PLAYER_PUT_CARDS_ON_TABLE")