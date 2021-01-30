package uk.co.bethlong.chess_validation_api.controller.reversi.play.get_board;

import uk.co.bethlong.chess_validation_api.model.game.reversi.VictoryStatus;

import java.util.Date;
import java.util.List;

public class ReversiGameBoardApiResponse {
    public Integer xColumnTotal;
    public Integer yRowTotal;
    public Integer redTotal;
    public Integer blueTotal;
    public List<BoardSlot> board;
    public Date dateCreated;
    public Date dateFinished;
    public GameTurn turn;
    public VictoryStatus victoryStatus;
    public List<Move> moveList;
}
