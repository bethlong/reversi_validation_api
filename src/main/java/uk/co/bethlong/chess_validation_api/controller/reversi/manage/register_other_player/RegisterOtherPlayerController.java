package uk.co.bethlong.chess_validation_api.controller.reversi.manage.register_other_player;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiGame;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiPlayer;
import uk.co.bethlong.chess_validation_api.model.game.reversi.ReversiGameService;
import uk.co.bethlong.chess_validation_api.model.game.reversi.ReversiPlayerService;

@RestController
@RequestMapping("/reversi/register-other-player")
public class RegisterOtherPlayerController {
    private final ReversiPlayerService reversiPlayerService;
    private final ReversiGameService reversiGameService;

    public RegisterOtherPlayerController(ReversiPlayerService reversiPlayerService, ReversiGameService reversiGameService) {
        this.reversiPlayerService = reversiPlayerService;
        this.reversiGameService = reversiGameService;
    }

    @GetMapping
    public RegisterOtherPlayerAPIResponse registerOtherPlayer(@RequestParam String gameUid, @RequestParam String playerName, @RequestParam(required = false) Boolean isRed)
    {
        if (isRed == null) isRed = false;

        ReversiPlayer otherPlayer = reversiPlayerService.registerPlayer(playerName, isRed);

        ReversiGame reversiGame = reversiGameService.registerOtherPlayer(gameUid, otherPlayer);

        RegisterOtherPlayerAPIResponse apiResponse = new RegisterOtherPlayerAPIResponse();
        apiResponse.gameUid = reversiGame.getGameUid();
        apiResponse.playerUid = otherPlayer.getPlayerUid();

        return apiResponse;
    }
}