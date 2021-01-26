package uk.co.bethlong.chess_validation_api.model.database.game.chess;

import uk.co.bethlong.chess_validation_api.model.database.game.chess.piece.Piece;

import java.util.Optional;

public class Spot {
    private int xColumn;
    private int yRow;
    private Piece piece;

    public Spot(int x, int y) {
        this.xColumn = x;
        this.yRow = y;
    }

    public int getXColumn() {
        return xColumn;
    }

    public int getYRow() {
        return yRow;
    }

    public Optional<Piece> getPiece() {
        return Optional.ofNullable(piece);
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }
}
