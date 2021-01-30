package uk.co.bethlong.chess_validation_api.controller.reversi.play.get_board;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.*;
import uk.co.bethlong.chess_validation_api.model.game.reversi.ReversiGameService;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/reversi/board")
public class GetBoardController {

    private final ReversiGameService reversiGameService;
    private final SpotRepository spotRepository;
    private final PlaceRequestRepository placeRequestRepository;

    public GetBoardController(ReversiGameService reversiGameService, SpotRepository spotRepository, PlaceRequestRepository placeRequestRepository) {
        this.reversiGameService = reversiGameService;
        this.spotRepository = spotRepository;
        this.placeRequestRepository = placeRequestRepository;
    }

    @GetMapping
    public ReversiGameBoardApiResponse getBoard(@RequestParam String gameUid)
    {
        ReversiGame reversiGame = reversiGameService.findGame(gameUid);

        GameTurn gameTurn;
        if (reversiGame.isTurn(true))
            gameTurn = GameTurn.RED;
        else if (reversiGame.isTurn(false))
            gameTurn = GameTurn.BLUE;
        else
            gameTurn = GameTurn.NONE;

        ReversiGameBoardApiResponse apiResponse = new ReversiGameBoardApiResponse();
        apiResponse.dateCreated = reversiGame.getDateCreated();
        apiResponse.dateFinished = reversiGame.getDateFinished();
        apiResponse.turn = gameTurn;
        apiResponse.victoryStatus = reversiGame.getVictoryStatus();
        apiResponse.xColumnTotal = reversiGame.getxColumnCount();
        apiResponse.yRowTotal = reversiGame.getyRowCount();
        apiResponse.blueTotal = reversiGame.getTotalBluePieces();
        apiResponse.redTotal = reversiGame.getTotalRedPieces();

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
            move.isSkip = placeRequest.isSkip();
            moveList.add(move);
        }
        apiResponse.moveList = moveList;

        return apiResponse;
    }
}
