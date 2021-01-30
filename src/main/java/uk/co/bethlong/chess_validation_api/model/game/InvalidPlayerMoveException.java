package uk.co.bethlong.chess_validation_api.model.game;

public class InvalidPlayerMoveException extends Exception {

    public InvalidPlayerMoveException() {
    }

    public InvalidPlayerMoveException(String message) {
        super(message);
    }

    public InvalidPlayerMoveException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPlayerMoveException(Throwable cause) {
        super(cause);
    }

    public InvalidPlayerMoveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
