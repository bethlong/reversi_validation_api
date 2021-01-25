package uk.co.bethlong.chess_validation_api.model.chess;

import java.time.LocalDateTime;

public class Player {
    private boolean isWhite;

    private LocalDateTime lastSuccessfulRequest;

    private boolean isRequestingDraw;
    private boolean isInCheck;
    private boolean isInCheckMate;

    private String playerUid;
    private String playerName;

    public Player(boolean isWhite, boolean isRequestingDraw, boolean isInCheck, boolean isInCheckMate, String playerUid, String playerName) {
        this.isWhite = isWhite;
        this.isRequestingDraw = isRequestingDraw;
        this.isInCheck = isInCheck;
        this.isInCheckMate = isInCheckMate;
        this.playerUid = playerUid;
        this.playerName = playerName;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public void setWhite(boolean white) {
        isWhite = white;
    }

    public boolean isRequestingDraw() {
        return isRequestingDraw;
    }

    public void setRequestingDraw(boolean requestingDraw) {
        isRequestingDraw = requestingDraw;
    }

    public boolean isInCheck() {
        return isInCheck;
    }

    public void setInCheck(boolean inCheck) {
        isInCheck = inCheck;
    }

    public boolean isInCheckMate() {
        return isInCheckMate;
    }

    public void setInCheckMate(boolean inCheckMate) {
        isInCheckMate = inCheckMate;
    }

    public String getPlayerUid() {
        return playerUid;
    }

    public void setPlayerUid(String playerUid) {
        this.playerUid = playerUid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public LocalDateTime getLastSuccessfulRequest() {
        return lastSuccessfulRequest;
    }

    public void setLastSuccessfulRequest(LocalDateTime lastSuccessfulRequest) {
        this.lastSuccessfulRequest = lastSuccessfulRequest;
    }
}
