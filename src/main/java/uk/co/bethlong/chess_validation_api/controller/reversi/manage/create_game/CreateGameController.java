package uk.co.bethlong.chess_validation_api.controller.reversi.manage.create_game;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiGame;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiPlayer;
import uk.co.bethlong.chess_validation_api.model.game.reversi.ReversiGameService;
import uk.co.bethlong.chess_validation_api.model.game.reversi.ReversiPlayerService;

@RestController
@RequestMapping("/reversi/create-game")
public class CreateGameController {
    private final ReversiGameService reversiGameService;
    private final ReversiPlayerService reversiPlayerService;

    public CreateGameController(ReversiGameService reversiGameService, ReversiPlayerService reversiPlayerService) {
        this.reversiGameService = reversiGameService;
        this.reversiPlayerService = reversiPlayerService;
    }

    @GetMapping
    public CreateGameApiResponse getCreateGame(@RequestParam("playerName") String playerName, @RequestParam(value = "isRed", required = false) Boolean isRed)
    {
        if (isRed == null) isRed = true;
        ReversiPlayer reversiPlayer = reversiPlayerService.registerPlayer(playerName, isRed);

        ReversiGame reversiGame = reversiGameService.createNewGame(reversiPlayer);

        CreateGameApiResponse apiResponse = new CreateGameApiResponse();
        apiResponse.gameUid = reversiGame.getGameUid();
        apiResponse.playerUid = reversiPlayer.getPlayerUid();

        return apiResponse;
    }
}
