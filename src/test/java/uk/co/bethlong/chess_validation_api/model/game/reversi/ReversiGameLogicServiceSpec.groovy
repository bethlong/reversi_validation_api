package uk.co.bethlong.chess_validation_api.model.game.reversi

import spock.lang.Specification
import spock.lang.Unroll
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiGame
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiPlayer
import uk.co.bethlong.chess_validation_api.model.game.InvalidPlayerMoveRequestException

class ReversiGameLogicServiceSpec extends Specification {

    ReversiGame reversiGame
    ReversiPlayer reversiPlayer

    ReversiGameLogicService reversiGameLogicService

    def setup()
    {
        reversiGame = Mock(ReversiGame)
        reversiPlayer = Mock(ReversiPlayer)

        reversiGameLogicService = new ReversiGameLogicService()
    }

    @Unroll("checkCorrectPlayerForTurn: When the correct player isRed=#isRedPlayer is passed for the game turn isRedTurn=#isRedTurn, no exception is thrown")
    def "checkCorrectPlayerForTurn: When the correct player is passed for the game status, no exception is thrown"()
    {
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
    def "checkCorrectPlayerForTurn: When the correct player is passed for the game status, an exception is thrown"()
    {
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


//    List<Spot> fullSpotList = new ArrayList<>()
//    for (int i = 0; i < boardSetup.size(); i++)
//    {
//        for (int k = 0; k < boardSetup.get(i).size(); k++)
//        {
//            Spot spot = Mock(Spot)
//            spot.getXColumn() >> i + 1
//            spot.getYRow() >> k + 1
//            spot.hasPiece() >> (boardSetup.get(i).get(k) > 0)
//            spot.isRedPiece() >> (boardSetup.get(i).get(k) == 1)
//
//            fullSpotList.add(spot)
//        }
//    }
//    placementX | placementY | boardSetup
//    1          | 2          | [[0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0]]
}
