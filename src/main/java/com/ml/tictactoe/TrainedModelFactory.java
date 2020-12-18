package com.ml.tictactoe;

import com.ml.tictactoe.model.GameState;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
     */
    public ByteArrayOutputStream serializeModel(final Map<String, GameState> trainedModel)
    {
        try
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(trainedModel);
            objectOutputStream.flush();
            objectOutputStream.close();
            return byteArrayOutputStream;
        }
        catch(IOException e)
        {
            return null;
        }

    }

    /**
     * Deserializes model from file name
     * @param fileName
     * @return
     */
    public Map<String, GameState> deserializeModel(final String fileName)
    {
        try
        {
            return readFromFileStream(new FileInputStream(fileName));
        }
        catch(IOException | ClassNotFoundException e)
        {
            return null;
        }
    }

    /**
     * Reads game model from file object
     * @param file
     * @return map representing game model
     */
    public Map<String, GameState> readModelFromFile(final MultipartFile file)
    {
        try
        {
            return readFromFileStream(file.getInputStream());
        }
        catch(IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Deserializes file content from input stream.
     * @param inputStream
     * @return
     */
    public Map<String, GameState> readFromFileStream(final InputStream inputStream) throws IOException, ClassNotFoundException
    {
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        Map<String, GameState> model = (Map<String, GameState>) objectInputStream.readObject();
        objectInputStream.close();
        return model;
    }
}
