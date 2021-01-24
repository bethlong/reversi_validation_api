package uk.co.bethlong.chess_validation_api.controller.game;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/make-move")
public class MakeMoveController {

    @GetMapping
    public String makeMove(String gameId, String playerId, int startX, int startY, String piece)
    {

    }
}
