package bart.scoreboard.service

import bart.scoreboard.ScoreBoardService
import bart.scoreboard.service.model.Game
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDate

class ScoreBoardServiceSpec extends Specification {
    @Shared
    ScoreBoardService scoreBoardService

    def setupSpec() {
        scoreBoardService = new ScoreBoardService(10)
    }

    def "Should start a game and place it on an empty board."() {
        given:
        def game = new Game("Scotland", "Spain")
        def scoreBoardService = new ScoreBoardService(0)

        when:
        scoreBoardService.startGame(game)
        def summary = scoreBoardService.getSummary()

        then:
        summary == List.of(game)
    }

    def "Should find an index on a board for an argument with #gameCount games."() {
        when:
        def index = ScoreBoardService.findIndexForArgument(searchArgument, games, gameCount)

        then:
        index == positionIndex

        where:
        searchArgument                                | positionIndex | gameCount | games
        new Game("France", "Italy")                   |             0 |         0 | new Game[] {}
        new Game("France", "Italy")                   |             1 |         1 | new Game[] { new Game("Hungary", 1, "Norway", 2, new Date())}
        new Game("France", "Italy")                   |             2 |         2 | new Game[] { new Game("Hungary", 1, "Norway", 2, new Date()), new Game("Belgium", 1, "Croatia", 1, new Date())}
        new Game("France", 2, "Italy", 3, new Date()) |             0 |         4 | new Game[] { new Game("Czech Republic", 2, "Spain", 2, new Date()), new Game("Hungary", 1, "Norway", 2, new Date()), new Game("Belgium", 1, "Croatia", 1, new Date()), new Game("Austria", 0, "Wales", 1, new Date())}
        new Game("France", 2, "Italy", 3, new Date()) |             1 |         5 | new Game[] { new Game("Estonia", 3, "France", 3, new Date()), new Game("Czech Republic", 2, "Spain", 2, new Date()), new Game("Hungary", 1, "Norway", 2, new Date()), new Game("Austria", 2, "Wales", 1, new Date()), new Game("Belgium", 1, "Croatia", 1, new Date())}
        new Game("France", "Italy")                   |             5 |         5 | new Game[] { new Game("Estonia", 3, "France", 3, new Date()), new Game("Czech Republic", 2, "Spain", 2, new Date()), new Game("Hungary", 1, "Norway", 2, new Date()), new Game("Austria", 2, "Wales", 1, new Date()), new Game("Belgium", 1, "Croatia", 1, new Date())}
    }

    def "Should move games to make an empty space at index: #spaceIndex."() {
        when:
        ScoreBoardService.moveGamesDown(spaceIndex, games, gameCaount)

        then:
        games == after

        where:
        spaceIndex | gameCaount | games                                                                                                                                      | after
                 0 |          0 | new Game[] {null, null, null}                                                                                                              | new Game[] {null, null, null}
                 0 |          1 | new Game[] {new Game("Japan", "Wales"), null, null}                                                                                        | new Game[] {null, new Game("Japan", "Wales"), null}
                 1 |          2 | new Game[] {new Game("Japan", "Wales"), new Game("Belgium", "Israel"), null}                                                               | new Game[] {new Game("Japan", "Wales"), null, new Game("Belgium", "Israel")}
                 2 |          4 | new Game[] {new Game("Japan", "Wales"), new Game("Belgium", "Israel"), new Game("Slovakia", "Portugal"), new Game("China", "Spain"), null} | new Game[] {new Game("Japan", "Wales"), new Game("Belgium", "Israel"), null, new Game("Slovakia", "Portugal"), new Game("China", "Spain")}
    }

    def "Should thorw an exception when moving games in full array."() {
        when:
        ScoreBoardService.moveGamesDown(spaceIndex, games, gameCaount)

        then:
        def ex = thrown(RuntimeException.class)
        ex.message == "Games array is full."

        where:
        spaceIndex | gameCaount | games
                 0 |          0 | new Game[] {}
                 0 |          1 | new Game[] {new Game("Japan", "Wales")}
                 1 |          2 | new Game[] {new Game("Japan", "Wales"), new Game("Belgium", "Israel")}
                 2 |          4 | new Game[] {new Game("Japan", "Wales"), new Game("Belgium", "Israel"), new Game("Slovakia", "Portugal"), new Game("China", "Spain")}
    }

    def "Should add games to service and generate a summary"() {
        given:
        def games = List.of(
                new Game("France", 2, "Italy", 3, LocalDate.of(2023, 10, 9).toDate()),
                new Game("Greece", 1, "England", 1, LocalDate.of(2023, 10, 10).toDate()),
                new Game("South Korea", 1, "Germany", 1, LocalDate.of(2023, 10, 9).toDate()),
                new Game("Poland", 1, "Spain", 0, LocalDate.of(2023, 10, 9).toDate()),
                new Game("Ireland", 0, "Ukraine", 0, LocalDate.of(2023, 10, 9).toDate()),
                new Game("Belgium", 2, "Turkey", 2, LocalDate.of(2023, 10, 10).toDate()),
                new Game("South Afrika", 2, "Japan", 2, LocalDate.of(2023, 10, 9).toDate()),
                new Game("Finland", 2, "New Zeland", 3, LocalDate.of(2023, 10, 8).toDate()),
        )
        def expectedSummary = List.of(
                new Game("Finland", 2, "New Zeland", 3, LocalDate.of(2023, 10, 8).toDate()),
                new Game("France", 2, "Italy", 3, LocalDate.of(2023, 10, 9).toDate()),
                new Game("South Afrika", 2, "Japan", 2, LocalDate.of(2023, 10, 9).toDate()),
                new Game("Belgium", 2, "Turkey", 2, LocalDate.of(2023, 10, 10).toDate()),
                new Game("South Korea", 1, "Germany", 1, LocalDate.of(2023, 10, 9).toDate()),
                new Game("Greece", 1, "England", 1, LocalDate.of(2023, 10, 10).toDate()),
                new Game("Poland", 1, "Spain", 0, LocalDate.of(2023, 10, 9).toDate()),
                new Game("Ireland", 0, "Ukraine", 0, LocalDate.of(2023, 10, 9).toDate()),
        )

        when:
        games.forEach(scoreBoardService::startGame)
        def actualSummary = scoreBoardService.summary

        then:
        actualSummary == expectedSummary
    }
}
