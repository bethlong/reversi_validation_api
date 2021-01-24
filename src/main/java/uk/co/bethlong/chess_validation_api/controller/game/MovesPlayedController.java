package uk.co.bethlong.chess_validation_api.controller.game;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bethlong.chess_validation_api.model.GameService;
import uk.co.bethlong.chess_validation_api.model.game.chess.Game;

import java.util.Optional;

@RestController
@RequestMapping("/get-all-moves")
public class MovesPlayedController {

    private GameService gameService;

    public MovesPlayedController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public String getAllMovesPlayed(String gameId, String playerId)
    {
        Optional<Game> game = gameService.getGame(gameId);

    }
}
