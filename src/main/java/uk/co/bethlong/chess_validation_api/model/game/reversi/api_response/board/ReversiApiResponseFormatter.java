package uk.co.bethlong.chess_validation_api.model.game.reversi.api_response.board;

import org.springframework.stereotype.Service;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class ReversiApiResponseFormatter {

    private final ReversiGameRepository reversiGameRepository;
    private final SpotRepository spotRepository;
    private final PlaceRequestRepository placeRequestRepository;

    public ReversiApiResponseFormatter(ReversiGameRepository reversiGameRepository, SpotRepository spotRepository, PlaceRequestRepository placeRequestRepository) {
        this.reversiGameRepository = reversiGameRepository;
        this.spotRepository = spotRepository;
        this.placeRequestRepository = placeRequestRepository;
    }

    public ReversiGameBoard getBoardResponse(String gameUid)
    {
        Optional<ReversiGame> gameOptional = reversiGameRepository.findById(gameUid);
        if (gameOptional.isEmpty())
            throw new IllegalArgumentException("Invalid game UID");

        ReversiGame reversiGame = gameOptional.get();

        ReversiGameBoard apiResponse = new ReversiGameBoard();
        apiResponse.dateCreated = reversiGame.getDateCreated();
        apiResponse.dateFinished = reversiGame.getDateFinished();
        apiResponse.turn = reversiGame.isRedPlayersTurn() ? GameTurn.RED : GameTurn.BLUE;
        apiResponse.victoryStatus = reversiGame.getVictoryStatus();
        apiResponse.xColumnTotal = reversiGame.getxColumnCount();
        apiResponse.yRowTotal = reversiGame.getyRowCount();

        List<BoardSlot> boardSlotList = new LinkedList<>();
        List<Spot> spotList = spotRepository.findByReversiGame(reversiGame);
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
        List<PlaceRequest> placeRequestList = placeRequestRepository.findByReversiGame(reversiGame);
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
