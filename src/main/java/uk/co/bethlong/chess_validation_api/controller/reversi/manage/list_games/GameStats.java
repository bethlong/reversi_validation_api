package uk.co.bethlong.chess_validation_api.controller.reversi.manage.list_games;

import uk.co.bethlong.chess_validation_api.model.game.reversi.GameManagementStatus;
import uk.co.bethlong.chess_validation_api.model.game.reversi.VictoryStatus;

public class GameStats {
    public String gameUid;
    public String redPlayerName;
    public String bluePlayerName;
    public String dateStarted;
    public String dateFinished;
    public VictoryStatus victoryStatus;
    public GameManagementStatus gameStatus;

    public GameStats() {
        this.gameUid = "";
        this.redPlayerName = "";
        this.bluePlayerName = "";
        this.dateStarted = "";
        this.dateFinished = "";
        this.victoryStatus = VictoryStatus.NONE;
    }
}
