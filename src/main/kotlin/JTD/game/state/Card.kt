package JTD.game.state


interface Card {
    val type: String
    val suit: String
    val number: Int
}


enum class Side {
    FACE_UP,
    FACE_DOWN
}


data class CardOnTable(
    val card: Card,
    val side: Side,
    val location: Pair<Float, Float>
)
