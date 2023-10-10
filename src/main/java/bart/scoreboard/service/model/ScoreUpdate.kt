package bart.scoreboard.service.model

@JvmRecord
data class ScoreUpdate(val homeTeam: String, val homeScore: Int, val awayTeam: String, val awayScore: Int)