package uk.co.bethlong.chess_validation_api.model.game.chess;

public class Player {
    private boolean isWhiteSide;
    private String playerId;

    public Player(boolean isWhiteSide, String playerId) {
        this.isWhiteSide = isWhiteSide;
        this.playerId = playerId;
    }

    public boolean isWhiteSide()
    {
        return this.isWhiteSide;
    }

    public String getPlayerId() {
        return playerId;
    }
}
