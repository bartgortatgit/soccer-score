package bart.scoreboard.service.model

import java.lang.RuntimeException
import java.util.Date
import java.util.Objects

class Game {
    companion object {
        fun isNameInvalid(name : String) = name.isBlank()
        fun isScoreInvalid(score : Int) = score < 0
    }

    val homeTeam: String
    val homeScore: Int
    val awayTeam: String
    val awayScore: Int
    val createdTime: Date
    val totalScore: Int

    constructor(homeTeam: String, homeScore: Int, awayTeam: String, awayScore: Int, createdTime: Date) {
        val exceptionMessages = mutableListOf<String>()
        if (isNameInvalid(homeTeam)) exceptionMessages.add("Invalid homeTeam:'${homeTeam}'.")
        if (isScoreInvalid(homeScore)) exceptionMessages.add("Invalid homeScore:${homeScore}.")
        if (isNameInvalid(awayTeam)) exceptionMessages.add("Invalid awayTeam:'${awayTeam}'.")
        if (isScoreInvalid(awayScore)) exceptionMessages.add(("Invalid awayScore:${awayScore}."))
        if (!exceptionMessages.isEmpty()) throw RuntimeException(exceptionMessages.joinToString(separator = " "))

        this.homeTeam = homeTeam
        this.homeScore = homeScore
        this.awayTeam = awayTeam
        this.awayScore = awayScore
        this.createdTime = createdTime
        this.totalScore = homeScore + awayScore
    }

    constructor(homeTeam: String, awayTeam: String) : this(homeTeam, 0, awayTeam, 0, Date())

    constructor(oldGame: Game, homeScore: Int, awayScore: Int) : this(oldGame.homeTeam, homeScore, oldGame.awayTeam, awayScore, oldGame.createdTime)

    override fun equals(other: Any?): Boolean {
        if (other is Game) {
            val compared : Game = other
            return homeTeam == compared.homeTeam
                    && homeScore == compared.homeScore
                    && awayTeam == compared.awayTeam
                    && awayScore == compared.awayScore
                    && createdTime == compared.createdTime
                    && totalScore == compared.totalScore
        }
        return false
    }

    override fun hashCode(): Int {
        return Objects.hash(homeTeam, homeScore, awayTeam, awayScore, createdTime, totalScore)
    }

    override fun toString(): String {
        return "(homeTeam:'${homeTeam}', homeScore:${homeScore}, awayTeam:'${awayTeam}', awayScore:${awayScore}, createdTime:${createdTime}, totalScore:${totalScore})"
    }
}