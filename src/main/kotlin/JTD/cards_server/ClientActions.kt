package JTD.cards_server

import JTD.cards_server.card.Card
import JTD.cards_server.card.CardOnTable


open class ClientAction(val name: String, open val data: Any)

data class SetNameClientAction(override val data: SetNameActionData) : ClientAction("SET_NAME", data)
data class SetNameActionData(val name: String)


data class PutCardsOnTableClientAction(
        override val data: PutCardsOnTableActionData
) : ClientAction("PUT_CARDS_ON_TABLE", data)
data class PutCardsOnTableActionData(val cards: Collection<CardOnTable>)


data class AddCardsToHandClientAction(
        override val data: AddCardsToHandActionData
) : ClientAction("ADD_CARDS_TO_HAND", data)
data class AddCardsToHandActionData(val cards: Collection<Card>)


data class RemoveCardsFromHandClientAction(
        override val data: RemoveCardsFromHandActionData
) : ClientAction("REMOVE_CARDS_FROM_HAND", data)
data class RemoveCardsFromHandActionData(val cards: Collection<Card>)


data class RemoveCardsFromTableClientAction(
        override val data: RemoveCardsFromTableActionData
) : ClientAction("REMOVE_CARDS_FROM_TABLE", data)
data class RemoveCardsFromTableActionData(val cards: Collection<CardOnTable>)