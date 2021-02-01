package uk.co.bethlong.chess_validation_api.model.game.reversi

import spock.lang.Specification
import spock.lang.Unroll
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiGame
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiGameRepository
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiPlayer
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.Spot
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.SpotRepository
import uk.co.bethlong.chess_validation_api.model.game.reversi.exception.InvalidGameManagementStatusStateException

class ReversiGameManagementServiceSpec extends Specification {

    static String DEFAULT_PLAYER_A_NAME = "TestA"
    static String DEFAULT_PLAYER_B_NAME = "TestB"
    static String DEFAULT_PLAYER_UID = "a-test-player-uid"
    static String DEFAULT_GAME_UID = "a-test-game-uid"

    ReversiGameRepository reversiGameRepository
    ReversiGame exampleReversiGame
    ReversiPlayer exampleReversiPlayer
    SpotRepository spotRepository
    ReversiPlayerService reversiPlayerService
    ReversiGameService reversiGameService

    ReversiGameManagementService reversiGameManagementService

    def setup()
    {
        reversiGameRepository = Mock(ReversiGameRepository)
        exampleReversiGame = Mock(ReversiGame)

        spotRepository = Mock(SpotRepository)

        reversiGameService = Mock(ReversiGameService)

        reversiPlayerService = Mock(ReversiPlayerService)
        exampleReversiPlayer = Mock(ReversiPlayer)

        reversiGameManagementService = new ReversiGameManagementService(reversiGameRepository, reversiPlayerService, spotRepository, reversiGameService)
    }

    @Unroll("Given a player name '#playerName' and colour isRed=#isRed, a new game should be created")
    def "createNewGame: Given a player name and colour, a new game should be created"()
    {
        when:
            ReversiGame result = reversiGameManagementService.createNewGame(playerName, isRed)
        then:
            1 * reversiGameRepository.save(*_)
            1 * reversiPlayerService.registerPlayer(playerName, isRed, _)
            result != null
        where:
            playerName | isRed
            "testA"    | true
            "testB"    | false
    }

    def "createNewGame: Given valid parameters, a new game should be created with correct data"()
    {
        when:
            ReversiGame result = reversiGameManagementService.createNewGame(DEFAULT_PLAYER_A_NAME, true)
        then:
            result.getVictoryStatus() == VictoryStatus.NONE
            result.getGameManagementStatus() == GameManagementStatus.WAITING_SECOND_PLAYER_TO_JOIN
            result.getGameUid() != null
            !result.getGameUid().isEmpty()
            result.getDateCreated() != null
            result.getDateFinished() == null
            result.getTotalBluePieces() == 2
            result.getTotalRedPieces() == 2
            result.getxColumnCount() == 8
            result.getyRowCount() == 8
    }

    def "createNewGame: Given valid parameters, the board should be populated with 64 spots with 4 pieces in the center 4 spots"()
    {
        when:
            reversiGameManagementService.createNewGame(DEFAULT_PLAYER_A_NAME, true)
        then:
            1 * spotRepository.saveAll(*_) >> { List<List<Spot>> argList ->
                List<Spot> spotList = argList.get(0);
                assert spotList.size() == 64
                for (Spot spotSaved : spotList) {
                    assert spotSaved.getXColumn() != -1
                    assert spotSaved.getYRow() != -1
                    if ((spotSaved.getXColumn() == 4 && spotSaved.getYRow() == 4) || (spotSaved.getXColumn() == 5 && spotSaved.getYRow() == 5)) {
                        assert spotSaved.hasPiece()
                        assert spotSaved.isRedPiece()
                    } else if ((spotSaved.getXColumn() == 4 && spotSaved.getYRow() == 5) || (spotSaved.getXColumn() == 5 && spotSaved.getYRow() == 4)) {
                        assert spotSaved.hasPiece()
                        assert !spotSaved.isRedPiece()
                    } else {
                        assert !spotSaved.hasPiece()
                    }
                }
            }
    }

