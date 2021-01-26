package uk.co.bethlong.chess_validation_api.model.database.game.chess.piece;

import uk.co.bethlong.chess_validation_api.model.database.game.chess.Spot;

public class Knight extends Piece {

    public Knight(boolean isWhite) {
        super(isWhite, PieceType.KNIGHT);
    }

    @Override
    public boolean canMove(Spot originalSpot, Spot newSpot) {
        // TODO
        return true;
    }
}
