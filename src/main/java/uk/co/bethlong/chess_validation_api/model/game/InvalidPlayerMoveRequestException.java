package uk.co.bethlong.chess_validation_api.model.game;

public class InvalidPlayerMoveRequestException extends RuntimeException {
    public InvalidPlayerMoveRequestException() {
    }

    public InvalidPlayerMoveRequestException(String message) {
        super(message);
    }

    public InvalidPlayerMoveRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPlayerMoveRequestException(Throwable cause) {
        super(cause);
    }

    public InvalidPlayerMoveRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
