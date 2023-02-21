package uk.co.bethlong.chess_validation_api.model.game.reversi

import spock.lang.Specification
import spock.lang.Unroll
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.PlaceRequest
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.PlaceRequestRepository
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiGame
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiGameRepository
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiPlayer
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.Spot
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.SpotRepository
import uk.co.bethlong.chess_validation_api.model.game.InvalidPlayerMoveException
import uk.co.bethlong.chess_validation_api.model.game.reversi.exception.InvalidGameStateException

class ReversiGamePlayServiceSpec extends Specification {
    static String DEFAULT_PLAYER_UID = "a-test-player-uid"
    static String DEFAULT_GAME_UID = "a-test-game-uid"
    static Integer DEFAULT_BOARD_X_COLUMN_SIZE = 8
    static Integer DEFAULT_BOARD_Y_ROW_SIZE = 8

    PlaceRequestRepository placeRequestRepository
    ReversiGameRepository reversiGameRepository
    ReversiGame reversiGame
    ReversiPlayerService reversiPlayerService
    ReversiPlayer reversiPlayer
    SpotRepository spotRepository
    ReversiGameLogicService logicService
    ReversiGameService reversiGameService

    ReversiGamePlayService reversiGamePlayService

    def setup() {
        placeRequestRepository = Mock(PlaceRequestRepository)
        reversiGameRepository = Mock(ReversiGameRepository)
        reversiGame = Mock(ReversiGame)
        reversiGame.getxColumnCount() >> DEFAULT_BOARD_X_COLUMN_SIZE
        reversiGame.getyRowCount() >> DEFAULT_BOARD_Y_ROW_SIZE
        reversiPlayerService = Mock(ReversiPlayerService)
        reversiPlayer = Mock(ReversiPlayer)
        spotRepository = Mock(SpotRepository)
        logicService = Mock(ReversiGameLogicService)
        reversiGameService = Mock(ReversiGameService)

        reversiGamePlayService = new ReversiGamePlayService(
                placeRequestRepository,
                reversiGameRepository,
                reversiPlayerService,
                spotRepository,
                logicService,
                reversiGameService
        )
    }

    def "skipTurn: Given a valid gameUid and playerUid, a place request should be saved with the correct details"()
    {
        given:
            reversiGame.isTurn(true) >> true
            reversiGame.isTurn(false) >> false
            reversiGameService.findGame(DEFAULT_GAME_UID) >> reversiGame
            reversiPlayerService.getPlayerInGame(reversiGame, DEFAULT_PLAYER_UID) >> reversiPlayer
        when:
            reversiGamePlayService.skipTurn(DEFAULT_GAME_UID, DEFAULT_PLAYER_UID)
        then:
            placeRequestRepository.save(*_) >> { PlaceRequest actualPlaceRequest ->
                assert actualPlaceRequest.getXColumn() == -1
                assert actualPlaceRequest.getYRow() == -1
                assert actualPlaceRequest.getPlayer() == reversiPlayer
                assert actualPlaceRequest.getReversiGame() == reversiGame
                assert actualPlaceRequest.isSkip()
            }
    }

    @Unroll("skipTurn: Given a valid gameUid and playerUid, the reversi game details should be updated with the new isRed=#isRedTurn turn")
    def "skipTurn: Given a valid gameUid and playerUid, the reversi game details should be updated with the new turn"()
    {
        given:
            reversiGame.isTurn(true) >> isRedTurn
            reversiGame.isTurn(false) >> !isRedTurn
            reversiGameService.findGame(DEFAULT_GAME_UID) >> reversiGame
            reversiPlayerService.getPlayerInGame(reversiGame, DEFAULT_PLAYER_UID) >> reversiPlayer
        when:
            reversiGamePlayService.skipTurn(DEFAULT_GAME_UID, DEFAULT_PLAYER_UID)
        then:
            1 * reversiGame.setGameManagementStatus(expectedGameManagementStatus)
            1 * reversiGameRepository.save(reversiGame)
        where:
            isRedTurn | expectedGameManagementStatus
            true      | GameManagementStatus.WAITING_BLUE_TURN
            false     | GameManagementStatus.WAITING_RED_TURN
    }

