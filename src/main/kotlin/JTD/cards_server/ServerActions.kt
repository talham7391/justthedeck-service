package JTD.cards_server

import JTD.cards_server.state.players.Player


open class ServerAction(val name: String)

data class PlayersServerAction(val players: Collection<Player>) : ServerAction("PLAYERS")