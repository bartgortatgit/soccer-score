package bart.scoreboard

import bart.scoreboard.service.model.Game

interface ScoreBoardServiceProtocol {
    fun startGame(game: Game)
    fun getSummary(): List<Game>
}