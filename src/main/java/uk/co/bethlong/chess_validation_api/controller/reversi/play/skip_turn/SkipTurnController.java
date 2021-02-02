package uk.co.bethlong.chess_validation_api.controller.reversi.play.skip_turn;

import org.springframework.web.bind.annotation.*;
import uk.co.bethlong.chess_validation_api.model.game.reversi.ReversiGamePlayService;

@RestController
@RequestMapping("/reversi/skip-turn")
public class SkipTurnController {

    private final ReversiGamePlayService reversiGamePlayService;

    public SkipTurnController(ReversiGamePlayService reversiGamePlayService) {
        this.reversiGamePlayService = reversiGamePlayService;
    }

    @GetMapping
    public SkipTurnApiResponse getSkipTurn(
            @RequestParam String gameUid,
            @RequestParam String playerUid
            )
    {
        SkipTurnApiResponse skipTurnApiResponse = new SkipTurnApiResponse();

        reversiGamePlayService.skipTurn(gameUid, playerUid);

        skipTurnApiResponse.hasSucceeded = true;
        skipTurnApiResponse.isValidMove = true;
        skipTurnApiResponse.failureReason = "";

        return skipTurnApiResponse;
    }
}
