package JTD.infrastructure.state

import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.coroutines.channels.Channel


data class PlayerState(
    val name: String,
    val collectedCards: List<Card>,
    val cardsInHand: List<Card>,

    var isConnected: Boolean
)


fun newPlayerState(
    name: String,
    collectedCards: List<Card> = emptyList(),
    cardsInHand: List<Card> = emptyList()
): PlayerState {
    return PlayerState(name, collectedCards, cardsInHand, false)
}
