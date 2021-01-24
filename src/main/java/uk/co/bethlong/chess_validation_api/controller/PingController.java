package uk.co.bethlong.chess_validation_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

@RestController
public class PingController {

    @GetMapping("/ping")
    public String getPing()
    {
        return "pong! " + ZonedDateTime.now().toString();
    }
}
