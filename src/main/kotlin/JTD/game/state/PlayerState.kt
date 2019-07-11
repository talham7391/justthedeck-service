package JTD.game.state

import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.coroutines.channels.Channel


data class PlayerState(
    val name: String,
    val collectedCards: List<Card>,
    val cardsInHand: List<Card>,

    var isConnected: Boolean,
    @JsonIgnore val update: Channel<ServerMessage>
)


fun newPlayerState(
    name: String,
    collectedCards: List<Card> = emptyList(),
    cardsInHand: List<Card> = emptyList()
): PlayerState {
    val channel = Channel<ServerMessage>()
    return PlayerState(name, collectedCards, cardsInHand, false, channel)
}

fun PlayerState?.revive(): PlayerState? {
    if (this == null) {
        return null
    }
    return newPlayerState(name, collectedCards, cardsInHand)
}