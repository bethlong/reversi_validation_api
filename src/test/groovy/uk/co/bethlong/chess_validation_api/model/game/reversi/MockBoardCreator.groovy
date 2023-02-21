package uk.co.bethlong.chess_validation_api.model.game.reversi

import uk.co.bethlong.chess_validation_api.model.database.game.reversi.Spot

class MockBoardCreator {
    private Spot[][] board

    MockBoardCreator mock(int xColumnSize, int yRowSize) {
        Spot[][] spotGrid = new Spot[xColumnSize][yRowSize]
        for (int i = 0; i < xColumnSize; i++)
        {
            for (int k = 0; k < yRowSize; k++)
            {
                Spot spot = new Spot()
                spot.setXColumn(i + 1)
                spot.setYRow(k + 1)
                spot.setHasPiece(false)
                spot.setIsRedPiece(true)

                spotGrid[i][k] = spot
            }
        }
        board = spotGrid
        return this
    }

    MockBoardCreator putR(int xColumn, int yRow) {
        put(xColumn, yRow, true)
        return this
    }

    MockBoardCreator putB(int xColumn, int yRow) {
        put(xColumn, yRow, false)
        return this
    }

    MockBoardCreator put(int xColumn, int yRow, boolean isRedPiece) {
        board[xColumn - 1][yRow - 1].setHasPiece(true)
        board[xColumn - 1][yRow - 1].setIsRedPiece(isRedPiece)
        return this
    }

    Spot[][] create() {
        return board
    }
}
