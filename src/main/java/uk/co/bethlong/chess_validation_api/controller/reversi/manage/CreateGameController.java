package uk.co.bethlong.chess_validation_api.controller.reversi.manage;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.Game;
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
        ReversiPlayer reversiPlayer = reversiPlayerService.registerPlayer(playerName, isRed);

        Game game = reversiGameService.createNewGame(reversiPlayer);

        CreateGameApiResponse apiResponse = new CreateGameApiResponse();
        apiResponse.gameUid = game.getGameUid();
        apiResponse.playerUid = reversiPlayer.getPlayerUid();

        return apiResponse;
    }
}
