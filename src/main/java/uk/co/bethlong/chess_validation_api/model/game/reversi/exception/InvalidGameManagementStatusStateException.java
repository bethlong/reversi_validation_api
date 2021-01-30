package uk.co.bethlong.chess_validation_api.model.game.reversi.exception;

public class InvalidGameManagementStatusStateException extends RuntimeException {
    public InvalidGameManagementStatusStateException() {
    }

    public InvalidGameManagementStatusStateException(String message) {
        super(message);
    }

    public InvalidGameManagementStatusStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidGameManagementStatusStateException(Throwable cause) {
        super(cause);
    }

    public InvalidGameManagementStatusStateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
