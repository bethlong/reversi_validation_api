package uk.co.bethlong.chess_validation_api.model.database.game.reversi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class ReversiPlayer {
    @Id
    @Column
    private String playerUid;
    @Column
    private String playerName;

    @Column
    private Date lastSuccessfulRequest;

    @ManyToOne
    private Game game;

    @Column
    private boolean isRed;

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean isRed() {
        return isRed;
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

    public Date getLastSuccessfulRequest() {
        return lastSuccessfulRequest;
    }

    public void setLastSuccessfulRequest(Date lastSuccessfulRequest) {
        this.lastSuccessfulRequest = lastSuccessfulRequest;
    }

    public void setRed(boolean red) {
        isRed = red;
    }
}
