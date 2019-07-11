package JTD.game.state

import kotlinx.coroutines.channels.Channel


data class PlayerState(
    val name: String,
    val collectedCards: List<Card>,
    val cardsInHand: List<Card>,
    val update: Channel<StateUpdateMessage>
)


fun newPlayerState(
    name: String,
    collectedCards: List<Card> = emptyList(),
    cardsInHand: List<Card> = emptyList()
): PlayerState {
    val channel = Channel<StateUpdateMessage>()
    return PlayerState(name, collectedCards, cardsInHand, channel)
}

fun PlayerState?.revive(): PlayerState? {
    if (this == null) {
        return null
    }
    return newPlayerState(name, collectedCards, cardsInHand)
}