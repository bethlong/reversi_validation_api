package uk.co.bethlong.chess_validation_api.model.game.reversi;

import org.springframework.stereotype.Service;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiPlayer;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiPlayerRepository;

import java.util.Date;
import java.util.UUID;

@Service
public class ReversiPlayerService {
    private final ReversiPlayerRepository reversiPlayerRepository;

    public ReversiPlayerService(ReversiPlayerRepository reversiPlayerRepository) {
        this.reversiPlayerRepository = reversiPlayerRepository;
    }

    public ReversiPlayer registerPlayer(String playerName, Boolean isRed) {
        ReversiPlayer reversiPlayer = new ReversiPlayer();
        reversiPlayer.setLastSuccessfulRequest(new Date());
        reversiPlayer.setPlayerName(playerName);
        reversiPlayer.setPlayerUid(UUID.randomUUID().toString());
        reversiPlayerRepository.save(reversiPlayer);

        return reversiPlayer;
    }
}
