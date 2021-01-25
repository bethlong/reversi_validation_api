package uk.co.bethlong.chess_validation_api.model.chess;

import uk.co.bethlong.chess_validation_api.model.chess.board.Board;
import uk.co.bethlong.chess_validation_api.model.chess.move.Move;

public class MoveValidator {

    private final Board board;

    public MoveValidator(Board board) {
        this.board = board;
    }

    public boolean isValid(Move move)
    {
        // TODO validate the move
        return true;
    }
}
