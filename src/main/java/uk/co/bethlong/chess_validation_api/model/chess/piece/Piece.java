package uk.co.bethlong.chess_validation_api.model.chess.piece;

import uk.co.bethlong.chess_validation_api.model.chess.Spot;

public abstract class Piece {
    private boolean isWhite;
    private boolean isKilled;
    private PieceType pieceType;

    public Piece(boolean isWhite, PieceType pieceType) {
        this.isWhite = isWhite;
        this.pieceType = pieceType;
    }

    public abstract boolean canMove(Spot originalSpot, Spot newSpot);

    public boolean isWhite() {
        return isWhite;
    }

    public void setWhite(boolean white) {
        isWhite = white;
    }

    public boolean isKilled() {
        return isKilled;
    }

    public void setKilled(boolean killed) {
        isKilled = killed;
    }

    public PieceType getPieceType() {
        return pieceType;
    }
}
