package com.ml.tictactoe;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController
{
    @GetMapping(value="/")
    public String start()
    {
        return "start";
    }
}
