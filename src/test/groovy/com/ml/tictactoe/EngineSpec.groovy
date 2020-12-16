package com.ml.tictactoe

import com.ml.tictactoe.model.Field
import com.ml.tictactoe.model.GameResult
import com.ml.tictactoe.model.GameState
import spock.lang.Specification
import java.util.stream.Collectors
import java.util.stream.Stream


class EngineSpec extends Specification
{
    def 'findBestFieldForTheNextMove: returns null when all matrix is occupied'()
    {
        given:
            Engine engine = new Engine(new HashMap<>())
        and:
            def matrix = [ ["x", "o", "x" ],
                           ["x", "o", "x" ],
                           ["x", "o", "x" ] ]

            GameState gameState = new GameState(matrix)

        expect:
            null == engine.findBestFieldForTheNextMove(gameState)
    }

    def 'findBestFieldForTheNextMove: returns the last position available'()
    {
        given:
            Map<String, GameState> trainedModel = new HashMap<>()
            Engine engine = new Engine(trainedModel)
        and:
            def matrix = [ ["x", "o", "x" ],
                           ["x", "o", "x" ],
                           ["x", "o", " " ] ]

        GameState gameState = new GameState(matrix)

        when:
            Field field = engine.findBestFieldForTheNextMove(gameState)
        then:
            field.getRow() == 2
            field.getCol() == 2
    }


    def 'findBestFieldForTheNextMove: returns the field having highest effectiveness ratio in trained model'()
    {
        given:
            Engine engine = new Engine(new HashMap<>())
        and:
            Field[][] matrix = new Field[3][3]
            matrix[0][0] = new Field(0,0,"x")
            matrix[0][1] = new Field(0,1,"o")
            matrix[0][2] = new Field(0, 2, " ", [1,2,3])
            matrix[1][0] = new Field(1,0,"x")
            matrix[1][1] = new Field(1,1, " ", [4,1,8])
            matrix[1][2] = new Field(1,2,"o")
            matrix[2][0] = new Field(2, 0, " ", [2,4,2])
            matrix[2][1] = new Field(2, 1, " ", [4,2,2])
            matrix[2][2] = new Field(2,3,"x")

            GameState gameState = new GameState(matrix)

        when:
            Field field = engine.findBestFieldForTheNextMove(gameState)
        then:
            field.getRow() == 0
            field.getCol() == 2
    }



    def 'updateTrainedModel: computer won the game so the effectiveness factor should increase for all computer moves'()
    {
        given:
            Map<String, GameState> trainedModel = new HashMap<>()
            Engine engine = new Engine(trainedModel)
        and:
            GameState gm01 = new GameState([[" ", " ", " " ],
                                            [" ", " ", " " ],
                                            [" ", " ", " " ]])
            GameState gm02 = new GameState([[" ", " ", " " ],
                                            [" ", "x", " " ],
                                            [" ", " ", "o" ]])
            GameState gm03 = new GameState([[" ", " ", " " ],
                                            [" ", "x", "o" ],
                                            [" ", "x", "o" ]])
            trainedModel.put(gm01.getKey(), gm01)
            trainedModel.put(gm02.getKey(), gm02)
            trainedModel.put(gm03.getKey(), gm03)

        and:
            GameState finalGameState = new GameState([[" ", "x", " " ],
                                                      [" ", "x", "o" ],
                                                      [" ", "x", "o" ]], new Field(0,1,"x"))
            finalGameState.setGameResult(GameResult.WIN)
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
                    finalGameState

            ]

        when:
            engine.updateTrainedModel(finalGameState, gameExecutionPath)
        then:
            trainedModel.get(gameExecutionPath.get(0).getKey()).getMatrix()[1][1].effectiveness() == 2.8
            trainedModel.get(gameExecutionPath.get(2).getKey()).getMatrix()[2][1].effectiveness() == 2.8
            trainedModel.get(gameExecutionPath.get(4).getKey()).getMatrix()[0][1].effectiveness() == 2.8

    }


    def 'doMoveAndUpdateModel: works as expected'()
    {
        given:
             Engine engine = new Engine(new HashMap<>())
        and:
            GameState gameState = new GameState([[" ", " ", " " ],
                                                 [" ", " ", " " ],
                                                 [" ", " ", " " ]])

        when:
            while( !gameState.isOver( ) )
            {
                List<Field> freeFields = Arrays.stream(gameState.getMatrix()).flatMap(
                        { f -> Stream.of(f) }).filter(
                        { f -> f.isFree() }).collect(Collectors.toList());

                if(freeFields.isEmpty())
                {
                    break;
                }
                Field freeField = freeFields.get((new Random()).nextInt(freeFields.size()));
                gameState.getMatrix()[freeField.getRow()][freeField.getCol()].setValue("o")
                gameState = engine.doMoveAndUpdateModel(gameState, "x")
            }
        then:
            true == gameState.isOver()
        and:
            engine.getTrainedModel().size() > 0
    }
}
