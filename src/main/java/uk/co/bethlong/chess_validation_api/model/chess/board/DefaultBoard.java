package uk.co.bethlong.chess_validation_api.model.chess.board;

import uk.co.bethlong.chess_validation_api.model.chess.piece.*;

public class DefaultBoard extends Board {

    @Override
    public void resetBoard() {
        getSpot(1, 1).setPiece(new Rook(true));
        getSpot(2, 1).setPiece(new Knight(true));
        getSpot(3, 1).setPiece(new Bishop(true));
        getSpot(4, 1).setPiece(new Queen(true));
        getSpot(5, 1).setPiece(new King(true));
        getSpot(6, 1).setPiece(new Bishop(true));
        getSpot(7, 1).setPiece(new Knight(true));
        getSpot(8, 1).setPiece(new Rook(true));
        getSpot(1, 2).setPiece(new Pawn(true));
        getSpot(2, 2).setPiece(new Pawn(true));
        getSpot(3, 2).setPiece(new Pawn(true));
        getSpot(4, 2).setPiece(new Pawn(true));
        getSpot(5, 2).setPiece(new Pawn(true));
        getSpot(6, 2).setPiece(new Pawn(true));
        getSpot(7, 2).setPiece(new Pawn(true));
        getSpot(8, 2).setPiece(new Pawn(true));

        getSpot(1, 8).setPiece(new Rook(false));
        getSpot(2, 8).setPiece(new Knight(false));
        getSpot(3, 8).setPiece(new Bishop(false));
        getSpot(4, 8).setPiece(new Queen(false));
        getSpot(5, 8).setPiece(new King(false));
        getSpot(6, 8).setPiece(new Bishop(false));
        getSpot(7, 8).setPiece(new Knight(false));
        getSpot(8, 8).setPiece(new Rook(false));
        getSpot(1, 7).setPiece(new Pawn(false));
        getSpot(2, 7).setPiece(new Pawn(false));
        getSpot(3, 7).setPiece(new Pawn(false));
        getSpot(4, 7).setPiece(new Pawn(false));
        getSpot(5, 7).setPiece(new Pawn(false));
        getSpot(6, 7).setPiece(new Pawn(false));
        getSpot(7, 7).setPiece(new Pawn(false));
        getSpot(8, 7).setPiece(new Pawn(false));
    }
}
