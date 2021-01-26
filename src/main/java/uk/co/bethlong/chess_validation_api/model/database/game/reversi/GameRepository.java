package uk.co.bethlong.chess_validation_api.model.database.game.reversi;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends CrudRepository<Game, String> {

}
