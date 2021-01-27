package uk.co.bethlong.chess_validation_api.model.game.reversi

import spock.lang.Specification
import spock.lang.Unroll
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.PlaceRequestRepository
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiGame
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiGameRepository
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.SpotRepository

class ReversiGameServiceSpec extends Specification {

    PlaceRequestRepository placeRequestRepository
    ReversiGameRepository reversiGameRepository
    SpotRepository spotRepository
    ReversiPlayerService reversiPlayerService

    ReversiGameService reversiGameService

    def setup()
    {
        placeRequestRepository = Mock(PlaceRequestRepository)

        reversiGameRepository = Mock(ReversiGameRepository)

        spotRepository = Mock(SpotRepository)

        reversiPlayerService = Mock(ReversiPlayerService)

        reversiGameService = new ReversiGameService(placeRequestRepository, reversiGameRepository, reversiPlayerService, spotRepository)
    }

    @Unroll("Given a player name '#playerName' and colour isRed=#isRed, a new game should be created")
    def "createNewGame: Given a player name and colour, a new game should be created"()
    {
        when:
            ReversiGame result = reversiGameService.createNewGame(playerName, isRed)
        then:
            1 * reversiGameRepository.save(*_) >> { ReversiGame reversiGame ->
                assert reversiGame.getGameManagementStatus() == GameManagementStatus.WAITING_SECOND_PLAYER_TO_JOIN
                assert reversiGame.getVictoryStatus() == VictoryStatus.NONE
                assert reversiGame.getGameUid() != null
            }
            1 * reversiPlayerService.registerPlayer(*_)
            result.getGameUid() != null
        where:
            playerName | isRed
            "testA" | true
            "testB" | true
    }
}
