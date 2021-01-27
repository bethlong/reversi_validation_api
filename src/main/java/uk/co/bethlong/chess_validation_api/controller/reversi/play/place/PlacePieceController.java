package uk.co.bethlong.chess_validation_api.controller.reversi.play.place;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bethlong.chess_validation_api.model.game.InvalidPlayerMoveRequestException;
import uk.co.bethlong.chess_validation_api.model.game.reversi.ReversiGameService;

@RestController
@RequestMapping("/reversi/make-move")
public class PlacePieceController {

    private final ReversiGameService reversiGameService;

    public PlacePieceController(ReversiGameService reversiGameService) {
        this.reversiGameService = reversiGameService;
    }

    @PostMapping
    public PlacePieceApiResponse placePiece(
            @RequestParam String gameUid,
            @RequestParam String playerUid,
            @RequestParam Integer xColumn,
            @RequestParam Integer yRow
            )
    {
        PlacePieceApiResponse placePieceApiResponse = new PlacePieceApiResponse();

        try {
            reversiGameService.makePlacement(gameUid, playerUid, xColumn, yRow);
        } catch (InvalidPlayerMoveRequestException e) {
            placePieceApiResponse.failureReason = e.getMessage();
            placePieceApiResponse.hasSucceeded = false;
            placePieceApiResponse.isValidMove = false;

            return placePieceApiResponse;
        }

        placePieceApiResponse.hasSucceeded = true;
        placePieceApiResponse.isValidMove = true;
        placePieceApiResponse.failureReason = "";

        return placePieceApiResponse;
    }
}