    def "createNewGame: Given an invalid player name, an IllegalArgumentException should be thrown"()
    {
        when:
            reversiGameManagementService.createNewGame("", true)
        then:
            thrown(IllegalArgumentException)
    }

    def "createNewGame: Given a null player name, an IllegalArgumentException should be thrown"()
    {
        when:
            reversiGameManagementService.createNewGame(null, true)
        then:
            thrown(IllegalArgumentException)
    }

    def "registerOtherPlayer: Given valid parameters, a player is registered successfully"()
    {
        given:
            exampleReversiGame.getGameManagementStatus() >> GameManagementStatus.WAITING_SECOND_PLAYER_TO_JOIN
        when:
            reversiGameManagementService.registerOtherPlayer(DEFAULT_GAME_UID, DEFAULT_PLAYER_B_NAME, true)
        then:
            1 * reversiGameService.findGame(DEFAULT_GAME_UID) >> exampleReversiGame
            1 * reversiPlayerService.getPlayerInGame(*_) >> Optional.empty()
            1 * reversiPlayerService.registerPlayer(DEFAULT_PLAYER_B_NAME, true, exampleReversiGame)
            1 * exampleReversiGame.setGameManagementStatus(GameManagementStatus.WAITING_FOR_READY_UP_BOTH)
            1 * reversiGameRepository.save(exampleReversiGame)
    }

    @Unroll("readyUp: When isRed=#isRed player ready's up, the game state is correctly changed from #initialGameState to #expectedGameState")
    def "readyUp: When a player ready's up, the game state is correctly changed"()
    {
        given:
            exampleReversiGame.getGameManagementStatus() >> initialGameState
            reversiGameService.findGame(DEFAULT_GAME_UID) >> exampleReversiGame
            exampleReversiPlayer.isRed() >> isRed
            reversiPlayerService.getPlayerInGame(*_) >> exampleReversiPlayer
        when:
            ReversiGame result = reversiGameManagementService.readyUpPlayer(DEFAULT_GAME_UID, DEFAULT_PLAYER_UID)
        then:
            result == exampleReversiGame
            1 * exampleReversiGame.setGameManagementStatus(expectedGameState)
        where:
            isRed | initialGameState                               | expectedGameState
            true  | GameManagementStatus.WAITING_FOR_READY_UP_BOTH | GameManagementStatus.WAITING_FOR_READY_UP_BLUE
            false | GameManagementStatus.WAITING_FOR_READY_UP_BOTH | GameManagementStatus.WAITING_FOR_READY_UP_RED
            false | GameManagementStatus.WAITING_FOR_READY_UP_BLUE | GameManagementStatus.WAITING_BLUE_TURN
            true  | GameManagementStatus.WAITING_FOR_READY_UP_RED  | GameManagementStatus.WAITING_BLUE_TURN
    }

    @Unroll("readyUp: When isRed=#isRed player ready's up but is already ready'ed up, the game state is not changed and an exception is thrown")
    def "readyUp: When a player ready's up but is already ready'ed up, the game state is not changed and an exception is thrown"()
    {
        given:
            exampleReversiGame.getGameManagementStatus() >> initialGameState
            reversiGameService.findGame(DEFAULT_GAME_UID) >> exampleReversiGame
            exampleReversiPlayer.isRed() >> isRed
            reversiPlayerService.getPlayerInGame(*_) >> exampleReversiPlayer
        when:
            reversiGameManagementService.readyUpPlayer(DEFAULT_GAME_UID, DEFAULT_PLAYER_UID)
        then:
            0 * reversiGameRepository.save(*_)
            thrown(InvalidGameManagementStatusStateException)
        where:
            isRed | initialGameState                               | expectedGameState
            true  | GameManagementStatus.WAITING_FOR_READY_UP_BLUE | GameManagementStatus.WAITING_FOR_READY_UP_BLUE
            false | GameManagementStatus.WAITING_FOR_READY_UP_RED  | GameManagementStatus.WAITING_FOR_READY_UP_RED
    }
}
