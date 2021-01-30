package uk.co.bethlong.chess_validation_api.controller.reversi.play.wait;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiGame;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiPlayer;
import uk.co.bethlong.chess_validation_api.model.game.reversi.GameManagementStatus;
import uk.co.bethlong.chess_validation_api.model.game.reversi.ReversiGameService;
import uk.co.bethlong.chess_validation_api.model.game.reversi.ReversiPlayerService;
import uk.co.bethlong.chess_validation_api.model.game.reversi.VictoryStatus;

import java.time.Duration;
import java.time.ZonedDateTime;

@RestController
@RequestMapping("/reversi/wait-for-turn")
public class WaitForTurnController {

    private final ReversiPlayerService reversiPlayerService;
    private final ReversiGameService reversiGameService;

    private final int fluxTimeoutCap;

    public WaitForTurnController(ReversiPlayerService reversiPlayerService,
                                 ReversiGameService reversiGameService,
                                 @Value("${uk.co.bethlong.api.controller.reversi.wait-for-turn.timeout-in-seconds}") int fluxTimeoutCap) {
        this.reversiPlayerService = reversiPlayerService;
        this.reversiGameService = reversiGameService;
        this.fluxTimeoutCap = fluxTimeoutCap;
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<TurnWaitApiResponse> streamFlux(@RequestParam final String gameUid, @RequestParam final String playerUid) {
        final ZonedDateTime timeoutCap = ZonedDateTime.now().plusMinutes(fluxTimeoutCap);
        return Flux.interval(Duration.ofSeconds(1)).takeWhile(t -> ZonedDateTime.now().equals(timeoutCap))
                .map(sequence ->
                {
                    ReversiGame reversiGame = reversiGameService.findGame(gameUid);
                    ReversiPlayer reversiPlayer = reversiPlayerService.getPlayerInGame(reversiGame, playerUid);

                    TurnWaitApiResponse turnWaitApiResponse = new TurnWaitApiResponse();
                    turnWaitApiResponse.gameUid = reversiGame.getGameUid();
                    turnWaitApiResponse.playerName = reversiPlayer.getPlayerName();
                    turnWaitApiResponse.victoryStatus = reversiGame.getVictoryStatus();

                    if (!reversiGame.getGameManagementStatus().equals(GameManagementStatus.WAITING_RED_TURN) && !reversiGame.getGameManagementStatus().equals(GameManagementStatus.WAITING_BLUE_TURN))
                    {
                        return turnWaitApiResponse;
                    }

                    if (reversiPlayer.isRed())
                        turnWaitApiResponse.isTurn = reversiGame.isTurn(true);
                    else
                        turnWaitApiResponse.isTurn = reversiGame.isTurn(false);

                    turnWaitApiResponse.disconnectPlease = !reversiGame.getVictoryStatus().equals(VictoryStatus.NONE) || turnWaitApiResponse.isTurn

                    return turnWaitApiResponse;
                });
    }
}
