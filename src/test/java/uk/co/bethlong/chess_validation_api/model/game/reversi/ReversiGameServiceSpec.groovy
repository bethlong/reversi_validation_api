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
    ReversiGame exampleReversiGame
    SpotRepository spotRepository
    ReversiPlayerService reversiPlayerService
    ReversiGameLogicService reversiGameLogicService

    ReversiGameService reversiGameService

    def setup()
    {
        placeRequestRepository = Mock(PlaceRequestRepository)

        reversiGameRepository = Mock(ReversiGameRepository)
        exampleReversiGame = Mock(ReversiGame)

        spotRepository = Mock(SpotRepository)

        reversiPlayerService = Mock(ReversiPlayerService)

        reversiGameLogicService = Mock(ReversiGameLogicService)

        reversiGameService = new ReversiGameService(placeRequestRepository, reversiGameRepository, reversiPlayerService, spotRepository, reversiGameLogicService, 2)
    }

    @Unroll("Given a player name '#playerName' and colour isRed=#isRed, a new game should be created")
    def "createNewGame: Given a player name and colour, a new game should be created"()
    {
        when:
            ReversiGame result = reversiGameService.createNewGame(playerName, isRed)
        then:
            1 * reversiGameRepository.save(*_)
            1 * reversiPlayerService.registerPlayer(playerName, isRed, _)
            result != null
        where:
            playerName | isRed
            "testA" | true
            "testB" | false
    }

    def "createNewGame: Given valid parameters, a new game should be created with correct data"()
    {
        when:
            ReversiGame result = reversiGameService.createNewGame("TestA", true)
        then:
            result.getVictoryStatus() == VictoryStatus.NONE
            result.getGameManagementStatus() == GameManagementStatus.WAITING_SECOND_PLAYER_TO_JOIN
            result.getGameUid() != null
            !result.getGameUid().isEmpty()
            result.getDateCreated() != null
            result.getDateFinished() == null
            result.getTotalBluePieces() == 2
            result.getTotalRedPieces() == 2
    }

    def "createNewGame: Given an invalid player name, an IllegalArgumentException should be thrown"()
    {
        when:
            reversiGameService.createNewGame("", true)
        then:
            thrown(IllegalArgumentException)
    }

    def "createNewGame: Given a null player name, an IllegalArgumentException should be thrown"()
    {
        when:
            reversiGameService.createNewGame(null, true)
        then:
            thrown(IllegalArgumentException)
    }

    def "registerOtherPlayer: Given valid parameters, a player is registered successfully"()
    {
        given:
            exampleReversiGame.getGameManagementStatus() >> GameManagementStatus.WAITING_SECOND_PLAYER_TO_JOIN
        when:
            reversiGameService.registerOtherPlayer("gameUid", "playerName", true)
        then:
            1 * reversiGameRepository.findById("gameUid") >> Optional.of(exampleReversiGame)
            1 * reversiPlayerService.getPlayerInGame(*_) >> Optional.empty()
            1 * reversiPlayerService.registerPlayer("playerName", true, exampleReversiGame)
            1 * exampleReversiGame.setGameManagementStatus(GameManagementStatus.WAITING_FOR_READY_UP_BOTH)
            1 * reversiGameRepository.save(exampleReversiGame)
    }

    // TODO add tests for registerOtherPlayer boundaries
    // TODO add tests for readyUpPlayer
    // TODO add tests for findGame


}
