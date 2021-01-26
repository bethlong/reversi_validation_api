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
    private final GameRepository gameRepository;
    private final ReversiPlayerRepository playerRepository;
    private final SpotRepository spotRepository;

    public ReversiGameService(PlaceRequestRepository placeRequestRepository, GameRepository gameRepository, ReversiPlayerRepository playerRepository, SpotRepository spotRepository) {
        this.placeRequestRepository = placeRequestRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.spotRepository = spotRepository;
    }

    public Game createNewGame(ReversiPlayer firstPlayer)
    {
        int xColumnCount = 8;
        int yRowCount = 8;

        Game game = new Game(xColumnCount, yRowCount);
        game.setGameManagementStatus(GameManagementStatus.WAITING_SECOND_PLAYER_TO_JOIN);
        gameRepository.save(game);

        List<Spot> spotList = new ArrayList<>();
        for (int i = 0; i < xColumnCount; i++) {
            for (int k = 0; k < yRowCount; k++) {
                spotList.add(new Spot(i, k, game));
            }
        }
        spotRepository.saveAll(spotList);

        firstPlayer.setGame(game);
        playerRepository.save(firstPlayer);

        return game;
    }

    public void registerOtherPlayer(String gameUid, ReversiPlayer otherPlayer) {
        Optional<Game> gameOptional = gameRepository.findById(gameUid);
        if (gameOptional.isEmpty())
            throw new IllegalArgumentException("Invalid game UID");

        Game game = gameOptional.get();

        List<ReversiPlayer> playerList = playerRepository.findByGame(game);

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

        game.setGameManagementStatus(GameManagementStatus.WAITING_RED_TURN);
    }

    public void makePlacement(String gameUid, ReversiPlayer player, PlaceRequest placeRequest) throws InvalidPlayerMoveRequestException {
        Optional<Game> gameOptional = gameRepository.findById(gameUid);
        if (gameOptional.isEmpty())
            throw new IllegalArgumentException("Invalid game UID");
        Game game = gameOptional.get();

        if (game.isRedPlayersTurn() && !player.isRed()) {
            throw new InvalidPlayerMoveRequestException("Move was requested by BLUE player '" + player.getPlayerName() + "' which should be waiting.");
        }

        if (game.isBluePlayersTurn() && player.isRed()) {
            throw new InvalidPlayerMoveRequestException("Move was requested by RED player '" + player.getPlayerName() + "' which should be waiting.");
        }

        Optional<Spot> spotOptional = spotRepository.findByXColumnAndYRowAndGame(placeRequest.getXColumn(), placeRequest.getYRow(), game);
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

        if (game.isRedPlayersTurn())
            game.setGameManagementStatus(GameManagementStatus.WAITING_BLUE_TURN);
        else if (game.isBluePlayersTurn())
            game.setGameManagementStatus(GameManagementStatus.WAITING_RED_TURN);

        gameRepository.save(game);
    }
}