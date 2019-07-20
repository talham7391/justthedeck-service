package JTD.cards_server.card


open class Card (
        open val type: String,
        open val suit: String,
        open val value: String
)


data class CardOnTable(
        override val type: String,
        override val suit: String,
        override val value: String,
        val side: String,
        val location: Location
) : Card(type, suit, value)


data class Location(val x: Float, val y: Float)
