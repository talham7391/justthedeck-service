package JTD.cards_server.card

import kotlin.random.Random


val NORMAL_SUITS = listOf("clubs", "spades", "hearts", "diamonds")
val NORMAL_VALUES = listOf(
        "ace",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "10",
        "jack",
        "queen",
        "king"
)


fun randomNormalCard(): Card {
    return Card(
            "normal",
            NORMAL_SUITS[Random.nextInt(NORMAL_SUITS.size)],
            NORMAL_VALUES[Random.nextInt(NORMAL_VALUES.size)]
    )
}