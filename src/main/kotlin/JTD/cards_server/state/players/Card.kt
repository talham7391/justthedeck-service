package JTD.cards_server.state.players


open class Card (
        open val type: String,
        open val suit: String,
        open val number: Int
)


data class CardOnTable(
        override val type: String,
        override val suit: String,
        override val number: Int,
        val side: String,
        val location: Location
) : Card(type, suit, number)


data class Location(val x: Float, val y: Float)