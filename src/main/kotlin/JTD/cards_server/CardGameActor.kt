package JTD.cards_server

import JTD.cards_server.card.CardOnTable
import JTD.cards_server.player.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.actor


fun CoroutineScope.cardGameActor(playersManager: DefaultPlayersManager) = actor<CardGameMessage> {

    val cardsOnTable = mutableListOf<CardOnTable>()

    for (mssg in channel) {
        when (mssg) {
            is SharePlayers -> {
                val players = playersManager.getPlayers()
                players.send(PlayersServerAction(players))
            }

            is PutCardsOnTable -> {
                cardsOnTable.addAll(mssg.cards)
                playersManager.sendToPlayers(CardsOnTableServerAction(mssg.cards))
            }

            is PlayerPutCardsOnTable -> {
                cardsOnTable.addAll(mssg.cards)
                playersManager.sendToPlayers(PlayerCardsOnTableServerAction(mssg.player.name, mssg.cards))
            }

            is ShareCardsOnTable -> {
                playersManager.sendToPlayers(CardsOnTableServerAction(cardsOnTable))
            }

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