package uk.co.bethlong.chess_validation_api.model.game.reversi;

public enum GameManagementStatus {
    NONE,
    WAITING_SECOND_PLAYER_TO_JOIN,
    WAITING_FOR_READY_UP_BOTH,
    WAITING_FOR_READY_UP_BLUE,
    WAITING_FOR_READY_UP_RED,
    WAITING_RED_TURN,
    WAITING_BLUE_TURN
}
