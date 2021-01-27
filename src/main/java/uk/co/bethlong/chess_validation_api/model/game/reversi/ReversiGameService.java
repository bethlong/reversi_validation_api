package uk.co.bethlong.chess_validation_api.model.game.reversi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.co.bethlong.chess_validation_api.controller.error.InvalidGameUidException;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.*;
import uk.co.bethlong.chess_validation_api.model.game.InvalidPlayerMoveRequestException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReversiGameService {
    private final Logger LOGGER = LoggerFactory.getLogger(ReversiGameService.class);

    private final PlaceRequestRepository placeRequestRepository;
    private final ReversiGameRepository reversiGameRepository;
    private final ReversiPlayerService reversiPlayerService;
    private final SpotRepository spotRepository;

    public ReversiGameService(PlaceRequestRepository placeRequestRepository, ReversiGameRepository reversiGameRepository,
                              ReversiPlayerService reversiPlayerService, SpotRepository spotRepository) {
        this.placeRequestRepository = placeRequestRepository;
        this.reversiGameRepository = reversiGameRepository;
        this.reversiPlayerService = reversiPlayerService;
        this.spotRepository = spotRepository;
    }

    public ReversiGame createNewGame(String playerName, boolean isRed) {
        int xColumnCount = 8;
        int yRowCount = 8;

        if (playerName == null || playerName.isEmpty())
            throw new IllegalArgumentException("Invalid player name passed to method");

        ReversiGame reversiGame = new ReversiGame();
        reversiGame.setxColumnCount(xColumnCount);
        reversiGame.setyRowCount(yRowCount);
        reversiGame.setTotalRedPieces(2);
        reversiGame.setTotalBluePieces(2);
        reversiGame.setGameManagementStatus(GameManagementStatus.WAITING_SECOND_PLAYER_TO_JOIN);
        reversiGameRepository.save(reversiGame);

        List<Spot> spotList = new ArrayList<>();
        for (int i = 1; i <= xColumnCount; i++) {
            for (int k = 1; k <= yRowCount; k++) {
                Spot spot = new Spot();
                spot.setXColumn(i);
                spot.setYRow(k);
                spot.setReversiGame(reversiGame);

                if ((i == 4 && k == 4) || (i == 5 && k == 5)) {
                    spot.setHasPiece(true);
                    spot.setIsRedPiece(true);
                } else if ((i == 5 && k == 4) || (i == 4 && k == 5)) {
                    spot.setHasPiece(true);
                    spot.setIsRedPiece(false);
                }

                spotList.add(spot);
            }
        }
        spotRepository.saveAll(spotList);

        reversiPlayerService.registerPlayer(playerName, isRed, reversiGame);

        return reversiGame;
    }

    public ReversiGame registerOtherPlayer(String gameUid, String playerName, boolean isRed) {
        if (playerName == null || playerName.isEmpty())
            throw new IllegalArgumentException("Invalid player name passed to method");

        ReversiGame reversiGame = this.findGame(gameUid);

        checkGameStatus(reversiGame,
                GameManagementStatus.WAITING_SECOND_PLAYER_TO_JOIN
        );

        Optional<ReversiPlayer> reversiPlayerOptional = reversiPlayerService.getPlayerInGame(reversiGame, isRed);

        if (reversiPlayerOptional.isPresent()) {
            throw new NoPlayerSlotAvailableException("No space for requested colour in game " + gameUid + "... player '" + playerName + "' cannot join!");
        }

        reversiPlayerService.registerPlayer(playerName, isRed, reversiGame);

        reversiGame.setGameManagementStatus(GameManagementStatus.WAITING_FOR_READY_UP_BOTH);
        reversiGameRepository.save(reversiGame);

        return reversiGame;
    }

    public ReversiGame readyUpPlayer(String gameUid, String playerUid) {

        ReversiGame reversiGame = this.findGame(gameUid);

        checkGameStatus(reversiGame,
                GameManagementStatus.WAITING_FOR_READY_UP_BLUE,
                GameManagementStatus.WAITING_FOR_READY_UP_BOTH,
                GameManagementStatus.WAITING_FOR_READY_UP_RED
        );

        ReversiPlayer player = reversiPlayerService.getPlayerInGame(reversiGame, playerUid);

        if (player.isRed() && reversiGame.getGameManagementStatus().equals(GameManagementStatus.WAITING_FOR_READY_UP_BOTH)) {
            reversiGame.setGameManagementStatus(GameManagementStatus.WAITING_FOR_READY_UP_BLUE);
        } else if (!player.isRed() && reversiGame.getGameManagementStatus().equals(GameManagementStatus.WAITING_FOR_READY_UP_BOTH)) {
            reversiGame.setGameManagementStatus(GameManagementStatus.WAITING_FOR_READY_UP_RED);
        } else if ((player.isRed() && reversiGame.getGameManagementStatus().equals(GameManagementStatus.WAITING_FOR_READY_UP_RED))
                || (!player.isRed() && reversiGame.getGameManagementStatus().equals(GameManagementStatus.WAITING_FOR_READY_UP_BLUE))) {
            reversiGame.setGameManagementStatus(GameManagementStatus.WAITING_RED_TURN);
        } else {
            throw new InvalidGameManagementStatusStateException("Player isRed=" + player.isRed() + " has already readied up for game '" + gameUid + "' but is trying to ready up again.");
        }

        reversiGameRepository.save(reversiGame);

        return reversiGame;
    }

    public ReversiGame findGame(String gameUid) {
        Optional<ReversiGame> reversiGameOptional = reversiGameRepository.findById(gameUid);
        if (reversiGameOptional.isEmpty())
            throw new InvalidGameUidException("GameUID '" + gameUid + "' is invalid.");

        return reversiGameOptional.get();
    }

    public void makePlacement(String gameUid, String playerUid, Integer xColumn, Integer yRow)
            throws InvalidPlayerMoveRequestException {
        ReversiGame reversiGame = findGame(gameUid);

        checkGameStatus(reversiGame, GameManagementStatus.WAITING_BLUE_TURN, GameManagementStatus.WAITING_RED_TURN);

        ReversiPlayer player = reversiPlayerService.getPlayerInGame(reversiGame, playerUid);

        PlaceRequest placeRequest = new PlaceRequest();
        placeRequest.setPlayer(player);
        placeRequest.setXColumn(xColumn);
        placeRequest.setYRow(yRow);
        placeRequest.setReversiGame(reversiGame);

        if (reversiGame.isTurn(true) && !player.isRed()) {
            throw new InvalidPlayerMoveRequestException("Move was requested by BLUE player '" + player.getPlayerName() + "' which should be waiting.");
        }

        if (reversiGame.isTurn(false) && player.isRed()) {
            throw new InvalidPlayerMoveRequestException("Move was requested by RED player '" + player.getPlayerName() + "' which should be waiting.");
        }

        List<Spot> spotList = spotRepository.findByReversiGame(reversiGame);
        Spot targetSpot = null;
        Spot[][] spotGrid = new Spot[reversiGame.getxColumnCount()][reversiGame.getyRowCount()];
        for (Spot spot : spotList) {
            spotGrid[spot.getXColumn()][spot.getYRow()] = spot;

            if (spot.getXColumn() == placeRequest.getXColumn() && spot.getYRow() == placeRequest.getYRow()) {
                targetSpot = spot;
            }
        }

        if (targetSpot == null)
            throw new InvalidPlayerMoveRequestException("Failed to find spot requested");

        if (targetSpot.hasPiece())
            throw new InvalidPlayerMoveRequestException("Piece already exists in spot (" + placeRequest.getXColumn()
                    + ", " + placeRequest.getYRow() + ")");


        Optional<Spot> lesserXEqualYOptional = getPossibleSpotInDirection(spotGrid, true, targetSpot, player.isRed(), -1, 0);
        Optional<Spot> greaterXEqualYOptional = getPossibleSpotInDirection(spotGrid, true, targetSpot, player.isRed(), 1, 0);
        Optional<Spot> equalXLesserYOptional = getPossibleSpotInDirection(spotGrid, true, targetSpot, player.isRed(), 0, -1);
        Optional<Spot> equalXGreaterYOptional = getPossibleSpotInDirection(spotGrid, true, targetSpot, player.isRed(), 0, 1);
        Optional<Spot> lesserXLesserYOptional = getPossibleSpotInDirection(spotGrid, true, targetSpot, player.isRed(), -1, -1);
        Optional<Spot> greaterXLesserYOptional = getPossibleSpotInDirection(spotGrid, true, targetSpot, player.isRed(), 1, -1);
        Optional<Spot> lesserXGreaterYOptional = getPossibleSpotInDirection(spotGrid, true, targetSpot, player.isRed(), -1, 1);
        Optional<Spot> greaterXGreaterYOptional = getPossibleSpotInDirection(spotGrid, true, targetSpot, player.isRed(), 1, 1);

        boolean atLeastOneMatch = lesserXEqualYOptional.isPresent() || greaterXEqualYOptional.isPresent()
                || equalXLesserYOptional.isPresent() || equalXGreaterYOptional.isPresent()
                || lesserXLesserYOptional.isPresent() || greaterXLesserYOptional.isPresent()
                || lesserXGreaterYOptional.isPresent() || greaterXGreaterYOptional.isPresent();
        if (!atLeastOneMatch)
            throw new InvalidPlayerMoveRequestException("Not a valid move: no matching pieces of same colour");

        targetSpot.setHasPiece(true);
        targetSpot.setIsRedPiece(player.isRed());

        if (lesserXEqualYOptional.isPresent()) flipSpotsInDirection(spotGrid, targetSpot, player.isRed(), -1, 0);
        if (greaterXEqualYOptional.isPresent()) flipSpotsInDirection(spotGrid, targetSpot, player.isRed(), 1, 0);
        if (equalXLesserYOptional.isPresent()) flipSpotsInDirection(spotGrid, targetSpot, player.isRed(), 0, -1);
        if (equalXGreaterYOptional.isPresent()) flipSpotsInDirection(spotGrid, targetSpot, player.isRed(), 0, 1);
        if (lesserXLesserYOptional.isPresent()) flipSpotsInDirection(spotGrid, targetSpot, player.isRed(), -1, -1);
        if (greaterXLesserYOptional.isPresent()) flipSpotsInDirection(spotGrid, targetSpot, player.isRed(), 1, -1);
        if (lesserXGreaterYOptional.isPresent()) flipSpotsInDirection(spotGrid, targetSpot, player.isRed(), -1, 1);
        if (greaterXGreaterYOptional.isPresent()) flipSpotsInDirection(spotGrid, targetSpot, player.isRed(), 1, 1);

        int blueCount = 0;
        int redCount = 0;
        for (Spot spot : spotList) {
            if (spot.hasPiece()) {
                if (spot.isRedPiece())
                    redCount++;
                else
                    blueCount++;
            }
        }
        reversiGame.setTotalRedPieces(redCount);
        reversiGame.setTotalBluePieces(blueCount);

        spotRepository.saveAll(spotList);
        placeRequestRepository.save(placeRequest);

        if (reversiGame.isTurn(true))
            reversiGame.setGameManagementStatus(GameManagementStatus.WAITING_BLUE_TURN);
        else if (reversiGame.isTurn(false))
            reversiGame.setGameManagementStatus(GameManagementStatus.WAITING_RED_TURN);

        reversiGameRepository.save(reversiGame);
    }

    private void checkGameStatus(ReversiGame reversiGame, GameManagementStatus... gameManagementStatuses) {
        for (GameManagementStatus gameManagementStatus : gameManagementStatuses) {
            if (reversiGame.getGameManagementStatus().equals(gameManagementStatus)) {
                return;
            }
        }

        throw new InvalidGameManagementStatusStateException(
                "Game Status for game '" + reversiGame.getGameUid()
                        + "' was '" + reversiGame.getGameManagementStatus() + "', but a game update only allowed for statuses '"
                        + Arrays.toString(gameManagementStatuses) + "' is being requested."
        );
    }

    private Optional<Spot> getPossibleSpotInDirection(Spot[][] spotGrid, boolean isStartSpot, Spot startingSpot, boolean targetColour, int xModifier, int yModifier) {
        int nextX = startingSpot.getXColumn() + xModifier;
        int nextY = startingSpot.getYRow() + yModifier;

        // Presumes grid has >1 columns
        if (nextX < 0 || nextX >= spotGrid.length || nextY < 0 || nextY >= spotGrid[0].length) {
            return Optional.empty();
        }

        Spot nextSpot;
        try {
            nextSpot = spotGrid[nextX][nextY];
        } catch (ArrayIndexOutOfBoundsException exception) {
            LOGGER.error("Edge of board reached without being caught!", exception);
            return Optional.empty();
        }

        boolean isNextSpotEmpty = !nextSpot.hasPiece();
        boolean isNextSpotSameColour = !isNextSpotEmpty && nextSpot.isRedPiece() == targetColour;

        if (isNextSpotEmpty && !isStartSpot) {
            return Optional.of(nextSpot);
        } else if (!isNextSpotSameColour) {
            return getPossibleSpotInDirection(spotGrid, false, nextSpot, targetColour, xModifier, yModifier);
        } else {
            return Optional.empty();
        }
    }

    private void flipSpotsInDirection(Spot[][] spotGrid, Spot startingSpot, boolean targetColour, int xModifier, int yModifier) {
        int nextX = startingSpot.getXColumn() + xModifier;
        int nextY = startingSpot.getYRow() + yModifier;

        // Presumes grid has >1 columns
        if (nextX < 0 || nextX >= spotGrid.length || nextY < 0 || nextY >= spotGrid[0].length) {
            throw new IllegalArgumentException("Asked for a flip in a direction that has no valid end!");
        }

        Spot nextSpot;
        try {
            nextSpot = spotGrid[nextX][nextY];
        } catch (ArrayIndexOutOfBoundsException exception) {
            throw new IllegalArgumentException("Asked for a flip in a direction that has no valid end! Went off board.");
        }

        if (nextSpot.hasPiece() && nextSpot.isRedPiece() != targetColour) {
            flipSpotsInDirection(spotGrid, nextSpot, targetColour, xModifier, yModifier);
        }
    }
}
