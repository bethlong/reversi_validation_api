package uk.co.bethlong.chess_validation_api.model.chess.piece;

import uk.co.bethlong.chess_validation_api.model.chess.Spot;

public class Queen extends Piece {

    public Queen(boolean isWhite) {
        super(isWhite, PieceType.QUEEN);
    }

    @Override
    public boolean canMove(Spot originalSpot, Spot newSpot) {
        // TODO
        return true;
    }
}
