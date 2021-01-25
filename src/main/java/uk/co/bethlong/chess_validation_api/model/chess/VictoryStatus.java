package uk.co.bethlong.chess_validation_api.model.chess;

public enum VictoryStatus {
    NONE,
    BLACK_CHECKMATE_WIN,
    WHITE_CHECKMATE_WIN,
    BLACK_FORFEIT_WIN,
    WHITE_FORFEIT_WIN,
    STALEMATE,
    DRAW_BY_TIME_LIMIT,
    DRAW_BY_REQUEST
}
