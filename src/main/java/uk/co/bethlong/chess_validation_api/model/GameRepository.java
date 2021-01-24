package uk.co.bethlong.chess_validation_api.model;

import org.springframework.stereotype.Service;
import uk.co.bethlong.chess_validation_api.model.game.chess.Game;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class GameRepository {
    private Map<String, Game> gameMap;

    public GameRepository()
    {
        gameMap = new HashMap<>();
    }

    public Optional<Game> findById(String id)
    {
        if (gameMap.containsKey(id))
            return Optional.of(gameMap.get(id));

        return Optional.empty();
    }
}
