package uk.co.bethlong.chess_validation_api.controller.reversi.manage.ready_up;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiGame;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiPlayer;
import uk.co.bethlong.chess_validation_api.model.game.reversi.ReversiGameManagementService;
import uk.co.bethlong.chess_validation_api.model.game.reversi.ReversiGameService;
import uk.co.bethlong.chess_validation_api.model.game.reversi.ReversiPlayerService;

import java.util.Optional;

@RestController
@RequestMapping("/reversi/ready-up")
public class ReadyUpController {

    private final ReversiGameManagementService managementService;
    private final ReversiGameService reversiGameService;
    private final ReversiPlayerService reversiPlayerService;

    public ReadyUpController(ReversiGameManagementService managementService, ReversiGameService reversiGameService, ReversiPlayerService reversiPlayerService) {
        this.managementService = managementService;
        this.reversiGameService = reversiGameService;
        this.reversiPlayerService = reversiPlayerService;
    }

    @GetMapping
    public ReadyUpStatusApiResponse readyUp(@RequestParam String gameUid, @RequestParam String playerUid)
    {
        managementService.readyUpPlayer(gameUid, playerUid);

        ReversiGame reversiGame = reversiGameService.findGame(gameUid);
        Optional<ReversiPlayer> redPlayerOptional = reversiPlayerService.getPlayerInGame(reversiGame, true);
        Optional<ReversiPlayer> bluePlayerOptional = reversiPlayerService.getPlayerInGame(reversiGame, false);

        ReadyUpStatusApiResponse readyUpStatusApiResponse = new ReadyUpStatusApiResponse();
        readyUpStatusApiResponse.redPlayerName = redPlayerOptional.isPresent() ? redPlayerOptional.get().getPlayerName() : "";
        readyUpStatusApiResponse.bluePlayerName = bluePlayerOptional.isPresent() ? bluePlayerOptional.get().getPlayerName() : "";
        readyUpStatusApiResponse.dateStarted = reversiGame.getDateCreated().toString();
        readyUpStatusApiResponse.gameStatus = reversiGame.getGameManagementStatus();
        readyUpStatusApiResponse.gameUid = reversiGame.getGameUid();
        readyUpStatusApiResponse.isRedReady = reversiGame.isReady(true);
        readyUpStatusApiResponse.isBlueReady = reversiGame.isReady(false);

        return readyUpStatusApiResponse;
    }
}
