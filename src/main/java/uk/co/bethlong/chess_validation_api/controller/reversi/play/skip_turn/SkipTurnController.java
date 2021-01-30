package uk.co.bethlong.chess_validation_api.controller.reversi.play.skip_turn;

import org.springframework.web.bind.annotation.*;
import uk.co.bethlong.chess_validation_api.model.game.InvalidPlayerMoveRequestException;
import uk.co.bethlong.chess_validation_api.model.game.reversi.ReversiGameService;

@RestController
@RequestMapping("/reversi/make-move")
public class SkipTurnController {

    private final ReversiGameService reversiGameService;

    public SkipTurnController(ReversiGameService reversiGameService) {
        this.reversiGameService = reversiGameService;
    }

    @GetMapping
    public SkipTurnApiResponse getSkipTurn(
            @RequestParam String gameUid,
            @RequestParam String playerUid
            )
    {
        SkipTurnApiResponse skipTurnApiResponse = new SkipTurnApiResponse();

        try {
            reversiGameService.requestSkipTurn(gameUid, playerUid);
        } catch (InvalidPlayerMoveRequestException e) {
            skipTurnApiResponse.failureReason = e.getMessage();
            skipTurnApiResponse.hasSucceeded = false;
            skipTurnApiResponse.isValidMove = false;

            return skipTurnApiResponse;
        }

        skipTurnApiResponse.hasSucceeded = true;
        skipTurnApiResponse.isValidMove = true;
        skipTurnApiResponse.failureReason = "";

        return skipTurnApiResponse;
    }
}