    def "makePlacement: Given an invalid board configuration without all 64 spots, the makePlacement method should throw an exception"()
    {
        given:
            reversiGameService.findGame(DEFAULT_GAME_UID) >> reversiGame
            reversiPlayerService.getPlayerInGame(reversiGame, DEFAULT_PLAYER_UID) >> reversiPlayer
            spotRepository.findByReversiGame(reversiGame) >> new ArrayList<Spot>()
        when:
            reversiGamePlayService.makePlacement(DEFAULT_GAME_UID, DEFAULT_PLAYER_UID, 1, 1)
        then:
            thrown(InvalidGameStateException)
    }

    def "makePlacement: Given an off-board target spot request, the makePlacement method should throw a InvalidPlayerMoveException"()
    {
        given:
            reversiGameService.findGame(DEFAULT_GAME_UID) >> reversiGame
            reversiPlayerService.getPlayerInGame(reversiGame, DEFAULT_PLAYER_UID) >> reversiPlayer
            List<Spot> fullSpotList = new ArrayList<>()
            for (int i = 1; i <= DEFAULT_BOARD_X_COLUMN_SIZE; i++)
            {
                for (int k = 1; k <= DEFAULT_BOARD_Y_ROW_SIZE; k++)
                {
                    Spot spot = Mock(Spot)
                    spot.getXColumn() >> i
                    spot.getYRow() >> k
                    spot.hasPiece() >> false
                    spot.isRedPiece() >> false

                    fullSpotList.add(spot)
                }
            }
            spotRepository.findByReversiGame(reversiGame) >> fullSpotList
        when:
            reversiGamePlayService.makePlacement(DEFAULT_GAME_UID, DEFAULT_PLAYER_UID, -1, -1)
        then:
            thrown(InvalidPlayerMoveException)
    }

    @Unroll("makePlacement: Given target spot request (#targetSpotX, #targetSpotY) where another piece is placed, the makePlacement method should throw a InvalidPlayerMoveException")
    def "makePlacement: Given a target spot request where another piece is placed, the makePlacement method should throw a InvalidPlayerMoveException"()
    {
        given:
            reversiGameService.findGame(DEFAULT_GAME_UID) >> reversiGame
            reversiPlayerService.getPlayerInGame(reversiGame, DEFAULT_PLAYER_UID) >> reversiPlayer

            List<Spot> fullSpotList = new ArrayList<>()
            for (int i = 1; i <= DEFAULT_BOARD_X_COLUMN_SIZE; i++)
            {
                for (int k = 1; k <= DEFAULT_BOARD_Y_ROW_SIZE; k++)
                {
                    Spot spot = Mock(Spot)
                    spot.getXColumn() >> i
                    spot.getYRow() >> k
                    spot.isRedPiece() >> false

                    if (i == targetSpotX && k == targetSpotY)
                        spot.hasPiece() >> true
                    else
                        spot.hasPiece() >> false

                    fullSpotList.add(spot)
                }
            }
            spotRepository.findByReversiGame(reversiGame) >> fullSpotList
        when:
            reversiGamePlayService.makePlacement(DEFAULT_GAME_UID, DEFAULT_PLAYER_UID, targetSpotX, targetSpotY)
        then:
            thrown(InvalidPlayerMoveException)
        where:
            targetSpotX | targetSpotY
            1           | 1
            1           | 2
            1           | 3
            1           | 4
            1           | 5
            1           | 6
            1           | 7
            1           | 8
            2           | 1
            2           | 2
            2           | 3
            2           | 4
            2           | 5
            2           | 6
            2           | 7
            2           | 8
            3           | 1
            3           | 2
            3           | 3
            3           | 4
            3           | 5
            3           | 6
            3           | 7
            3           | 8
            4           | 1
            4           | 2
            4           | 3
            4           | 4
            4           | 5
            4           | 6
            4           | 7
            4           | 8
            5           | 1
            5           | 2
            5           | 3
            5           | 4
            5           | 5
            5           | 6
            5           | 7
            5           | 8
            6           | 1
            6           | 2
            6           | 3
            6           | 4
            6           | 5
            6           | 6
            6           | 7
            6           | 8
            7           | 1
            7           | 2
            7           | 3
            7           | 4
            7           | 5
            7           | 6
            7           | 7
            7           | 8
            8           | 1
            8           | 2
            8           | 3
            8           | 4
            8           | 5
            8           | 6
            8           | 7
            8           | 8

    }

