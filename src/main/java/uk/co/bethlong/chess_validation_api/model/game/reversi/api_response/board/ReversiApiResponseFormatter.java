package uk.co.bethlong.chess_validation_api.model.game.reversi.api_response.board;

import org.springframework.stereotype.Service;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class ReversiApiResponseFormatter {

    private final GameRepository gameRepository;
    private final SpotRepository spotRepository;
    private final PlaceRequestRepository placeRequestRepository;

    public ReversiApiResponseFormatter(GameRepository gameRepository, SpotRepository spotRepository, PlaceRequestRepository placeRequestRepository) {
        this.gameRepository = gameRepository;
        this.spotRepository = spotRepository;
        this.placeRequestRepository = placeRequestRepository;
    }

    public ReversiGameBoard getBoardResponse(String gameUid)
    {
        Optional<Game> gameOptional = gameRepository.findById(gameUid);
        if (gameOptional.isEmpty())
            throw new IllegalArgumentException("Invalid game UID");

        Game game = gameOptional.get();

        ReversiGameBoard apiResponse = new ReversiGameBoard();
        apiResponse.dateCreated = game.getDateCreated();
        apiResponse.dateFinished = game.getDateFinished();
        apiResponse.turn = game.isRedPlayersTurn() ? GameTurn.RED : GameTurn.BLUE;
        apiResponse.victoryStatus = game.getVictoryStatus();
        apiResponse.xColumnTotal = game.getxColumnCount();
        apiResponse.yRowTotal = game.getyRowCount();

        List<BoardSlot> boardSlotList = new LinkedList<>();
        List<Spot> spotList = spotRepository.findByGame(game);
        for (Spot spot : spotList)
        {
            BoardSlot boardSlot = new BoardSlot();
            boardSlot.xColumn = spot.getXColumn();
            boardSlot.yRow = spot.getYRow();
            boardSlot.hasPiece = spot.hasPiece();
            boardSlot.isRedPiece = spot.isRedPiece();
            boardSlotList.add(boardSlot);
        }
        apiResponse.board = boardSlotList;

        List<Move> moveList = new LinkedList<>();
        List<PlaceRequest> placeRequestList = placeRequestRepository.findByGame(game);
        for (PlaceRequest placeRequest: placeRequestList)
        {
            Move move = new Move();
            move.playerName = placeRequest.getPlayer().getPlayerName();
            move.xColumn = placeRequest.getXColumn();
            move.yRow = placeRequest.getYRow();
            moveList.add(move);
        }
        apiResponse.moveList = moveList;

        return apiResponse;
    }
}
