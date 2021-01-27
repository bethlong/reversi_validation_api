package uk.co.bethlong.chess_validation_api.model.game.reversi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.co.bethlong.chess_validation_api.controller.error.InvalidGameUidException;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.*;
import uk.co.bethlong.chess_validation_api.model.game.InvalidPlayerMoveRequestException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ReversiGameService {
    private final Logger LOGGER = LoggerFactory.getLogger(ReversiGameService.class);

    private final PlaceRequestRepository placeRequestRepository;
    private final ReversiGameRepository reversiGameRepository;
    private final ReversiPlayerService reversiPlayerService;
    private final SpotRepository spotRepository;

    public ReversiGameService(PlaceRequestRepository placeRequestRepository, ReversiGameRepository reversiGameRepository,
                              ReversiPlayerService reversiPlayerService, SpotRepository spotRepository) {
        this.placeRequestRepository = placeRequestRepository;
        this.reversiGameRepository = reversiGameRepository;
        this.reversiPlayerService = reversiPlayerService;
        this.spotRepository = spotRepository;
    }

    public ReversiGame createNewGame(String playerName, Boolean isRed) {
        int xColumnCount = 8;
        int yRowCount = 8;

        ReversiGame reversiGame = new ReversiGame();
        reversiGame.setxColumnCount(xColumnCount);
        reversiGame.setyRowCount(yRowCount);
        reversiGame.setGameManagementStatus(GameManagementStatus.WAITING_SECOND_PLAYER_TO_JOIN);
        reversiGameRepository.save(reversiGame);

        List<Spot> spotList = new ArrayList<>();
        for (int i = 0; i < xColumnCount; i++) {
            for (int k = 0; k < yRowCount; k++) {
                Spot spot = new Spot();
                spot.setXColumn(i);
                spot.setYRow(k);
                spot.setReversiGame(reversiGame);
                spotList.add(spot);
            }
        }
        spotRepository.saveAll(spotList);

        reversiPlayerService.registerPlayer(playerName, isRed, reversiGame);

        return reversiGame;
    }

    public ReversiGame registerOtherPlayer(String gameUid, String playerName, Boolean isRed) {
        ReversiGame reversiGame = this.findGame(gameUid);

        checkGameStatus(reversiGame,
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

        ReversiGame reversiGame = this.findGame(gameUid);

        checkGameStatus(reversiGame,
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
            reversiGame.setGameManagementStatus(GameManagementStatus.WAITING_RED_TURN);
        } else {
            throw new InvalidGameManagementStatusStateException("Player isRed=" + player.isRed() + " has already readied up for game '" + gameUid + "' but is trying to ready up again.");
        }

        reversiGameRepository.save(reversiGame);

        return reversiGame;
    }

    public ReversiGame findGame(String gameUid) {
        Optional<ReversiGame> reversiGameOptional = reversiGameRepository.findById(gameUid);
        if (reversiGameOptional.isEmpty())
            throw new InvalidGameUidException("GameUID '" + gameUid + "' is invalid.");

        return reversiGameOptional.get();
    }

    public void makePlacement(String gameUid, String playerUid, Integer xColumn, Integer yRow)
            throws InvalidPlayerMoveRequestException {
        ReversiGame reversiGame = findGame(gameUid);

        checkGameStatus(reversiGame, GameManagementStatus.WAITING_BLUE_TURN, GameManagementStatus.WAITING_RED_TURN);

        ReversiPlayer player = reversiPlayerService.getPlayerInGame(reversiGame, playerUid);

        PlaceRequest placeRequest = new PlaceRequest();
        placeRequest.setPlayer(player);
        placeRequest.setXColumn(xColumn);
        placeRequest.setYRow(yRow);
        placeRequest.setReversiGame(reversiGame);

        if (reversiGame.isTurn(true) && !player.isRed()) {
            throw new InvalidPlayerMoveRequestException("Move was requested by BLUE player '" + player.getPlayerName() + "' which should be waiting.");
        }

        if (reversiGame.isTurn(false) && player.isRed()) {
            throw new InvalidPlayerMoveRequestException("Move was requested by RED player '" + player.getPlayerName() + "' which should be waiting.");
        }

        Optional<Spot> spotOptional = spotRepository.findByXColumnAndYRowAndReversiGame(
                placeRequest.getXColumn(),
                placeRequest.getYRow(),
                reversiGame
        );
        if (spotOptional.isEmpty())
            throw new InvalidPlayerMoveRequestException("Failed to find spot requested");

        Spot spot = spotOptional.get();

        if (spot.hasPiece())
            throw new InvalidPlayerMoveRequestException("Piece already exists in spot (" + placeRequest.getXColumn()
                    + ", " + placeRequest.getYRow() + ")");

        spot.setHasPiece(true);
        spot.setIsRedPiece(player.isRed());

        // TODO Flip over colours in between

        // TODO total scores

        spotRepository.save(spot);
        placeRequestRepository.save(placeRequest);

        if (reversiGame.isTurn(true))
            reversiGame.setGameManagementStatus(GameManagementStatus.WAITING_BLUE_TURN);
        else if (reversiGame.isTurn(false))
            reversiGame.setGameManagementStatus(GameManagementStatus.WAITING_RED_TURN);

        reversiGameRepository.save(reversiGame);
    }

    private void checkGameStatus(ReversiGame reversiGame, GameManagementStatus... gameManagementStatuses) {
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
