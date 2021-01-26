package uk.co.bethlong.chess_validation_api.model.database.game.player;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public abstract class Player {
    @Id
    @Column
    private String playerUid;
    @Column
    private String playerName;
    @Column
    private Date lastSuccessfulRequest;

    public Player(String playerUid, String playerName) {
        this.playerUid = playerUid;
        this.playerName = playerName;
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
}
