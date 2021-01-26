package uk.co.bethlong.chess_validation_api.model.database.game.chess;

import uk.co.bethlong.chess_validation_api.model.database.game.player.Player;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@Entity
public class ChessPlayer extends Player {
    @Column
    private boolean isWhite;

    @Column
    private Date lastSuccessfulRequest;

    @Column
    private boolean isRequestingDraw;
    @Column
    private boolean isInCheck;
    @Column
    private boolean isInCheckMate;

    public ChessPlayer(boolean isWhite, boolean isRequestingDraw, boolean isInCheck, boolean isInCheckMate, String playerUid, String playerName) {
        super(playerUid, playerName);
        this.isWhite = isWhite;
        this.isRequestingDraw = isRequestingDraw;
        this.isInCheck = isInCheck;
        this.isInCheckMate = isInCheckMate;
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

    public Date getLastSuccessfulRequest() {
        return lastSuccessfulRequest;
    }

    public void setLastSuccessfulRequest(Date lastSuccessfulRequest) {
        this.lastSuccessfulRequest = lastSuccessfulRequest;
    }
}
