package uk.co.bethlong.chess_validation_api.model.game.reversi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.*;
import uk.co.bethlong.chess_validation_api.model.game.InvalidPlayerMoveException;
import uk.co.bethlong.chess_validation_api.model.game.reversi.exception.InvalidGameStateException;

import java.util.List;
import java.util.Optional;

@Service
public class ReversiGamePlayService {
    private final Logger LOGGER = LoggerFactory.getLogger(ReversiGamePlayService.class);

    private final PlaceRequestRepository placeRequestRepository;
    private final ReversiGameRepository reversiGameRepository;
    private final ReversiPlayerService reversiPlayerService;
    private final SpotRepository spotRepository;
    private final ReversiGameLogicService logicService;
    private final ReversiGameService reversiGameService;

    public ReversiGamePlayService(PlaceRequestRepository placeRequestRepository,
                                  ReversiGameRepository reversiGameRepository,
                                  ReversiPlayerService reversiPlayerService,
                                  SpotRepository spotRepository,
                                  ReversiGameLogicService logicService,
                                  ReversiGameService reversiGameService
    ) {
        this.placeRequestRepository = placeRequestRepository;
        this.reversiGameRepository = reversiGameRepository;
        this.reversiPlayerService = reversiPlayerService;
        this.spotRepository = spotRepository;
        this.logicService = logicService;
        this.reversiGameService = reversiGameService;
    }

    public void skipTurn(String gameUid, String playerUid) {
        ReversiGame reversiGame = reversiGameService.findGame(gameUid);

        reversiGameService.checkGameStatus(reversiGame, GameManagementStatus.WAITING_BLUE_TURN, GameManagementStatus.WAITING_RED_TURN);

        ReversiPlayer player = reversiPlayerService.getPlayerInGame(reversiGame, playerUid);

        PlaceRequest placeRequest = new PlaceRequest();
        placeRequest.setPlayer(player);
        placeRequest.setReversiGame(reversiGame);
        placeRequest.setSkip(true);

        logicService.checkCorrectPlayerForTurn(reversiGame, player);

        placeRequestRepository.save(placeRequest);

        if (reversiGame.isTurn(true))
            reversiGame.setGameManagementStatus(GameManagementStatus.WAITING_BLUE_TURN);
        else if (reversiGame.isTurn(false))
            reversiGame.setGameManagementStatus(GameManagementStatus.WAITING_RED_TURN);

        reversiGameRepository.save(reversiGame);
    }

