package uk.co.bethlong.chess_validation_api.controller.error;

public class InvalidPlayerUidException extends RuntimeException {
    public InvalidPlayerUidException() {
    }

    public InvalidPlayerUidException(String message) {
        super(message);
    }

    public InvalidPlayerUidException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPlayerUidException(Throwable cause) {
        super(cause);
    }

    public InvalidPlayerUidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
