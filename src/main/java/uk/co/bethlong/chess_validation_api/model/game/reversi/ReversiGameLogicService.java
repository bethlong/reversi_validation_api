package uk.co.bethlong.chess_validation_api.model.game.reversi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiGame;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiPlayer;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.Spot;
import uk.co.bethlong.chess_validation_api.model.game.InvalidPlayerMoveRequestException;

import java.util.List;
import java.util.Optional;

@Service
public class ReversiGameLogicService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReversiGameLogicService.class);

    public Optional<Spot> getPossibleSpotInDirection(Spot[][] spotGrid, boolean isStartSpot, Spot startingSpot, boolean targetColour, int xModifier, int yModifier) {
        int nextX = startingSpot.getXColumn() - 1 + xModifier;
        int nextY = startingSpot.getYRow() - 1 + yModifier;

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

    public void flipSpotsInDirection(Spot[][] spotGrid, Spot startingSpot, boolean targetColour, int xModifier, int yModifier) {
        int nextX = startingSpot.getXColumn() - 1 + xModifier;
        int nextY = startingSpot.getYRow() - 1 + yModifier;

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

    public void declareWinnerFromTimerExpiry(ReversiGame reversiGame, List<Spot> spotList, boolean isRedPlayerWhoseTimerExpire) {
        VictoryStatus victoryStatus = isRedPlayerWhoseTimerExpire ? VictoryStatus.BLUE_VICTORY : VictoryStatus.RED_VICTORY;

        int redScore = reversiGame.getTotalRedPieces();
        int blueScore = reversiGame.getTotalBluePieces();

        int emptyPieces = 0;
        for (Spot spot : spotList) {
            if (!spot.hasPiece()) emptyPieces++;
        }

        if (victoryStatus.equals(VictoryStatus.RED_VICTORY))
            redScore += emptyPieces;
        else
            blueScore += emptyPieces;

        if (victoryStatus.equals(VictoryStatus.RED_VICTORY) && redScore <= blueScore) {
            redScore = 33;
            blueScore = 31;
        } else if (victoryStatus.equals(VictoryStatus.BLUE_VICTORY) && blueScore <= redScore) {
            redScore = 31;
            blueScore = 33;
        }

        reversiGame.setTotalRedVictoryPoints(redScore);
        reversiGame.setTotalBlueVictoryPoints(blueScore);
        reversiGame.setVictoryStatus(victoryStatus);
    }

    public void declareWinnerFromNoPossibleMoves(ReversiGame reversiGame, List<Spot> spotList) {
        int emptyPieces = 0;
        for (Spot spot : spotList) {
            if (!spot.hasPiece()) emptyPieces++;
        }

        int redScore = reversiGame.getTotalRedPieces();
        int blueScore = reversiGame.getTotalBluePieces();

        VictoryStatus victoryStatus;
        if (redScore > blueScore) {
            redScore += emptyPieces;
            victoryStatus = VictoryStatus.RED_VICTORY;
        } else if (redScore < blueScore) {
            blueScore += emptyPieces;
            victoryStatus = VictoryStatus.BLUE_VICTORY;
        } else {
            redScore = 32;
            blueScore = 32;
            victoryStatus = VictoryStatus.DRAW;
        }

        reversiGame.setTotalRedVictoryPoints(redScore);
        reversiGame.setTotalBlueVictoryPoints(blueScore);
        reversiGame.setVictoryStatus(victoryStatus);
    }

    public void checkCorrectPlayerForTurn(ReversiGame reversiGame, ReversiPlayer player) {
        if (reversiGame.isTurn(true) && !player.isRed()) {
            throw new InvalidPlayerMoveRequestException("Move was requested by BLUE player '" + player.getPlayerName() + "' which should be waiting.");
        }

        if (reversiGame.isTurn(false) && player.isRed()) {
            throw new InvalidPlayerMoveRequestException("Move was requested by RED player '" + player.getPlayerName() + "' which should be waiting.");
        }
    }

    public void totalScores(ReversiGame reversiGame, List<Spot> spotList) {
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
    }
}
