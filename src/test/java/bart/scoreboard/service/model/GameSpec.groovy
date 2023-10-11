package bart.scoreboard.service.model

import bart.scoreboard.ScoreBoardService
import spock.lang.Specification

import java.time.LocalDate

class GameSpec extends Specification {
    def "Should create a game when valid parameters given: (homeTeam:'#homeTeam', homeScore:#homeScore, awayTeam:'#awayTeam', awayScore:#awayScore)."() {
        given:
        def createdDate = LocalDate.of(2023, 10, 2).toDate()
        def totalScore = homeScore + awayScore

        when:
        def game = new Game(homeTeam, homeScore, awayTeam, awayScore, createdDate)

        then:
        game.homeTeam == homeTeam
        game.homeScore == homeScore
        game.awayTeam == awayTeam
        game.awayScore == awayScore
        game.totalScore == totalScore

        where:
        homeTeam | homeScore | awayTeam   | awayScore
        "Italy"  |         0 | "England"  |         0
        "France" |         1 | "Portugal" |         1
    }

    def "Should throw an exception on a game creation when invalid parameter given: ((homeTeam:'#homeTeam', homeScore:#homeScore, awayTeam:'#awayTeam', awayScore:#awayScore)), exception message: '#message'."() {
        given:
        def createdDate = LocalDate.of(2023, 10, 2).toDate()

        when:
        new Game(homeTeam, homeScore, awayTeam, awayScore, createdDate)

        then:
        def ex = thrown(RuntimeException.class)
        ex.message == message

        where:
        homeTeam | homeScore | awayTeam   | awayScore | message
        "Italy"  |        -1 | "England"  |         0 | "Invalid homeScore:-1."
        "Italy"  |         0 | "England"  |        -1 | "Invalid awayScore:-1."
        ""       |         1 | "Portugal" |         1 | "Invalid homeTeam:''."
        "France" |         1 | ""         |         1 | "Invalid awayTeam:''."
        " "      |         1 | "Portugal" |         1 | "Invalid homeTeam:' '."
        "France" |         1 | " "        |         1 | "Invalid awayTeam:' '."
        "  "     |         1 | "Portugal" |         1 | "Invalid homeTeam:'  '."
        "France" |         1 | "  "       |         1 | "Invalid awayTeam:'  '."
        ""       |        -3 | " "        |        -5 | "Invalid homeTeam:''. Invalid homeScore:-3. Invalid awayTeam:' '. Invalid awayScore:-5."
    }

    def "Should create a fresh game when valid parameters given: (homeTeam:'#homeTeam', awayTeam:'#awayTeam')."() {
        given:
        def homeScore = 0
        def awayScore = 0

        when:
        def game = new Game(homeTeam, awayTeam)

        then:
        game.homeTeam == homeTeam
        game.homeScore == homeScore
        game.awayTeam == awayTeam
        game.awayScore == awayScore

        where:
        homeTeam | awayTeam
        "Italy"  | "England"
        "France" | "Portugal"
    }

    def "Should generate a game key from a game."() {
        when:
        def actualGameKey = ScoreBoardService.generateGameKey(game)

        then:
        actualGameKey == expectedGameKey

        where:
        game                          | expectedGameKey
        new Game("Romania", "France") | "ROMANIA FRANCE"
        new Game("Chile", "Canada")   | "CHILE CANADA"
    }

    def "Should generate a game key from a score update."() {
        when:
        def actualGameKey = ScoreBoardService.generateGameKey(scoreUpdate)

        then:
        actualGameKey == expectedGameKey

        where:
        scoreUpdate                                | expectedGameKey
        new ScoreUpdate("Romania", 1, "France", 2) | "ROMANIA FRANCE"
        new ScoreUpdate("Chile", 0, "Canada", 1)   | "CHILE CANADA"
    }
}
