package uk.co.bethlong.chess_validation_api.model.database.game.chess.move;

public class InvalidMoveRequestException extends RuntimeException {

    public InvalidMoveRequestException() {
    }

    public InvalidMoveRequestException(String message) {
        super(message);
    }

    public InvalidMoveRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidMoveRequestException(Throwable cause) {
        super(cause);
    }

    public InvalidMoveRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
