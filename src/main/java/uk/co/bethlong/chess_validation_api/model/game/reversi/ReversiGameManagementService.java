package uk.co.bethlong.chess_validation_api.model.game.reversi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.*;
import uk.co.bethlong.chess_validation_api.model.game.reversi.exception.InvalidGameManagementStatusStateException;
import uk.co.bethlong.chess_validation_api.model.game.reversi.exception.NoPlayerSlotAvailableException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReversiGameManagementService {

    private final Logger LOGGER = LoggerFactory.getLogger(ReversiGameManagementService.class);

    private final ReversiGameRepository reversiGameRepository;
    private final ReversiPlayerService reversiPlayerService;
    private final SpotRepository spotRepository;
    private final ReversiGameService reversiGameService;

    public ReversiGameManagementService(ReversiGameRepository reversiGameRepository, ReversiPlayerService reversiPlayerService, SpotRepository spotRepository, ReversiGameService reversiGameService) {
        this.reversiGameRepository = reversiGameRepository;
        this.reversiPlayerService = reversiPlayerService;
        this.spotRepository = spotRepository;
        this.reversiGameService = reversiGameService;
    }

    public ReversiGame createNewGame(String playerName, boolean isRed) {
        int xColumnCount = 8;
        int yRowCount = 8;

        if (playerName == null || playerName.isEmpty())
            throw new IllegalArgumentException("Invalid player name passed to method");

        ReversiGame reversiGame = new ReversiGame();
        reversiGame.setxColumnCount(xColumnCount);
        reversiGame.setyRowCount(yRowCount);
        reversiGame.setTotalRedPieces(2);
        reversiGame.setTotalBluePieces(2);
        reversiGame.setGameManagementStatus(GameManagementStatus.WAITING_SECOND_PLAYER_TO_JOIN);
        reversiGameRepository.save(reversiGame);

        List<Spot> spotList = new ArrayList<>();
        for (int i = 1; i <= xColumnCount; i++) {
            for (int k = 1; k <= yRowCount; k++) {
                Spot spot = new Spot();
                spot.setXColumn(i);
                spot.setYRow(k);
                spot.setReversiGame(reversiGame);

                if ((i == 4 && k == 4) || (i == 5 && k == 5)) {
                    spot.setHasPiece(true);
                    spot.setIsRedPiece(true);
                } else if ((i == 5 && k == 4) || (i == 4 && k == 5)) {
                    spot.setHasPiece(true);
                    spot.setIsRedPiece(false);
                }

                spotList.add(spot);
            }
        }
        spotRepository.saveAll(spotList);

        reversiPlayerService.registerPlayer(playerName, isRed, reversiGame);

        return reversiGame;
    }

    public ReversiGame registerOtherPlayer(String gameUid, String playerName, boolean isRed) {
        if (playerName == null || playerName.isEmpty())
            throw new IllegalArgumentException("Invalid player name passed to method");

        ReversiGame reversiGame = reversiGameService.findGame(gameUid);

        reversiGameService.checkGameStatus(reversiGame,
                GameManagementStatus.WAITING_SECOND_PLAYER_TO_JOIN
        );

        Optional<ReversiPlayer> reversiPlayerOptional = reversiPlayerService.getPlayerInGame(reversiGame, isRed);

        if (reversiPlayerOptional.isPresent()) {
            throw new NoPlayerSlotAvailableException("No space for requested colour in game " + gameUid + "... player '" + playerName + "' cannot join!");
        }

        reversiPlayerService.registerPlayer(playerName, isRed, reversiGame);

        reversiGame.setGameManagementStatus(GameManagementStatus.WAITING_FOR_READY_UP_BOTH);
        reversiGameRepository.save(reversiGame);

        return reversiGame;
    }

    public ReversiGame readyUpPlayer(String gameUid, String playerUid) {

        ReversiGame reversiGame = reversiGameService.findGame(gameUid);

        reversiGameService.checkGameStatus(reversiGame,
                GameManagementStatus.WAITING_FOR_READY_UP_BLUE,
                GameManagementStatus.WAITING_FOR_READY_UP_BOTH,
                GameManagementStatus.WAITING_FOR_READY_UP_RED
        );

        ReversiPlayer player = reversiPlayerService.getPlayerInGame(reversiGame, playerUid);

        if (player.isRed() && reversiGame.getGameManagementStatus().equals(GameManagementStatus.WAITING_FOR_READY_UP_BOTH)) {
            reversiGame.setGameManagementStatus(GameManagementStatus.WAITING_FOR_READY_UP_BLUE);
        } else if (!player.isRed() && reversiGame.getGameManagementStatus().equals(GameManagementStatus.WAITING_FOR_READY_UP_BOTH)) {
            reversiGame.setGameManagementStatus(GameManagementStatus.WAITING_FOR_READY_UP_RED);
        } else if ((player.isRed() && reversiGame.getGameManagementStatus().equals(GameManagementStatus.WAITING_FOR_READY_UP_RED))
                || (!player.isRed() && reversiGame.getGameManagementStatus().equals(GameManagementStatus.WAITING_FOR_READY_UP_BLUE))) {
            // Blue goes first
            reversiGame.setGameManagementStatus(GameManagementStatus.WAITING_BLUE_TURN);
        } else {
            throw new InvalidGameManagementStatusStateException("Player isRed=" + player.isRed() + " has already readied up for game '" + gameUid + "' but is trying to ready up again.");
        }

        reversiGameRepository.save(reversiGame);

        return reversiGame;
    }
}
