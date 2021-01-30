package uk.co.bethlong.chess_validation_api.controller.reversi.manage.create_game;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiGame;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiPlayer;
import uk.co.bethlong.chess_validation_api.model.game.reversi.ReversiGameManagementService;
import uk.co.bethlong.chess_validation_api.model.game.reversi.ReversiPlayerService;

import java.util.Optional;

@RestController
@RequestMapping("/reversi/create-game")
public class CreateGameController {
    private final ReversiGameManagementService managementService;
    private final ReversiPlayerService reversiPlayerService;

    public CreateGameController(ReversiGameManagementService managementService, ReversiPlayerService reversiPlayerService) {
        this.managementService = managementService;
        this.reversiPlayerService = reversiPlayerService;
    }

    @GetMapping
    public CreateGameApiResponse getCreateGame(@RequestParam("playerName") String playerName, @RequestParam(value = "isRed", required = false, defaultValue = "true") Boolean isRed)
    {
        if (playerName == null || playerName.isEmpty())
        {
            throw new IllegalArgumentException("Invalid player name, please specify a player name");
        }

        ReversiGame reversiGame = managementService.createNewGame(playerName, isRed);
        Optional<ReversiPlayer> reversiPlayer = reversiPlayerService.getPlayerInGame(reversiGame, isRed);
        if (reversiPlayer.isEmpty())
        {
            throw new IllegalArgumentException("Reversi player was empty when it was expected for gameUID '" + reversiGame.getGameUid() + "'");
        }

        CreateGameApiResponse apiResponse = new CreateGameApiResponse();
        apiResponse.gameUid = reversiGame.getGameUid();
        apiResponse.playerUid = reversiPlayer.get().getPlayerUid();

        return apiResponse;
    }
}
