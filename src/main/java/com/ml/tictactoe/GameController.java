package com.ml.tictactoe;

import com.ml.tictactoe.model.FieldCannotBeSetException;
import com.ml.tictactoe.model.GameState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.time.LocalDate.now;

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
    GameState moveAndLearn(@RequestBody GameState gameState, @RequestParam String userFigure,
                           @RequestParam int userMoveRow, @RequestParam int userMoveCol) throws FieldCannotBeSetException
    {
        GameState newGameState = engine.doMoveAndUpdateModel(gameState,
                                                             userFigure.equals("x") ? "o" : "x",
                                                             userMoveRow,
                                                             userMoveCol);
        return newGameState;
    }


    @GetMapping(value="/tictactoe/downloadModel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    ResponseEntity<byte[]> downloadTrainedModel()
    {
        final SimpleDateFormat df = new SimpleDateFormat("YYYYMMdd-HHmm");
        final String modelFileName = df.format(new Date()) + "_model.gmf";
        ByteArrayOutputStream modelByteStream = trainedModelFactory.serializeModel(engine.getTrainedModel());
        if(modelByteStream != null)
        {
            byte[] fileContent = modelByteStream.toByteArray();
            return ResponseEntity.ok()
                                     .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + modelFileName + "\"")
                                     .contentLength(fileContent.length)
                                     .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                     .body(fileContent);
        }
       return null;
    }


    @PostMapping("/tictactoe/loadModel")
    String uploadTrainedModel(@RequestParam("modelFile") MultipartFile modelFile, RedirectAttributes redirectAttributes)
    {
        if(modelFile == null && modelFile.isEmpty())
        {
            redirectAttributes.addFlashAttribute("message",
                                                 "File is empty: " + modelFile.getOriginalFilename() + "!");
            return "redirect:/";
        }

        Map loadedModel = trainedModelFactory.readModelFromFile(modelFile);
        if( loadedModel != null )
        {
            engine.setTrainedModel(loadedModel);
            redirectAttributes.addFlashAttribute("message",
                                                 "You successfully uploaded " + modelFile.getOriginalFilename() + "!");
        }
        else
        {
            redirectAttributes.addFlashAttribute("message",
                                                 "Error occurred when deserializing game model from file: " + modelFile.getOriginalFilename() + "!");

        }
        return "redirect:/";
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
