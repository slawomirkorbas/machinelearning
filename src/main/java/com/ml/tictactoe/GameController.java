package com.ml.tictactoe;

import com.ml.tictactoe.model.FieldCannotBeSetException;
import com.ml.tictactoe.model.GameState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Default Game controller class.
 *
 * @author Slawomir Korbas
 */
@RestController
public class GameController
{

    @Autowired
    Engine engine;

    @Autowired
    TrainedModelFactory trainedModelFactory;

    @GetMapping("tictactoe/newGameState")
    GameState getNeGame()
    {
        return new GameState();
    }

    @PostMapping(name = "/tictactoe/moveAndLearn", produces = MediaType.APPLICATION_JSON_VALUE)
    GameState moveAndLearn(@RequestBody GameState gameState, @RequestParam String userFigure) throws FieldCannotBeSetException
    {
        GameState newGameState = engine.doMoveAndUpdateModel(gameState, userFigure.equals("x") ? "o" : "x");
        return newGameState;
    }


    @PostMapping("/tictactoe/saveModel")
    boolean saveTrainedModel(@RequestParam String fileName)
    {
       return trainedModelFactory.serializeModel(engine.getTrainedModel(), fileName);
    }


    @PostMapping("/tictactoe/loadModel")
    boolean loadTrainedModel(@RequestParam String fileName)
    {
        Map loadedModel = trainedModelFactory.deserializeModel(fileName);
        if( loadedModel != null )
        {
            engine.setTrainedModel(loadedModel);
            return true;
        }
        return false;
    }

    @GetMapping("/tictactoe/trainedModelEfficiency")
    List<String> getTrainedModelAsEfficiencyBoard()
    {
        final List<String> trainedModelEfficiency = new ArrayList<>();
        engine.getTrainedModel().forEach( (k, v) -> {
            trainedModelEfficiency.add(v.toEfficiencyBoard());
        });
        return trainedModelEfficiency;
    }

}
