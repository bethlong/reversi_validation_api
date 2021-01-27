package uk.co.bethlong.chess_validation_api.controller.reversi.manage.ready_up;

import uk.co.bethlong.chess_validation_api.model.game.reversi.GameManagementStatus;
import uk.co.bethlong.chess_validation_api.model.game.reversi.VictoryStatus;

public class ReadyUpStatusApiResponse {
    public String gameUid;
    public String redPlayerName;
    public Boolean isRedReady;
    public String bluePlayerName;
    public Boolean isBlueReady;
    public String dateStarted;
    public VictoryStatus victoryStatus;
    public GameManagementStatus gameStatus;

    public ReadyUpStatusApiResponse() {
        this.gameUid = "";
        this.redPlayerName = "";
        this.bluePlayerName = "";
        this.dateStarted = "";
        this.victoryStatus = VictoryStatus.NONE;
    }
}
