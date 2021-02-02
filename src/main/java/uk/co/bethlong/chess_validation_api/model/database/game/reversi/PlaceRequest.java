package uk.co.bethlong.chess_validation_api.model.database.game.reversi;

import javax.persistence.*;
import java.util.Date;

@Entity
public class PlaceRequest {
    @Id
    @GeneratedValue
    @Column(updatable = false)
    private int moveId;

    @ManyToOne
    private ReversiGame reversiGame;

    @ManyToOne
    private ReversiPlayer player;

    @Column
    private int xColumn;
    @Column
    private int yRow;
    @Column
    private boolean isSkip;

    @GeneratedValue
    @Column
    private Date requestedDate;

    public PlaceRequest() {
        isSkip = false;
        xColumn = -1;
        yRow = -1;
    }

    public int getMoveId() {
        return moveId;
    }

    public int getXColumn() {
        return xColumn;
    }

    public void setXColumn(int xColumn) {
        this.xColumn = xColumn;
    }

    public int getYRow() {
        return yRow;
    }

    public void setYRow(int yRow) {
        this.yRow = yRow;
    }

    public ReversiGame getReversiGame() {
        return reversiGame;
    }

    public void setReversiGame(ReversiGame reversiGame) {
        this.reversiGame = reversiGame;
    }

    public ReversiPlayer getPlayer() {
        return player;
    }

    public void setPlayer(ReversiPlayer player) {
        this.player = player;
    }

    public boolean isSkip() {
        return isSkip;
    }

    public void setSkip(boolean skip) {
        isSkip = skip;
    }

    public boolean isForSpot(Spot spot) {
        return spot.getXColumn() == this.getXColumn() && spot.getYRow() == this.getYRow();
    }
}
