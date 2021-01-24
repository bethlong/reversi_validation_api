package uk.co.bethlong.chess_validation_api.model.game.chess;
public class Board {
    private Spot[][] spotGrid;

    public Board()
    {
        this.resetBoard();
    }

    public Spot getBox(int x, int y)
    {

        if (x < 0 || x > 7 || y < 0 || y > 7) {
            throw new IllegalArgumentException("Index out of bound");
        }

        return spotGrid[x][y];
    }

    public void resetBoard()
    {
        // initialize white pieces
        spotGrid[0][0] = new Spot(0, 0, new Rook(true));
        spotGrid[0][1] = new Spot(0, 1, new Knight(true));
        spotGrid[0][2] = new Spot(0, 2, new Bishop(true));
        spotGrid[1][0] = new Spot(1, 0, new Pawn(true));
        spotGrid[1][1] = new Spot(1, 1, new Pawn(true));

        // initialize black pieces
        spotGrid[7][0] = new Spot(7, 0, new Rook(false));
        spotGrid[7][1] = new Spot(7, 1, new Knight(false));
        spotGrid[7][2] = new Spot(7, 2, new Bishop(false));
        //...
        spotGrid[6][0] = new Spot(6, 0, new Pawn(false));
        spotGrid[6][1] = new Spot(6, 1, new Pawn(false));
        //...

        // initialize remaining spotGrid without any piece
        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                spotGrid[i][j] = new Spot(null, i, j);
            }
        }
    }
}