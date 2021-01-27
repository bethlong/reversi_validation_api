package uk.co.bethlong.chess_validation_api.model.game.reversi;

import org.springframework.stereotype.Service;
import uk.co.bethlong.chess_validation_api.controller.error.InvalidPlayerUidException;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiGame;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiPlayer;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.ReversiPlayerRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReversiPlayerService {
    private final ReversiPlayerRepository reversiPlayerRepository;

    public ReversiPlayerService(ReversiPlayerRepository reversiPlayerRepository) {
        this.reversiPlayerRepository = reversiPlayerRepository;
    }

    public ReversiPlayer registerPlayer(String playerName, Boolean isRed, ReversiGame reversiGame) {
        ReversiPlayer reversiPlayer = new ReversiPlayer();
        reversiPlayer.setLastSuccessfulRequest(new Date());
        reversiPlayer.setPlayerName(playerName);
        reversiPlayer.setPlayerUid(UUID.randomUUID().toString());
        reversiPlayer.setRed(isRed);
        reversiPlayer.setReversiGame(reversiGame);
        reversiPlayerRepository.save(reversiPlayer);

        return reversiPlayer;
    }

    public ReversiPlayer getPlayerInGame(ReversiGame reversiGame, String playerUid) {
        List<ReversiPlayer> reversiPlayerList = reversiPlayerRepository.findByReversiGame(reversiGame);
        ReversiPlayer player = null;
        for (ReversiPlayer reversiPlayer : reversiPlayerList) {
            if (reversiPlayer.getPlayerUid().equals(playerUid)) {
                player = reversiPlayer;
            }
        }

        if (player == null) {
            throw new InvalidPlayerUidException("Player UID '" + playerUid + "' is invalid.");
        }

        return player;
    }

    public Optional<ReversiPlayer> getPlayerInGame(ReversiGame reversiGame, Boolean isRed) {
        List<ReversiPlayer> reversiPlayerList = reversiPlayerRepository.findByReversiGame(reversiGame);
        for (ReversiPlayer reversiPlayer : reversiPlayerList) {
            if (reversiPlayer.isRed() == isRed) {
                return Optional.of(reversiPlayer);
            }
        }

        return Optional.empty();
    }
}
