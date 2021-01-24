package uk.co.bethlong.chess_validation_api.model.game.chess.piece;

import uk.co.bethlong.chess_validation_api.model.game.chess.Board;
import uk.co.bethlong.chess_validation_api.model.game.chess.Spot;

public abstract class Piece {
    private boolean killed;
    private boolean white;

    public Piece(boolean white)
    {
        this.setKilled(false);
        this.setWhite(white);
    }

    public boolean isWhite()
    {
        return this.white;
    }

    public void setWhite(boolean white)
    {
        this.white = white;
    }

    public boolean isKilled()
    {
        return this.killed;
    }

    public void setKilled(boolean killed)
    {
        this.killed = killed;
    }

    public abstract boolean canMove(Board board, Spot start, Spot end);
}
