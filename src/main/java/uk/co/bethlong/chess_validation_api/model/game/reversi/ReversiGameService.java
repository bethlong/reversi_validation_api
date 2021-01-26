package uk.co.bethlong.chess_validation_api.model.game.reversi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.co.bethlong.chess_validation_api.model.database.game.reversi.*;
import uk.co.bethlong.chess_validation_api.model.game.InvalidPlayerMoveRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReversiGameService {
    private final Logger LOGGER = LoggerFactory.getLogger(ReversiGameService.class);

    private final PlaceRequestRepository placeRequestRepository;
    private final ReversiGameRepository reversiGameRepository;
    private final ReversiPlayerRepository playerRepository;
    private final SpotRepository spotRepository;

    public ReversiGameService(PlaceRequestRepository placeRequestRepository, ReversiGameRepository reversiGameRepository, ReversiPlayerRepository playerRepository, SpotRepository spotRepository) {
        this.placeRequestRepository = placeRequestRepository;
        this.reversiGameRepository = reversiGameRepository;
        this.playerRepository = playerRepository;
        this.spotRepository = spotRepository;
    }

    public ReversiGame createNewGame(ReversiPlayer firstPlayer)
    {
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

        firstPlayer.setReversiGame(reversiGame);
        playerRepository.save(firstPlayer);

        return reversiGame;
    }

    public ReversiGame registerOtherPlayer(String gameUid, ReversiPlayer otherPlayer) {
        Optional<ReversiGame> gameOptional = reversiGameRepository.findById(gameUid);
        if (gameOptional.isEmpty())
            throw new IllegalArgumentException("Invalid game UID");

        ReversiGame reversiGame = gameOptional.get();

        List<ReversiPlayer> playerList = playerRepository.findByReversiGame(reversiGame);

        if (playerList.size() > 1)
        {
            throw new NoPlayerSlotAvailableException("All player slots are full for game " + gameUid + "... player '" + otherPlayer.getPlayerName() + "' cannot join!");
        }
        if (playerList.get(0).isRed() && otherPlayer.isRed())
            playerList.add(otherPlayer);
        else if (!playerList.get(0).isRed() && !otherPlayer.isRed())
            playerList.add(otherPlayer);
        else
            throw new NoPlayerSlotAvailableException("No space for requested colour in game " + gameUid + "... player '" + otherPlayer.getPlayerName() + "' cannot join!");

        reversiGame.setGameManagementStatus(GameManagementStatus.WAITING_RED_TURN);

        return reversiGame;
    }

    public void makePlacement(String gameUid, ReversiPlayer player, PlaceRequest placeRequest) throws InvalidPlayerMoveRequestException {
        Optional<ReversiGame> gameOptional = reversiGameRepository.findById(gameUid);
        if (gameOptional.isEmpty())
            throw new IllegalArgumentException("Invalid game UID");
        ReversiGame reversiGame = gameOptional.get();

        if (reversiGame.isRedPlayersTurn() && !player.isRed()) {
            throw new InvalidPlayerMoveRequestException("Move was requested by BLUE player '" + player.getPlayerName() + "' which should be waiting.");
        }

        if (reversiGame.isBluePlayersTurn() && player.isRed()) {
            throw new InvalidPlayerMoveRequestException("Move was requested by RED player '" + player.getPlayerName() + "' which should be waiting.");
        }

        Optional<Spot> spotOptional = spotRepository.findByXColumnAndYRowAndReversiGame(placeRequest.getXColumn(), placeRequest.getYRow(), reversiGame);
        if (spotOptional.isEmpty())
            throw new InvalidPlayerMoveRequestException("Failed to find spot requested");

        Spot spot = spotOptional.get();

        if (spot.hasPiece())
            throw new InvalidPlayerMoveRequestException("Piece already exists in spot (" + placeRequest.getXColumn() + ", " + placeRequest.getYRow() + ")");

        spot.setHasPiece(true);
        spot.setIsRedPiece(player.isRed());

        // TODO Flip over colours in between

        spotRepository.save(spot);
        placeRequestRepository.save(placeRequest);

        if (reversiGame.isRedPlayersTurn())
            reversiGame.setGameManagementStatus(GameManagementStatus.WAITING_BLUE_TURN);
        else if (reversiGame.isBluePlayersTurn())
            reversiGame.setGameManagementStatus(GameManagementStatus.WAITING_RED_TURN);

        reversiGameRepository.save(reversiGame);
    }
}