    @Unroll
    def "makePlacement: Given one or more of the directions finds a spot, a placement should be valid"()
    {
        given:
            List<Spot> fullSpotList = new ArrayList<>()
            for (int i = 1; i <= DEFAULT_BOARD_X_COLUMN_SIZE; i++)
            {
                for (int k = 1; k <= DEFAULT_BOARD_Y_ROW_SIZE; k++)
                {
                    Spot spot = Mock(Spot)
                    spot.getXColumn() >> i
                    spot.getYRow() >> k
                    spot.hasPiece() >> false
                    spot.isRedPiece() >> false

                    fullSpotList.add(spot)
                }
            }
            reversiPlayer.isRed() >> true
            reversiGameService.findGame(DEFAULT_GAME_UID) >> reversiGame
            reversiPlayerService.getPlayerInGame(reversiGame, DEFAULT_PLAYER_UID) >> reversiPlayer
            spotRepository.findByReversiGame(reversiGame) >> fullSpotList
        when:
            reversiGamePlayService.makePlacement(DEFAULT_GAME_UID, DEFAULT_PLAYER_UID, 1, 1)
        then:
            1 * logicService.getPossibleSpotInDirection((Spot[][]) _, true, (Spot) _, true, -1, 0) >> ((lesserXEqualYOptional) ? Optional.of(Mock(Spot)) : Optional.empty())
            1 * logicService.getPossibleSpotInDirection((Spot[][]) _, true, (Spot) _, true, 1, 0) >> ((greaterXEqualYOptional) ? Optional.of(Mock(Spot)) : Optional.empty())
            1 * logicService.getPossibleSpotInDirection((Spot[][]) _, true, (Spot) _, true, 0, -1) >> ((equalXLesserYOptional) ? Optional.of(Mock(Spot)) : Optional.empty())
            1 * logicService.getPossibleSpotInDirection((Spot[][]) _, true, (Spot) _, true, 0, 1) >> ((equalXGreaterYOptional) ? Optional.of(Mock(Spot)) : Optional.empty())
            1 * logicService.getPossibleSpotInDirection((Spot[][]) _, true, (Spot) _, true, -1, -1) >> ((lesserXLesserYOptional) ? Optional.of(Mock(Spot)) : Optional.empty())
            1 * logicService.getPossibleSpotInDirection((Spot[][]) _, true, (Spot) _, true, 1, -1) >> ((greaterXLesserYOptional) ? Optional.of(Mock(Spot)) : Optional.empty())
            1 * logicService.getPossibleSpotInDirection((Spot[][]) _, true, (Spot) _, true, -1, 1) >> ((lesserXGreaterYOptional) ? Optional.of(Mock(Spot)) : Optional.empty())
            1 * logicService.getPossibleSpotInDirection((Spot[][]) _, true, (Spot) _, true, 1, 1) >> ((greaterXGreaterYOptional) ? Optional.of(Mock(Spot)) : Optional.empty())
            notThrown(InvalidPlayerMoveException)
        where:
            lesserXEqualYOptional | greaterXEqualYOptional | equalXLesserYOptional | equalXGreaterYOptional | lesserXLesserYOptional | greaterXLesserYOptional | lesserXGreaterYOptional | greaterXGreaterYOptional
            true                  | true                   | true                  | true                   | true                   | true                    | true                    | true
            false                 | true                   | true                  | true                   | true                   | true                    | true                    | true
            true                  | false                  | true                  | true                   | true                   | true                    | true                    | true
            true                  | true                   | false                 | true                   | true                   | true                    | true                    | true
            true                  | true                   | true                  | false                  | true                   | true                    | true                    | true
            true                  | true                   | true                  | true                   | false                  | true                    | true                    | true
            true                  | true                   | true                  | true                   | true                   | false                   | true                    | true
            true                  | true                   | true                  | true                   | true                   | true                    | false                   | true
            true                  | true                   | true                  | true                   | true                   | true                    | true                    | false
            true                  | false                  | false                 | false                  | false                  | false                   | false                   | false
            false                 | true                   | false                 | false                  | false                  | false                   | false                   | false
            false                 | false                  | true                  | false                  | false                  | false                   | false                   | false
            false                 | false                  | false                 | true                   | false                  | false                   | false                   | false
            false                 | false                  | false                 | false                  | true                   | false                   | false                   | false
            false                 | false                  | false                 | false                  | false                  | true                    | false                   | false
            false                 | false                  | false                 | false                  | false                  | false                   | true                    | false
            false                 | false                  | false                 | false                  | false                  | false                   | false                   | true
    }
}
