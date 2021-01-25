package uk.co.bethlong.chess_validation_api.model.chess.board;

public enum BoardType {
    DEFAULT(DefaultBoard.class);

    private Class<? extends Board> boardClass;

    BoardType(Class<? extends Board> boardClass) {
        this.boardClass = boardClass;
    }

    public Class<? extends Board> getBoardClass() {
        return boardClass;
    }
}
