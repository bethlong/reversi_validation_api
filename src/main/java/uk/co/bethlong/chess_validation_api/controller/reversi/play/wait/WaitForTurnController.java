package uk.co.bethlong.chess_validation_api.controller.reversi.play.wait;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiGame;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiPlayer;
import uk.co.bethlong.chess_validation_api.model.game.reversi.ReversiGameService;
import uk.co.bethlong.chess_validation_api.model.game.reversi.ReversiPlayerService;

import java.time.Duration;

@RestController
@RequestMapping("/reversi/wait-for-turn")
public class WaitForTurnController {

    private final ReversiPlayerService reversiPlayerService;
    private final ReversiGameService reversiGameService;

    public WaitForTurnController(ReversiPlayerService reversiPlayerService, ReversiGameService reversiGameService) {
        this.reversiPlayerService = reversiPlayerService;
        this.reversiGameService = reversiGameService;
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<TurnWaitApiResponse> streamFlux(@RequestParam String gameUid, @RequestParam String playerUid) {

        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence ->
                {
                    ReversiGame reversiGame = reversiGameService.findGame(gameUid);
                    ReversiPlayer reversiPlayer = reversiPlayerService.getPlayerInGame(reversiGame, playerUid);

                    TurnWaitApiResponse turnWaitApiResponse = new TurnWaitApiResponse();
                    turnWaitApiResponse.gameUid = reversiGame.getGameUid();
                    turnWaitApiResponse.playerName = reversiPlayer.getPlayerName();

                    if (reversiPlayer.isRed())
                        turnWaitApiResponse.isTurn = reversiGame.isTurn(true);
                    else
                        turnWaitApiResponse.isTurn = reversiGame.isTurn(false);

                    return turnWaitApiResponse;
                });
    }
}
