package uk.co.bethlong.chess_validation_api.model.game.reversi

import spock.lang.Specification
import spock.lang.Unroll
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiGame
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiPlayer
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.Spot
import uk.co.bethlong.chess_validation_api.model.game.InvalidPlayerMoveRequestException

class ReversiGameLogicServiceSpec extends Specification {

    ReversiGame reversiGame
    ReversiPlayer reversiPlayer


    ReversiGameLogicService reversiGameLogicService

    def setup() {

        reversiGame = Mock(ReversiGame)
        reversiPlayer = Mock(ReversiPlayer)

        reversiGameLogicService = new ReversiGameLogicService()
    }

    @Unroll("checkCorrectPlayerForTurn: When the correct player isRed=#isRedPlayer is passed for the game turn isRedTurn=#isRedTurn, no exception is thrown")
    def "checkCorrectPlayerForTurn: When the correct player is passed for the game status, no exception is thrown"() {
        given:
            reversiGame.isTurn(true) >> isRedTurn
            reversiGame.isTurn(false) >> !isRedTurn
            reversiPlayer.isRed() >> isRedPlayer
        when:
            reversiGameLogicService.checkCorrectPlayerForTurn(reversiGame, reversiPlayer)
        then:
            notThrown(InvalidPlayerMoveRequestException)
        where:
            isRedPlayer | isRedTurn
            true        | true
            false       | false
    }

    @Unroll("checkCorrectPlayerForTurn: When the player isRed=#isRedPlayer is passed for the game turn isRedTurn=#isRedTurn, an exception is thrown")
    def "checkCorrectPlayerForTurn: When the correct player is passed for the game status, an exception is thrown"() {
        given:
            reversiGame.isTurn(true) >> isRedTurn
            reversiGame.isTurn(false) >> !isRedTurn
            reversiPlayer.isRed() >> isRedPlayer
        when:
            reversiGameLogicService.checkCorrectPlayerForTurn(reversiGame, reversiPlayer)
        then:
            thrown(InvalidPlayerMoveRequestException)
        where:
            isRedPlayer | isRedTurn
            true        | false
            false       | true
    }

    @Unroll("getPossibleSpotInDirection: Given a spot grid using \"#description\" config, a starting spot of (#placementX,#placementY), and a direction of (#xModifier,#yModifier), the end spot is (#expectedSpotFoundX,#expectedSpotFoundY)")
    def "getPossibleSpotInDirection: Given a spot grid, a starting spot, and a direction, the correct end spot is returned"() {
        given:
            Spot testStartingSpot = new Spot()
            testStartingSpot.setXColumn(placementX)
            testStartingSpot.setYRow(placementY)
            Spot[][] spotGrid = boardSetup
        when:
            Optional<Spot> result = reversiGameLogicService.getPossibleSpotInDirection(spotGrid, true, testStartingSpot, true, xModifier, yModifier)
        then:
            if (result.isPresent()) {
                assert result.get().getXColumn() == expectedSpotFoundX
                assert result.get().getYRow() == expectedSpotFoundY
            }
            result.isPresent() == (expectedSpotFoundX != -1 && expectedSpotFoundY != -1)
        where:
            placementX | placementY | xModifier | yModifier | boardSetup                                                                  | description   | expectedSpotFoundX | expectedSpotFoundY
            2          | 2          | 1         | 0         | new MockBoardCreator().mock(8, 8).create()                                  | "blank board" | -1                 | -1
            2          | 2          | 0         | 1         | new MockBoardCreator().mock(8, 8).create()                                  | "blank board" | -1                 | -1
            2          | 2          | -1        | 0         | new MockBoardCreator().mock(8, 8).create()                                  | "blank board" | -1                 | -1
            2          | 2          | 0         | -1        | new MockBoardCreator().mock(8, 8).create()                                  | "blank board" | -1                 | -1
            2          | 2          | 1         | -1        | new MockBoardCreator().mock(8, 8).create()                                  | "blank board" | -1                 | -1
            2          | 2          | -1        | 1         | new MockBoardCreator().mock(8, 8).create()                                  | "blank board" | -1                 | -1
            2          | 2          | -1        | 1         | new MockBoardCreator().mock(8, 8).create()                                  | "blank board" | -1                 | -1
            2          | 2          | 1         | -1        | new MockBoardCreator().mock(8, 8).create()                                  | "blank board" | -1                 | -1
            4          | 4          | 1         | 0         | new MockBoardCreator().mock(8, 8).create()                                  | "blank board" | -1                 | -1
            4          | 4          | 0         | 1         | new MockBoardCreator().mock(8, 8).create()                                  | "blank board" | -1                 | -1
            4          | 4          | -1        | 0         | new MockBoardCreator().mock(8, 8).create()                                  | "blank board" | -1                 | -1
            4          | 4          | 0         | -1        | new MockBoardCreator().mock(8, 8).create()                                  | "blank board" | -1                 | -1
            4          | 4          | 1         | -1        | new MockBoardCreator().mock(8, 8).create()                                  | "blank board" | -1                 | -1
            4          | 4          | -1        | 1         | new MockBoardCreator().mock(8, 8).create()                                  | "blank board" | -1                 | -1
            4          | 4          | -1        | 1         | new MockBoardCreator().mock(8, 8).create()                                  | "blank board" | -1                 | -1
            4          | 4          | 1         | -1        | new MockBoardCreator().mock(8, 8).create()                                  | "blank board" | -1                 | -1
            4          | 3          | 0         | 1         | new MockBoardCreator().mock(8, 8).putB(4, 4).putR(4, 6).create()            | "vertical"    | -1                 | -1
            4          | 3          | 0         | 1         | new MockBoardCreator().mock(8, 8).putB(4, 4).putR(4, 5).create()            | "vertical"    | 4                  | 5
            4          | 3          | 1         | 0         | new MockBoardCreator().mock(8, 8).putB(4, 4).putR(4, 5).create()            | "vertical"    | -1                 | -1
            3          | 4          | 1         | 0         | new MockBoardCreator().mock(8, 8).putB(4, 4).putR(6, 4).create()            | "horizontal"  | -1                 | -1
            3          | 4          | 1         | 0         | new MockBoardCreator().mock(8, 8).putB(4, 4).putR(5, 5).putR(6, 4).create() | "horizontal"  | -1                 | -1
            3          | 4          | 1         | 0         | new MockBoardCreator().mock(8, 8).putB(4, 4).putR(5, 4).create()            | "horizontal"  | 5                  | 4
            3          | 4          | 0         | 1         | new MockBoardCreator().mock(8, 8).putB(4, 4).putR(5, 4).create()            | "horizontal"  | -1                 | -1
            4          | 4          | 1         | 1         | new MockBoardCreator().mock(8, 8).putB(5, 5).putR(6, 6).create()            | "diagonal"    | 6                  | 6
            4          | 4          | -1        | -1        | new MockBoardCreator().mock(8, 8).putB(3, 3).putR(2, 2).create()            | "diagonal"    | 2                  | 2
            4          | 4          | 1         | -1        | new MockBoardCreator().mock(8, 8).putB(5, 3).putR(6, 2).create()            | "diagonal"    | 6                  | 2
            4          | 4          | -1        | 1         | new MockBoardCreator().mock(8, 8).putB(3, 5).putR(2, 6).create()            | "diagonal"    | 2                  | 6
    }
}
