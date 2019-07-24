package JTD.cards_server.card

import JTD.cards_server.player.Player
import kotlin.random.Random


open class Card (
        open val type: String,
        open val suit: String,
        open val value: String
) {
    override fun equals(other: Any?): Boolean {
        val c = other as? Card ?: return false
        return type == c.type && suit == c.suit && value == c.value
    }
}


data class CardOnTable(
        override val type: String,
        override val suit: String,
        override val value: String,
        val side: String,
        val location: Location,
        val playedBy: String?
) : Card(type, suit, value)


data class Location(val x: Float, val y: Float)


fun MutableList<Card>.randomlyTake(num: Int) = (0 until num).map {
    val idx = Random.nextInt(size)
    return@map removeAt(idx)
}