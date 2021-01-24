package uk.co.bethlong.chess_validation_api.model;

import org.springframework.stereotype.Service;
import uk.co.bethlong.chess_validation_api.model.game.chess.Game;

import java.util.Optional;

@Service
public class GameService {

    private GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Game createGame()
    {

    }

    public Optional<Game> getGame(String gameId)
    {
        return gameRepository.findById(gameId);
    }
}
