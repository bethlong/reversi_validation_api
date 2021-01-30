package uk.co.bethlong.chess_validation_api.controller.reversi.play.wait;

import uk.co.bethlong.chess_validation_api.model.game.reversi.VictoryStatus;

public class TurnWaitApiResponse {
    public String gameUid;
    public String playerName;
    public Boolean isTurn;
    public VictoryStatus victoryStatus;
    public Boolean disconnectPlease;
}
