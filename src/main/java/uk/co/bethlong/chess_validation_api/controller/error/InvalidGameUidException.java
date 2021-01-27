package uk.co.bethlong.chess_validation_api.controller.error;

public class InvalidGameUidException extends RuntimeException {

    public InvalidGameUidException() {
    }

    public InvalidGameUidException(String message) {
        super(message);
    }

    public InvalidGameUidException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidGameUidException(Throwable cause) {
        super(cause);
    }

    public InvalidGameUidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
