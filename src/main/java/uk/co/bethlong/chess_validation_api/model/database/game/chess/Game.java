package uk.co.bethlong.chess_validation_api.model.database.game.chess;

import uk.co.bethlong.chess_validation_api.model.database.game.chess.board.Board;
import uk.co.bethlong.chess_validation_api.model.database.game.chess.board.BoardType;
import uk.co.bethlong.chess_validation_api.model.database.game.chess.board.DefaultBoard;
import uk.co.bethlong.chess_validation_api.model.database.game.chess.move.Move;
import uk.co.bethlong.chess_validation_api.model.database.game.chess.move.MoveValidator;
import uk.co.bethlong.chess_validation_api.model.game.InvalidPlayerMoveException;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Game {
    private final MoveValidator moveValidator;
    private final List<Move> moveList;

    private String gameUid;

    private final Board board;

    private GameManagementStatus gameManagementStatus;
    private LocalDateTime dateCreated;

    private ChessPlayer whitePlayer;
    private ChessPlayer blackPlayer;

    private VictoryStatus victoryStatus;
    private LocalDateTime dateFinished;

    public Game(ChessPlayer whitePlayer, BoardType boardType) {
        switch (boardType)
        {
            default:
                this.board = new DefaultBoard();
                break;
        }

        this.moveValidator = new MoveValidator(board);

        this.whitePlayer = whitePlayer;
        this.blackPlayer = null;

        this.gameUid = UUID.randomUUID().toString();
        this.gameManagementStatus = GameManagementStatus.WAITING_SECOND_PLAYER_TO_JOIN;

        this.dateCreated = LocalDateTime.now();

        this.victoryStatus = VictoryStatus.NONE;
        this.dateFinished = null;

        this.moveList = new LinkedList<>();
    }

    public void registerBlackPlayer(ChessPlayer blackPlayer)
    {
        this.blackPlayer = blackPlayer;

        this.gameManagementStatus = GameManagementStatus.WAITING_WHITE_TURN;
    }

    public void makeMove(ChessPlayer player, Move move) throws InvalidPlayerMoveException
    {
        if (isWhitePlayersTurn() && !player.isWhite())
        {
            throw new InvalidPlayerMoveException("Move was requested by BLACK player '" + player.getPlayerName() + "' which should be waiting.");
        }

        if (isBlackPlayersTurn() && player.isWhite())
        {
            throw new InvalidPlayerMoveException("Move was requested by WHITE player '" + player.getPlayerName() + "' which should be waiting.");
        }

        if (!moveValidator.isValid(move))
            throw new InvalidPlayerMoveException("Move is not valid given current state of the board");

        // TODO Make the move

        this.moveList.add(move);

        if (isWhitePlayersTurn())
            gameManagementStatus = GameManagementStatus.WAITING_BLACK_TURN;
        else if (isBlackPlayersTurn())
            gameManagementStatus = GameManagementStatus.WAITING_WHITE_TURN;
    }

    public String getGameUid() {
        return gameUid;
    }

    public Board getBoard() {
        return board;
    }

    public GameManagementStatus getGameManagementStatus() {
        return gameManagementStatus;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public ChessPlayer getWhitePlayer() {
        return whitePlayer;
    }

    public ChessPlayer getBlackPlayer() {
        return blackPlayer;
    }

    public VictoryStatus getVictoryStatus() {
        return victoryStatus;
    }

    public LocalDateTime getDateFinished() {
        return dateFinished;
    }

    private boolean isWhitePlayersTurn()
    {
        return gameManagementStatus.equals(GameManagementStatus.WAITING_WHITE_TURN);
    }

    private boolean isBlackPlayersTurn()
    {
        return gameManagementStatus.equals(GameManagementStatus.WAITING_BLACK_TURN);
    }
}
