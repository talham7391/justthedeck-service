package JTD.game.state

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.launch


sealed class GameStateMessage

data class PlayerConnected(
    val name: String,
    val response: CompletableDeferred<ReceiveChannel<ServerMessage>>
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


sealed class ServerMessage(val action: String)

data class PrintMessage(val message: String) : ServerMessage("PRINT_MESSAGE")
data class PlayerStatesUpdate(val states: Collection<PlayerState>) : ServerMessage("PLAYER_STATES")

fun CoroutineScope.gameStateActor() = actor<GameStateMessage> {
    val cardsOnTable = mutableListOf<CardOnTable>()
    val playerStates = mutableMapOf<String, PlayerState>()

    for (mssg in channel) {
        when(mssg) {

            is PlayerConnected -> {
                val playerState = playerStates[mssg.name].revive() ?: newPlayerState(mssg.name)
                playerState.isConnected = true
                playerStates[mssg.name] = playerState
                mssg.response.complete(playerState.update)

                playerStates.forEach { _, ps ->
                    launch { ps.update.send(PlayerStatesUpdate(playerStates.values)) }
                }
            }

            is PlayerDisconneced -> {
                playerStates[mssg.name]?.let {
                    it.update.cancel()
                    it.isConnected = false
                }

                playerStates.forEach { _, ps ->
                    launch { ps.update.send(PlayerStatesUpdate(playerStates.values)) }
                }
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
