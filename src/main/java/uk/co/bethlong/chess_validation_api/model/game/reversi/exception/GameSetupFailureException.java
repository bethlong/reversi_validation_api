package uk.co.bethlong.chess_validation_api.model.game.reversi.exception;

public class GameSetupFailureException extends RuntimeException {
    public GameSetupFailureException() {
    }

    public GameSetupFailureException(String message) {
        super(message);
    }

    public GameSetupFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public GameSetupFailureException(Throwable cause) {
        super(cause);
    }

    public GameSetupFailureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
