package uk.co.bethlong.chess_validation_api.model.game2;

import uk.co.bethlong.chess_validation_api.model.game.chess.Move;
import uk.co.bethlong.chess_validation_api.model.game.chess.Spot;

import java.util.List;
import java.util.Map;

public class Response {
    private boolean isTurn;
    private List<Spot> spotList;
    private List<Move> moveList;
    private boolean opponentHasRequestedDraw;
    private boolean playerHasRequestedDraw;
}
