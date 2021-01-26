package uk.co.bethlong.chess_validation_api.model.database.game.reversi;

import javax.persistence.*;

@Entity
public class PlaceRequest {
    @Id
    @GeneratedValue
    @Column(updatable = false)
    private int moveId;

    @ManyToOne
    private Game game;

    @ManyToOne
    private ReversiPlayer player;

    @Column
    private int xColumn;
    @Column
    private int yRow;

    public PlaceRequest(int xColumn, int yRow) {
        this.xColumn = xColumn;
        this.yRow = yRow;
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

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public ReversiPlayer getPlayer() {
        return player;
    }

    public void setPlayer(ReversiPlayer player) {
        this.player = player;
    }
}
