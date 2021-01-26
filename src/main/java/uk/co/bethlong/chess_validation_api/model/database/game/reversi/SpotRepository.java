package uk.co.bethlong.chess_validation_api.model.database.game.reversi;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpotRepository extends CrudRepository<Spot, Integer> {
    Optional<Spot> findByXColumnAndYRowAndReversiGame(int xColumn, int yRow, ReversiGame reversiGame);

    List<Spot> findByReversiGame(ReversiGame reversiGame);
}
