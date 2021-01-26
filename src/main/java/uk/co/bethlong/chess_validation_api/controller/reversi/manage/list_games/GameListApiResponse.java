package uk.co.bethlong.chess_validation_api.controller.reversi.manage.list_games;

import java.util.ArrayList;
import java.util.List;

public class GameListApiResponse {
    public List<GameStats> gameStatsList;

    public GameListApiResponse() {
        this.gameStatsList = new ArrayList<>();
    }
}
