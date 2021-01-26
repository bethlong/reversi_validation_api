package uk.co.bethlong.chess_validation_api.model.game.reversi;

public class NoPlayerSlotAvailableException extends RuntimeException {
    public NoPlayerSlotAvailableException() {
    }

    public NoPlayerSlotAvailableException(String message) {
        super(message);
    }

    public NoPlayerSlotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoPlayerSlotAvailableException(Throwable cause) {
        super(cause);
    }

    public NoPlayerSlotAvailableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
