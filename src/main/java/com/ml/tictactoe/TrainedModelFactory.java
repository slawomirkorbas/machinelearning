package com.ml.tictactoe;

import com.ml.tictactoe.model.GameState;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible for serializing and deserializing game model.
 *
 * @author Slawomir Korbas
 */
public class TrainedModelFactory
{

    /** Trained model **/
    private  Map<String, GameState> trainedModel;

    /** Default file name where the model is stored **/
    private final String DEFAULT_MODEL_FILE_NAME = "trainedModel.txt";

    public TrainedModelFactory()
    {
        trainedModel = deserializeModel(DEFAULT_MODEL_FILE_NAME);
        if( trainedModel == null )
        {
            // create an empty model if there is nothing stored
            trainedModel = new HashMap<>();
        }
    }

    public Map<String, GameState> getModel()
    {
        return this.trainedModel;
    }

    /**
     * Serializes game model to file
     * @param trainedModel
     * @param fileName
     */
    public boolean serializeModel(final Map<String, GameState> trainedModel, final String fileName)
    {
        try
        {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(trainedModel);
            objectOutputStream.flush();
            objectOutputStream.close();
            return true;
        }
        catch(IOException e)
        {
            return false;
        }

    }

    /**
     * Deserializes model from file
     * @param fileName
     * @return
     */
    public Map<String, GameState> deserializeModel(final String fileName)
    {
        FileInputStream fileInputStream = null;
        try
        {
            fileInputStream = new FileInputStream(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Map<String, GameState> model = (Map<String, GameState>) objectInputStream.readObject();
            objectInputStream.close();
            return model;
        }
        catch(IOException | ClassNotFoundException e)
        {
            return null;
        }
    }

}
