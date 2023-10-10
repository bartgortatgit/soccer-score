package bart.scoreboard

import bart.scoreboard.service.model.Game
import bart.scoreboard.service.model.ScoreUpdate

interface ScoreBoardServiceProtocol {
    fun startGame(game: Game)
    fun getSummary(): List<Game>
    fun updateScore(scoreUpdate: ScoreUpdate)
}