package uk.co.bethlong.chess_validation_api.controller.reversi.manage.register_other_player;

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
@RequestMapping("/reversi/join-game")
public class RegisterOtherPlayerController {
    private final ReversiPlayerService reversiPlayerService;
    private final ReversiGameManagementService managementService;

    public RegisterOtherPlayerController(ReversiPlayerService reversiPlayerService, ReversiGameManagementService managementService) {
        this.reversiPlayerService = reversiPlayerService;
        this.managementService = managementService;
    }

    @GetMapping
    public RegisterOtherPlayerAPIResponse registerOtherPlayer(@RequestParam String gameUid, @RequestParam String playerName, @RequestParam(required = false, defaultValue = "false") Boolean isRed)
    {
        ReversiGame reversiGame = managementService.registerOtherPlayer(gameUid, playerName, isRed);
        Optional<ReversiPlayer> otherPlayer = reversiPlayerService.getPlayerInGame(reversiGame, isRed);
        if (otherPlayer.isEmpty())
        {
            throw new IllegalArgumentException("Reversi player was empty when it was expected for gameUID '" + reversiGame.getGameUid() + "'");
        }

        RegisterOtherPlayerAPIResponse apiResponse = new RegisterOtherPlayerAPIResponse();
        apiResponse.gameUid = reversiGame.getGameUid();
        apiResponse.playerUid = otherPlayer.get().getPlayerUid();

        return apiResponse;
    }
}