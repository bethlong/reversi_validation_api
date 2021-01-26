package uk.co.bethlong.chess_validation_api.controller.reversi.manage.list_games;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiGame;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiGameRepository;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiPlayer;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiPlayerRepository;

import java.util.List;

@RestController
@RequestMapping("/reversi/get-games")
public class ListGameController {

    private final ReversiGameRepository reversiGameRepository;
    private final ReversiPlayerRepository reversiPlayerRepository;

    public ListGameController(ReversiGameRepository reversiGameRepository, ReversiPlayerRepository reversiPlayerRepository) {
        this.reversiGameRepository = reversiGameRepository;
        this.reversiPlayerRepository = reversiPlayerRepository;
    }

    @GetMapping
    public GameListApiResponse getGames() {
        GameListApiResponse gameListApiResponse = new GameListApiResponse();
        Iterable<ReversiGame> gameList = reversiGameRepository.findAll();

        for (ReversiGame reversiGame : gameList) {
            GameStats gameStats = new GameStats();
            gameStats.gameUid = reversiGame.getGameUid();
            gameStats.dateStarted = reversiGame.getDateCreated().toString();
            gameStats.dateFinished = (reversiGame.getDateFinished() != null) ? reversiGame.getDateFinished().toString() : "";
            gameStats.victoryStatus = reversiGame.getVictoryStatus();
            gameStats.gameStatus = reversiGame.getGameManagementStatus();

            List<ReversiPlayer> reversiPlayerList = reversiPlayerRepository.findByReversiGame(reversiGame);
            for (ReversiPlayer reversiPlayer : reversiPlayerList) {
                if (reversiPlayer.isRed()) {
                    gameStats.redPlayerName = gameStats.redPlayerName + reversiPlayer.getPlayerName();
                } else {
                    gameStats.bluePlayerName = gameStats.bluePlayerName + reversiPlayer.getPlayerName();
                }
            }

            gameListApiResponse.gameStatsList.add(gameStats);
        }

        return gameListApiResponse;
    }
}
