package bart.scoreboard

import bart.scoreboard.service.model.Game
import java.lang.RuntimeException

class ScoreBoardService(private var games: Array<Game?>, private var gameCount: Int = 0, private var cachedGames: List<Game> = listOf<Game>()) : ScoreBoardServiceProtocol {
    companion object {
        val BOARD_GROWTH = 10

        @JvmStatic
        fun findIndexForArgument(argument: Game, games: Array<Game?>, gameCount: Int): Int = findIndexForArgument(argument, games, 0, gameCount)

        @JvmStatic
        fun moveGamesDown(spaceIndex: Int, games: Array<Game?>, gameCount: Int) {
            if (games.size == gameCount) throw RuntimeException("Games array is full.")

            for (index in gameCount  downTo  spaceIndex + 1) {
                games[index] = games[index - 1]
            }
            games[spaceIndex] = null
        }

        private fun findIndexForArgument(argument: Game, games: Array<Game?>, begin: Int, end: Int): Int {
            if (begin == end) return begin

            val middleIndex = begin + (end - begin) / 2
            val middleBase = games[middleIndex]!!

            if (isBasePreceding(argument, middleBase)) {
                // investigate middle to end
                val middleBackIndex = middleIndex + 1
                if (isIndexInRange(middleBackIndex, begin, end)) {
                    if (isBasePreceding(argument, games[middleBackIndex]!!)) {
                        // search in back part
                        return findIndexForArgument(argument, games, middleBackIndex, end)
                    } else {
                        // index found
                        return middleBackIndex
                    }
                } else {
                    return end
                }
            } else {
                // investigate beginning to middle
                return findIndexForArgument(argument, games, begin, middleIndex)
            }
        }

        private fun isBasePreceding(argument: Game, base: Game): Boolean = argument.totalScore < base.totalScore
                || argument.totalScore == base.totalScore && argument.createdTime.after(base.createdTime)

        private fun isIndexInRange(index: Int, begin: Int, end: Int): Boolean = begin <= index && index < end
    }

    constructor(initialBoardSize: Int = 10) : this(arrayOfNulls(initialBoardSize)) {}

    @Synchronized
    override fun startGame(game: Game) {
        if (isBoardFull()) expandBoard()

        val gameDestinationIndex = findIndexForArgument(game, games, 0, gameCount)
        moveGamesDown(gameDestinationIndex, games, gameCount)
        games[gameDestinationIndex] = game
        gameCount++
        cacheGames()
    }

    override fun getSummary(): List<Game> = cachedGames

    private fun cacheGames() {
        cachedGames = games.filterNotNull().toList()
    }

    private fun expandBoard() {
        val comingBoardSize = games.size + BOARD_GROWTH
        val comingBoard = arrayOfNulls<Game>(comingBoardSize)
        for (i in 0 until games.size) {
            comingBoard[i] = games[i]
        }
        games = comingBoard
    }

    private fun isBoardFull(): Boolean = gameCount == games.size
}