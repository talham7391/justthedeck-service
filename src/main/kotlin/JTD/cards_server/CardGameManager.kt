package JTD.cards_server

import JTD.infrastructure.BaseGameManager
import JTD.infrastructure.Game
import JTD.infrastructure.info
import org.slf4j.LoggerFactory


class CardGameManager : BaseGameManager<Int>() {
    private val logger = LoggerFactory.getLogger(CardGameManager::class.java)

    override suspend fun newGame(): Game<Int> {
        val game = super.newGame()
        logger.info(game.id, "Created.")
        return game
    }

    override suspend fun deleteGame(id: Int) {
        super.deleteGame(id)
        logger.info(id, "Deleted.")
    }

    override suspend fun createGame(id: Int): Game<Int> {
        return CardGame(id)
    }

    override fun newId(i: Int) = i

    override fun String.getSupportedFormat() = toInt()
}