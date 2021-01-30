package uk.co.bethlong.chess_validation_api.model.game.reversi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiGame;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.Spot;

import java.util.Optional;

@Service
public class ReversiGameLogicService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReversiGameLogicService.class);

    public Optional<Spot> getPossibleSpotInDirection(Spot[][] spotGrid, boolean isStartSpot, Spot startingSpot, boolean targetColour, int xModifier, int yModifier) {
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

    public void flipSpotsInDirection(Spot[][] spotGrid, Spot startingSpot, boolean targetColour, int xModifier, int yModifier) {
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

    public VictoryStatus declareWinner(ReversiGame reversiGame) {
        if (reversiGame.getTotalBluePieces() > reversiGame.getTotalRedPieces()) {
            return VictoryStatus.BLUE_VICTORY;
        }

        if (reversiGame.getTotalRedPieces() > reversiGame.getTotalBluePieces()) {
            return VictoryStatus.RED_VICTORY;
        }

        return VictoryStatus.DRAW;
    }
}
