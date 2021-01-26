package uk.co.bethlong.chess_validation_api.model.database.game.chess.piece;

import uk.co.bethlong.chess_validation_api.model.database.game.chess.Spot;

public class Pawn extends Piece {

    public Pawn(boolean isWhite) {
        super(isWhite, PieceType.PAWN);
    }

    @Override
    public boolean canMove(Spot originalSpot, Spot newSpot) {
        // TODO
        return true;
    }
}
