package uk.co.bethlong.chess_validation_api.model.database.game.chess.piece;

import uk.co.bethlong.chess_validation_api.model.database.game.chess.Spot;

public class Bishop extends Piece {

    public Bishop(boolean isWhite) {
        super(isWhite, PieceType.BISHOP);
    }

    @Override
    public boolean canMove(Spot originalSpot, Spot newSpot) {
        // TODO
        return true;
    }
}