    public void makePlacement(String gameUid, String playerUid, Integer xColumn, Integer yRow)
            throws InvalidPlayerMoveException {
        ReversiGame reversiGame = reversiGameService.findGame(gameUid);

        reversiGameService.checkGameStatus(reversiGame, GameManagementStatus.WAITING_BLUE_TURN, GameManagementStatus.WAITING_RED_TURN);

        ReversiPlayer player = reversiPlayerService.getPlayerInGame(reversiGame, playerUid);

        PlaceRequest placeRequest = new PlaceRequest();
        placeRequest.setPlayer(player);
        placeRequest.setXColumn(xColumn);
        placeRequest.setYRow(yRow);
        placeRequest.setReversiGame(reversiGame);

        logicService.checkCorrectPlayerForTurn(reversiGame, player);

        // Build Board Representation: Spot Grid
        List<Spot> spotList = spotRepository.findByReversiGame(reversiGame);

        if (spotList.size() != (reversiGame.getxColumnCount() * reversiGame.getyRowCount()))
            throw new InvalidGameStateException("Invalid total number of stored spots: expected " + (reversiGame.getxColumnCount() * reversiGame.getyRowCount()) + ", found " + spotList.size());

        Spot targetSpot = null;
        Spot[][] spotGrid = new Spot[reversiGame.getxColumnCount()][reversiGame.getyRowCount()];
        for (Spot spot : spotList) {
            spotGrid[spot.getXColumn() - 1][spot.getYRow() - 1] = spot;

            if (placeRequest.isForSpot(spot)) {
                targetSpot = spot;
            }
        }

        // Validate Move
        if (targetSpot == null)
            throw new InvalidPlayerMoveException("Failed to find spot requested");

        if (targetSpot.hasPiece())
            throw new InvalidPlayerMoveException("Piece already exists in spot (" + placeRequest.getXColumn()
                    + ", " + placeRequest.getYRow() + ")");

        Optional<Spot> lesserXEqualYOptional = logicService.getPossibleSpotInDirection(spotGrid, true, targetSpot, player.isRed(), -1, 0);
        Optional<Spot> greaterXEqualYOptional = logicService.getPossibleSpotInDirection(spotGrid, true, targetSpot, player.isRed(), 1, 0);
        Optional<Spot> equalXLesserYOptional = logicService.getPossibleSpotInDirection(spotGrid, true, targetSpot, player.isRed(), 0, -1);
        Optional<Spot> equalXGreaterYOptional = logicService.getPossibleSpotInDirection(spotGrid, true, targetSpot, player.isRed(), 0, 1);
        Optional<Spot> lesserXLesserYOptional = logicService.getPossibleSpotInDirection(spotGrid, true, targetSpot, player.isRed(), -1, -1);
        Optional<Spot> greaterXLesserYOptional = logicService.getPossibleSpotInDirection(spotGrid, true, targetSpot, player.isRed(), 1, -1);
        Optional<Spot> lesserXGreaterYOptional = logicService.getPossibleSpotInDirection(spotGrid, true, targetSpot, player.isRed(), -1, 1);
        Optional<Spot> greaterXGreaterYOptional = logicService.getPossibleSpotInDirection(spotGrid, true, targetSpot, player.isRed(), 1, 1);

        boolean atLeastOneMatch = lesserXEqualYOptional.isPresent() || greaterXEqualYOptional.isPresent()
                || equalXLesserYOptional.isPresent() || equalXGreaterYOptional.isPresent()
                || lesserXLesserYOptional.isPresent() || greaterXLesserYOptional.isPresent()
                || lesserXGreaterYOptional.isPresent() || greaterXGreaterYOptional.isPresent();
        if (!atLeastOneMatch)
            throw new InvalidPlayerMoveException("Not a valid move: no matching pieces of same colour in any direction");

        // Place piece
        targetSpot.setHasPiece(true);
        targetSpot.setIsRedPiece(player.isRed());

        // Flip Other Pieces
        if (lesserXEqualYOptional.isPresent()) logicService.flipSpotsInDirection(spotGrid, targetSpot, player.isRed(), -1, 0);
        if (greaterXEqualYOptional.isPresent()) logicService.flipSpotsInDirection(spotGrid, targetSpot, player.isRed(), 1, 0);
        if (equalXLesserYOptional.isPresent()) logicService.flipSpotsInDirection(spotGrid, targetSpot, player.isRed(), 0, -1);
        if (equalXGreaterYOptional.isPresent()) logicService.flipSpotsInDirection(spotGrid, targetSpot, player.isRed(), 0, 1);
        if (lesserXLesserYOptional.isPresent()) logicService.flipSpotsInDirection(spotGrid, targetSpot, player.isRed(), -1, -1);
        if (greaterXLesserYOptional.isPresent()) logicService.flipSpotsInDirection(spotGrid, targetSpot, player.isRed(), 1, -1);
        if (lesserXGreaterYOptional.isPresent()) logicService.flipSpotsInDirection(spotGrid, targetSpot, player.isRed(), -1, 1);
        if (greaterXGreaterYOptional.isPresent()) logicService.flipSpotsInDirection(spotGrid, targetSpot, player.isRed(), 1, 1);

        // Total New Scores
        logicService.totalScores(reversiGame, spotList);

        // Save entities to DB
        spotRepository.saveAll(spotList);
        placeRequestRepository.save(placeRequest);

        // Figure out if game has ended
        boolean hasSpotsLeft = false;
        for (Spot spot : spotList) {
            if (!spot.hasPiece()) {
                hasSpotsLeft = true;
                break;
            }
        }

        // If game has ended
        if (!hasSpotsLeft) {
            logicService.declareWinnerFromNoPossibleMoves(reversiGame, spotList);

            reversiGame.setGameManagementStatus(GameManagementStatus.GAME_ENDED_NO_SPOTS_LEFT);
        }
        else
        {
            if (reversiGame.isTurn(true))
                reversiGame.setGameManagementStatus(GameManagementStatus.WAITING_BLUE_TURN);
            else if (reversiGame.isTurn(false))
                reversiGame.setGameManagementStatus(GameManagementStatus.WAITING_RED_TURN);
        }

        reversiGameRepository.save(reversiGame);
    }
}
