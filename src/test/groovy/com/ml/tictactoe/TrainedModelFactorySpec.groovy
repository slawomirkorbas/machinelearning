package com.ml.tictactoe

import com.ml.tictactoe.model.Field
import com.ml.tictactoe.model.GameState
import org.springframework.test.annotation.Rollback
import spock.lang.Specification

class TrainedModelFactorySpec extends Specification
{
    @Rollback
    def 'Serialization of model works as expected'()
    {
        given:
            final TrainedModelFactory modelFactory = new TrainedModelFactory()
            final Map<String, GameState> trainedModel = new HashMap<>()
            final String modelFileName = "trainedModelTest.txt";
        and:
            List gameExecutionPath = [
                    new GameState([[" ", " ", " " ],
                                   [" ", " ", " " ],
                                   [" ", " ", " " ]]),
                    new GameState([[" ", " ", " " ],
                                   [" ", "x", " " ],
                                   [" ", " ", " " ]], new Field(1,1,"x")),
                    new GameState([[" ", " ", " " ],
                                   [" ", "x", " " ],
                                   [" ", " ", "o" ]]),
                    new GameState([[" ", " ", " " ],
                                   [" ", "x", " " ],
                                   [" ", "x", "o" ]], new Field(2,1,"x")),
                    new GameState([[" ", " ", " " ],
                                   [" ", "x", "o" ],
                                   [" ", "x", "o" ]]),
                    new GameState([[" ", "x", " " ],
                                   [" ", "x", "o" ],
                                   [" ", "x", "o" ]], new Field(0,1,"x"))
            ]
        and:
            gameExecutionPath.stream().forEach({ gs ->
                trainedModel.put(gs.getKey(), gs)
            })

        when:
            modelFactory.serializeModel(trainedModel, modelFileName)
        and:
            final Map<String, GameState> deserializedModel = modelFactory.deserializeModel(modelFileName)

        then:
            deserializedModel.size() == trainedModel.size()
        and:
            deserializedModel.forEach( { k,v ->
                null != trainedModel.get(k)
            })

    }
}
