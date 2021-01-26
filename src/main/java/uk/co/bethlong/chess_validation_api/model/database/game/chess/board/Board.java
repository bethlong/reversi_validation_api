package uk.co.bethlong.chess_validation_api.model.database.game.chess.board;

import uk.co.bethlong.chess_validation_api.model.database.game.chess.Spot;
import uk.co.bethlong.chess_validation_api.model.database.game.chess.piece.Piece;
import uk.co.bethlong.chess_validation_api.model.database.game.chess.piece.PieceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Board {
    private Spot[][] spotGrid;

    public Board()
    {
        super();

        spotGrid = new Spot[8][8];
        for (int i = 0; i < 8; i++)
        {
            for (int k = 0; k < 8; k++)
            {
                spotGrid[i][k] = new Spot(i, k);
            }
        }

        resetBoard();
    }

    public abstract void resetBoard();

    public List<Piece> getPiece(boolean isWhite, PieceType pieceType)
    {
        List<Piece> pieceList = new ArrayList<>();
        for (Spot[] column : spotGrid)
        {
            for (Spot rowColumn: column)
            {
                Optional<Piece> pieceOptional = rowColumn.getPiece();
                if (pieceOptional.isPresent() && pieceType.equals(pieceOptional.get().getPieceType()) && pieceOptional.get().isWhite() == isWhite)
                {
                    pieceList.add(pieceOptional.get());
                }
            }
        }

        return pieceList;
    }

    public Spot getSpot(int xColumn, int yRow)
    {
        int modifiedXColumn = xColumn - 1;
        int modifiedYRow = yRow - 1;

        if (modifiedXColumn < 0 || modifiedXColumn > 7 || modifiedYRow < 0 || modifiedYRow > 7)
            throw new IllegalArgumentException("Invalid spot requested: " + xColumn + yRow);

        return spotGrid[modifiedXColumn][modifiedYRow];
    }
}
