package JTD.game.state

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.actor


sealed class GameStateMessage

data class PlayerConnected(
    val name: String,
    val response: CompletableDeferred<ReceiveChannel<StateUpdateMessage>>
) : GameStateMessage()

data class PlayerDisconneced(
    val name: String
) : GameStateMessage()

data class GetPlayerState(
    val name: String,
    val response: CompletableDeferred<PlayerState>
) : GameStateMessage()

data class AddCardsToTable(val cards: List<CardOnTable>) : GameStateMessage()

data class GetCardsOnTable(
    val response: CompletableDeferred<List<CardOnTable>>
) : GameStateMessage()


sealed class StateUpdateMessage

object Test : StateUpdateMessage()

fun CoroutineScope.gameStateActor() = actor<GameStateMessage> {
    val cardsOnTable = mutableListOf<CardOnTable>()
    val playerStates = mutableMapOf<String, PlayerState>()

    for (mssg in channel) {
        when(mssg) {

            is PlayerConnected -> {
                val playerState = playerStates[mssg.name].revive() ?: newPlayerState(mssg.name)
                playerStates[mssg.name] = playerState

                mssg.response.complete(playerState.update)
            }

            is PlayerDisconneced -> {
                playerStates[mssg.name]?.update?.cancel()
            }

            is AddCardsToTable -> {
                cardsOnTable.addAll(mssg.cards)
            }

            is GetPlayerState -> {
                val player = playerStates[mssg.name]
                if (player == null) {
                    mssg.response.completeExceptionally(PlayerDoesNotExist())
                } else {
                    mssg.response.complete(player)
                }
            }

            is GetCardsOnTable -> {
                mssg.response.complete(cardsOnTable)
            }
        }
    }
}