package JTD.infrastructure.state

import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.coroutines.channels.Channel


data class PlayerState(
    val name: String,
    val collectedCards: List<Int>,
    val cardsInHand: List<Int>,

    var isConnected: Boolean
)


fun newPlayerState(
    name: String,
    collectedCards: List<Int> = emptyList(),
    cardsInHand: List<Int> = emptyList()
): PlayerState {
    return PlayerState(name, collectedCards, cardsInHand, false)
}
