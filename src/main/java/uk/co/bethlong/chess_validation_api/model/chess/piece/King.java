package uk.co.bethlong.chess_validation_api.model.chess.piece;

import uk.co.bethlong.chess_validation_api.model.chess.Spot;

public class King extends Piece {

    public King(boolean isWhite) {
        super(isWhite, PieceType.KING);
    }

    @Override
    public boolean canMove(Spot originalSpot, Spot newSpot) {
        return Math.abs(originalSpot.getXColumn() - newSpot.getXColumn()) <= 1 && Math.abs(originalSpot.getYRow() - newSpot.getYRow()) <= 1;
    }
}
