package uk.co.bethlong.chess_validation_api.controller.reversi.manage.list_games;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reversi/get-games")
public class ListGameController {

    @GetMapping
    public ActiveGameListApiResponse getGames()
    {
        return new ActiveGameListApiResponse();
    }
}
