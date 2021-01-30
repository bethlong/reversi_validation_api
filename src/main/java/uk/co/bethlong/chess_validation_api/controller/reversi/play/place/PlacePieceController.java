package uk.co.bethlong.chess_validation_api.controller.reversi.play.place;

import org.springframework.web.bind.annotation.*;
import uk.co.bethlong.chess_validation_api.model.game.InvalidPlayerMoveException;
import uk.co.bethlong.chess_validation_api.model.game.reversi.ReversiGamePlayService;

@RestController
@RequestMapping("/reversi/make-move")
public class PlacePieceController {
    private final ReversiGamePlayService reversiGamePlayService;

    public PlacePieceController(ReversiGamePlayService reversiGamePlayService) {
        this.reversiGamePlayService = reversiGamePlayService;
    }

    @GetMapping
    public PlacePieceApiResponse placePiece(
            @RequestParam String gameUid,
            @RequestParam String playerUid,
            @RequestParam Integer xColumn,
            @RequestParam Integer yRow
            )
    {
        PlacePieceApiResponse placePieceApiResponse = new PlacePieceApiResponse();

        try {
            reversiGamePlayService.makePlacement(gameUid, playerUid, xColumn, yRow);
        } catch (InvalidPlayerMoveException e) {
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
