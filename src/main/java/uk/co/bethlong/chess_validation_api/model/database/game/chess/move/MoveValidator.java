package uk.co.bethlong.chess_validation_api.model.database.game.chess.move;

import uk.co.bethlong.chess_validation_api.model.database.game.chess.board.Board;

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
