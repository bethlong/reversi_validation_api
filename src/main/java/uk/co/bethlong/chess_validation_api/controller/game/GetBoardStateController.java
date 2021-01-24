package uk.co.bethlong.chess_validation_api.controller.game;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/get-board-state")
public class GetBoardStateController {

    @GetMapping
    public String getBoardState(String gameId)
    {
        return "hi!";
    }
}
