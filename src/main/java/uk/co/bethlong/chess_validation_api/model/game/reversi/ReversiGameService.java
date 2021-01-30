package uk.co.bethlong.chess_validation_api.model.game.reversi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.co.bethlong.chess_validation_api.controller.error.InvalidGameUidException;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.*;
import uk.co.bethlong.chess_validation_api.model.game.reversi.exception.InvalidGameManagementStatusStateException;

import java.util.Arrays;
import java.util.Optional;

@Service
public class ReversiGameService {
    private final Logger LOGGER = LoggerFactory.getLogger(ReversiGameService.class);

    private final ReversiGameRepository reversiGameRepository;

    public ReversiGameService(ReversiGameRepository reversiGameRepository) {
        this.reversiGameRepository = reversiGameRepository;
    }

    public ReversiGame findGame(String gameUid) {
        Optional<ReversiGame> reversiGameOptional = reversiGameRepository.findById(gameUid);
        if (reversiGameOptional.isEmpty())
            throw new InvalidGameUidException("GameUID '" + gameUid + "' is invalid.");

        return reversiGameOptional.get();
    }

    public void checkGameStatus(ReversiGame reversiGame, GameManagementStatus... gameManagementStatuses) {
        for (GameManagementStatus gameManagementStatus : gameManagementStatuses) {
            if (reversiGame.getGameManagementStatus().equals(gameManagementStatus)) {
                return;
            }
        }

        throw new InvalidGameManagementStatusStateException(
                "Game Status for game '" + reversiGame.getGameUid()
                        + "' was '" + reversiGame.getGameManagementStatus() + "', but a game update only allowed for statuses '"
                        + Arrays.toString(gameManagementStatuses) + "' is being requested."
        );
    }
}
