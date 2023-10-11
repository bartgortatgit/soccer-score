package bart.scoreboard

import bart.scoreboard.service.model.Game
import bart.scoreboard.service.model.GameRemoval
import bart.scoreboard.service.model.ScoreUpdate
import java.lang.RuntimeException

class ScoreBoardService(private var games: Array<Game?>,
                        private var gameCount: Int = 0,
                        private var cachedGames: List<Game> = listOf(),
                        private var gameIndex: MutableMap<String, Int> = mutableMapOf()) : ScoreBoardServiceProtocol {
    companion object {
        val BOARD_GROWTH = 10

        @JvmStatic
        fun findIndexForArgument(argument: Game, games: Array<Game?>, gameCount: Int): Int = findIndexForArgument(argument, games, 0, gameCount)

        @JvmStatic
        fun generateGameKey(game: Game) = "${game.homeTeam} ${game.awayTeam}".uppercase()

        @JvmStatic
        fun generateGameKey(update: ScoreUpdate) = "${update.homeTeam} ${update.awayTeam}".uppercase()

        @JvmStatic
        fun generateGameKey(gameRemoval: GameRemoval) = "${gameRemoval.homeTeam} ${gameRemoval.awayTeam}".uppercase()

        @JvmStatic
        fun moveGamesDown(spaceIndex: Int, games: Array<Game?>, gameCount: Int) {
            if (games.size == gameCount) throw RuntimeException("Games array is full.")
            if (spaceIndex < 0 || games.size <= spaceIndex) throw RuntimeException("Index ${spaceIndex} is out of range of a board with ${gameCount} elements.")

            for (index in gameCount  downTo  spaceIndex + 1) {
                games[index] = games[index - 1]
            }
            games[spaceIndex] = null
        }

        @JvmStatic
        fun moveGamesUp(spaceIndex: Int, games: Array<Game?>, gameCount: Int) {
            if (games.size == gameCount) throw RuntimeException("Games array is full.")

            for (index in spaceIndex until gameCount - 1) {
                games[index] = games[index + 1]
            }
            games[gameCount - 1] = null
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
        val gameKey = generateGameKey(game)
        if (gameIndex.containsKey(gameKey)) throw RuntimeException("Game between homeTeam:'${game.homeTeam}' awayTeam:'${game.awayTeam}' has already been started.")

        if (isBoardFull()) expandBoard()

        var gameDestinationIndex = 0
        if (gameCount > 0) {
            gameDestinationIndex = findIndexForArgument(game, games, 0, gameCount)
            moveGamesDown(gameDestinationIndex, games, gameCount)
        }
        games[gameDestinationIndex] = game
        gameCount++
        buildIndex(gameDestinationIndex, gameCount)
        cacheGames()
    }

    override fun getSummary(): List<Game> = cachedGames

    @Synchronized
    override fun updateScore(scoreUpdate: ScoreUpdate) {
        val gameKey = generateGameKey(scoreUpdate)
        val sourceIndex = gameIndex.getOrDefault(gameKey, -1)

        if (sourceIndex == -1) throw RuntimeException("Game between homeTeam:'${scoreUpdate.homeTeam}', awayTeam:'${scoreUpdate.awayTeam}' is not on the board.")

        val updatedGame = Game(games[sourceIndex]!!, scoreUpdate.homeScore, scoreUpdate.awayScore)
        val destinationIndex = findIndexForArgument(updatedGame, games, gameCount)
        for (index in sourceIndex downTo destinationIndex + 1) {
            games[index] = games[index - 1]
        }
        games[destinationIndex] = updatedGame
        buildIndex(destinationIndex, sourceIndex + 1)
        cacheGames()
    }

    @Synchronized
    override fun finishGame(gameRemoval: GameRemoval) {
        val sourceIndex = gameIndex.getOrDefault(generateGameKey(gameRemoval), -1)

        if (sourceIndex == -1) throw RuntimeException("Game between homeTeam:'${gameRemoval.homeTeam}', awayTeam:'${gameRemoval.awayTeam}' is not on the board.")

        moveGamesUp(sourceIndex, games, gameCount)
        gameCount--
        buildIndex(sourceIndex, gameCount)
        cacheGames()
    }

    private fun buildIndex(begin: Int, end: Int) {
        for (index in begin until end) {
            gameIndex.put(generateGameKey(games[index]!!), index)
        }
    }

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