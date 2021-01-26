package uk.co.bethlong.chess_validation_api.model.database.game.reversi;

import org.hibernate.annotations.CreationTimestamp;
import uk.co.bethlong.chess_validation_api.model.game.reversi.GameManagementStatus;
import uk.co.bethlong.chess_validation_api.model.game.reversi.VictoryStatus;

import javax.persistence.*;
import java.util.*;

@Entity
public class ReversiGame {
    @Id
    @Column
    private String gameUid;

    @OneToMany
    private final List<PlaceRequest> placeRequestList;

    @Column
    @Enumerated(value = EnumType.STRING)
    private GameManagementStatus gameManagementStatus;

    @Column
    @CreationTimestamp
    private Date dateCreated;

    @OneToMany
    private List<ReversiPlayer> playerList;

    @Column
    @Enumerated(value = EnumType.STRING)
    private VictoryStatus victoryStatus;
    @Column
    private Date dateFinished;

    @Column
    private int xColumnCount;
    @Column
    private int yRowCount;

    public ReversiGame() {
        this.xColumnCount = 0;
        this.yRowCount = 0;

        playerList = new ArrayList<>();

        this.gameUid = UUID.randomUUID().toString();
        this.gameManagementStatus = GameManagementStatus.NONE;

        this.dateCreated = new Date();

        this.victoryStatus = VictoryStatus.NONE;
        this.dateFinished = null;

        this.placeRequestList = new LinkedList<>();
    }

    public List<PlaceRequest> getPlaceRequestList()
    {
        return placeRequestList;
    }

    public String getGameUid() {
        return gameUid;
    }

    public GameManagementStatus getGameManagementStatus() {
        return gameManagementStatus;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public VictoryStatus getVictoryStatus() {
        return victoryStatus;
    }

    public Date getDateFinished() {
        return dateFinished;
    }

    public boolean isRedPlayersTurn() {
        return gameManagementStatus.equals(GameManagementStatus.WAITING_RED_TURN);
    }

    public boolean isBluePlayersTurn() {
        return gameManagementStatus.equals(GameManagementStatus.WAITING_BLUE_TURN);
    }

    public List<ReversiPlayer> getPlayerList() {
        return playerList;
    }

    public void setGameManagementStatus(GameManagementStatus gameManagementStatus) {
        this.gameManagementStatus = gameManagementStatus;
    }

    public void setVictoryStatus(VictoryStatus victoryStatus) {
        this.victoryStatus = victoryStatus;
    }

    public void setDateFinished(Date dateFinished) {
        this.dateFinished = dateFinished;
    }

    public void setxColumnCount(int xColumnCount) {
        this.xColumnCount = xColumnCount;
    }

    public void setyRowCount(int yRowCount) {
        this.yRowCount = yRowCount;
    }

    public int getxColumnCount() {
        return xColumnCount;
    }

    public int getyRowCount() {
        return yRowCount;
    }
}