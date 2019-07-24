package JTD.cards_server

import JTD.cards_server.card.CardOnTable
import JTD.cards_server.player.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.actor


fun CoroutineScope.cardGameActor(playersManager: DefaultPlayersManager) = actor<CardGameMessage> {

    val cardsOnTable = mutableListOf<CardOnTable>()

    suspend fun shareCardsWithPlayers() {
        playersManager.sendToPlayers(CardsOnTableServerAction(cardsOnTable))
    }

    for (mssg in channel) {
        when (mssg) {
            is SharePlayers -> {
                val players = playersManager.getPlayers()
                players.send(PlayersServerAction(players))
            }

            is PutCardsOnTable -> {
                cardsOnTable.addAll(mssg.cards)
                shareCardsWithPlayers()
            }

            is RemoveCardsOnTable -> {
                cardsOnTable.removeAll(mssg.cards)
                shareCardsWithPlayers()
            }

            is ShareCardsOnTable -> { shareCardsWithPlayers() }

            is GiveCardsToPlayersHand -> {
                mssg.player.addCardsToHand(mssg.cards)
            }
        }
    }
}

suspend fun DefaultPlayersManager.sendToPlayers(action: ServerAction) {
    val players = getPlayers()
    players.send(action)
}