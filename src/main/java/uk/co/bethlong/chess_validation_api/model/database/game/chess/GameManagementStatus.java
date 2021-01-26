package uk.co.bethlong.chess_validation_api.model.database.game.chess;

public enum GameManagementStatus {
    NONE,
    WAITING_SECOND_PLAYER_TO_JOIN,
    WAITING_WHITE_TURN,
    WAITING_BLACK_TURN
}
