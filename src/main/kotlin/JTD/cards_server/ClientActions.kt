package JTD.cards_server


open class ClientAction(val name: String, open val data: Any)

data class SetNameClientAction(override val data: SetNameActionData) : ClientAction("SET_NAME", data)
data class SetNameActionData(val name: String)
