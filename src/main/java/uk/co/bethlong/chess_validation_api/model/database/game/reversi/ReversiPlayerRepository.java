package uk.co.bethlong.chess_validation_api.model.database.game.reversi;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReversiPlayerRepository extends CrudRepository<ReversiPlayer, String> {
    List<ReversiPlayer> findByReversiGame(ReversiGame reversiGame);
}
