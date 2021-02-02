package uk.co.bethlong.chess_validation_api.model.game.reversi

import spock.lang.Specification
import spock.lang.Unroll
import uk.co.bethlong.chess_validation_api.controller.error.InvalidGameUidException
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiGame
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiGameRepository
import uk.co.bethlong.chess_validation_api.model.game.reversi.exception.InvalidGameManagementStatusStateException

class ReversiGameServiceSpec extends Specification {
    static String EXAMPLE_GAME_UID = "test-game-uid"

    ReversiGameRepository reversiGameRepository
    ReversiGame reversiGame

    ReversiGameService reversiGameService

    def setup()
    {
        reversiGameRepository = Mock(ReversiGameRepository)
        reversiGame = Mock(ReversiGame)

        reversiGameService = new ReversiGameService(reversiGameRepository)
    }

    def "Given a valid game UID, the findGame method should return the correctly found game"()
    {
        when:
            ReversiGame result = reversiGameService.findGame(EXAMPLE_GAME_UID)
        then:
            1 * reversiGameRepository.findById(EXAMPLE_GAME_UID) >> Optional.of(reversiGame)
            result == reversiGame
            notThrown(InvalidGameUidException)
    }

    def "Given an invalid game UID, the findGame method should throw an exception"()
    {
        when:
            reversiGameService.findGame(EXAMPLE_GAME_UID)
        then:
            1 * reversiGameRepository.findById(EXAMPLE_GAME_UID) >> Optional.empty()
            thrown(InvalidGameUidException)
    }

    @Unroll("Given game status '#gameStatus' that is not in the possible list of '#possibleGameStatuses', the checkGameStatus method should throw an exception")
    def "Given a game status that is not in the possible list, the checkGameStatus method should throw an exception"()
    {
        given:
            reversiGame.getGameManagementStatus() >> gameStatus
        when:
            reversiGameService.checkGameStatus(reversiGame, possibleGameStatuses.toArray(new GameManagementStatus[possibleGameStatuses.size()]))
        then:
            thrown(InvalidGameManagementStatusStateException)
        where:
            gameStatus | possibleGameStatuses
            GameManagementStatus.NONE | [GameManagementStatus.WAITING_SECOND_PLAYER_TO_JOIN, GameManagementStatus.WAITING_FOR_READY_UP_BOTH]
            GameManagementStatus.GAME_ENDED_NO_SPOTS_LEFT | [GameManagementStatus.WAITING_SECOND_PLAYER_TO_JOIN, GameManagementStatus.WAITING_FOR_READY_UP_BOTH]
            GameManagementStatus.GAME_ENDED_WITH_ERROR | [GameManagementStatus.WAITING_SECOND_PLAYER_TO_JOIN, GameManagementStatus.WAITING_FOR_READY_UP_BOTH]
            GameManagementStatus.GAME_ENDED_WITH_ERROR | [GameManagementStatus.WAITING_SECOND_PLAYER_TO_JOIN]
            GameManagementStatus.GAME_ENDED_WITH_ERROR | [GameManagementStatus.WAITING_SECOND_PLAYER_TO_JOIN, GameManagementStatus.GAME_ENDED_NO_SPOTS_LEFT, GameManagementStatus.WAITING_FOR_READY_UP_RED, GameManagementStatus.WAITING_RED_TURN]
    }
}
