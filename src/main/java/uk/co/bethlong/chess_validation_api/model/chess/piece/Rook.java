package uk.co.bethlong.chess_validation_api.model.chess.piece;

import uk.co.bethlong.chess_validation_api.model.chess.Spot;

public class Rook extends Piece {

    public Rook(boolean isWhite) {
        super(isWhite, PieceType.ROOK);
    }

    @Override
    public boolean canMove(Spot originalSpot, Spot newSpot) {
        // TODO
        return true;
    }
}
