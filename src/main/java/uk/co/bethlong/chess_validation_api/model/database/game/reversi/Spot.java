package uk.co.bethlong.chess_validation_api.model.database.game.reversi;

import javax.persistence.*;

@Entity
public class Spot {
    @Id
    @GeneratedValue
    @Column
    private int spotId;

    @Column
    private Integer XColumn;
    @Column
    private Integer YRow;

    @ManyToOne
    private ReversiGame reversiGame;

    @Column
    private boolean hasPiece;
    @Column
    private boolean isRedPiece;

    public Spot() {
        XColumn = -1;
        YRow = -1;
        reversiGame = null;
        hasPiece = false;
        isRedPiece = true;
    }

    public void setXColumn(Integer XColumn) {
        this.XColumn = XColumn - 1;
    }

    public void setYRow(Integer YRow) {
        this.YRow = YRow - 1;
    }

    public void setReversiGame(ReversiGame reversiGame) {
        this.reversiGame = reversiGame;
    }

    public int getXColumn() {
        return XColumn + 1;
    }

    public int getYRow() {
        return YRow + 1;
    }

    public boolean hasPiece() {
        return hasPiece;
    }

    public boolean isRedPiece() {
        return isRedPiece;
    }

    public void setHasPiece(boolean hasPiece) {
        this.hasPiece = hasPiece;
    }

    public void setIsRedPiece(boolean redPiece) {
        isRedPiece = redPiece;
    }

    public void flipPiece()
    {
        isRedPiece = !isRedPiece;
    }
}
