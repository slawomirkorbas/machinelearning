package com.ml.tictactoe;

import com.ml.tictactoe.model.Field;
import com.ml.tictactoe.model.FieldCannotBeSetException;
import com.ml.tictactoe.model.GameState;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * TicTacToe engine capable for "learning" (optimizing). Based on subsequent games played.
 *
 * @author Slawomir Korbas
 */
public class Engine
{

    /**
     * Stores game states that occurred during learning process. Each of game state corresponds to the situation
     * "before" computer next move (or right after opponents move).
     */
    @Getter
    @Setter
    Map<String, GameState> trainedModel;

    /**
     *  Stores subsequent game states for specific games. The "trained model" is updated using this execution path
     *  when the game is finished. This is kind of one game history record.
     */
    List<GameState> gameExecutionPath = new ArrayList<>();
    List<GameState> mirrorGameExecutionPath = new ArrayList<>();
    List<GameState> transposedGameExecutionPath = new ArrayList<>();
    List<GameState> transposedMirrorGameExecutionPath = new ArrayList<>();

    /**
     * Default constructor
     * @param trainedModel
     */
    public Engine(Map<String, GameState> trainedModel)
    {
        this.trainedModel = trainedModel;
    }


    public GameState doMove(GameState gameState, String computerFigure) throws Exception
    {
        if(!gameState.isOver(computerFigure))
        {
            final GameState trainedGameState = getTrainedModelGameState(gameState);
            gameState.addNextMove(findBestFieldForTheNextMove(trainedGameState), computerFigure);
            return new GameState(gameState.getMatrix());
        }

        gameState.isOver(computerFigure);
        return gameState;
    }

    /**
     *
     * @param gameState
     * @param computerFigure
     * @param usersRow
     * @param userCol
     * @return
     * @throws FieldCannotBeSetException
     */
    public GameState doMoveAndUpdateModForParallelGames(GameState gameState, String computerFigure, int usersRow, int userCol) throws FieldCannotBeSetException
    {
        GameState mirrorGameState = gameState.createMirror();
        GameState transposedGameState = gameState.createTransposition();
        GameState transposedMirrorGameState = transposedGameState.createMirror();

        doMoveAndUpdateModel(mirrorGameState, mirrorGameExecutionPath, computerFigure, usersRow, userCol);
        doMoveAndUpdateModel(transposedGameState, transposedGameExecutionPath, computerFigure, usersRow, userCol);
        doMoveAndUpdateModel(transposedMirrorGameState, transposedMirrorGameExecutionPath, computerFigure, usersRow, userCol);

        return doMoveAndUpdateModel(gameState, gameExecutionPath, computerFigure, usersRow, userCol);

    }


    /**
     * This method should be called in "learning mode".
     * @param gameState - game state returned by the opponent
     * @param computerFigure - figure that should be used by the machine 'x' or 'o'
     * @return updated GameState with the computer's move
     */
    public GameState doMoveAndUpdateModel(GameState gameState, final List<GameState> executionPath, String computerFigure, int usersRow, int userCol) throws FieldCannotBeSetException
    {
        if(gameState.isNewGame(computerFigure))
        {
            executionPath.clear(); //game just started so clear current game execution path
        }

        executionPath.add(new GameState(gameState.getMatrix())); //add game state to execution path

        GameState currentGameState = gameState;
        if(!currentGameState.isOver(computerFigure))
        {
            //lookup for the game state in the model...or if not found then creates new game state from scratch
            final GameState trainedGameState = getTrainedModelGameState(currentGameState);

            //find the "best" move(field) and adds to the game...
            currentGameState = new GameState(gameState.getMatrix());
            Field nextMove = findBestFieldForTheNextMove(trainedGameState);
            currentGameState.addNextMove(nextMove, computerFigure);

            // after the move has been done record NEW game state to execution path history
            executionPath.add(currentGameState);
        }

        if(currentGameState.isOver(computerFigure))
        {
            updateTrainedModel(currentGameState, executionPath);
        }

        return currentGameState;
    }


    /**
     * Train the model by updating W/D/L percentage factors
     * @param finalGameState
     * @param gameExecutionPath
     */
    void updateTrainedModel(GameState finalGameState, final List<GameState> gameExecutionPath)
    {
        Field computerMovedTo = null;
        // iterate backward over game execution path and update W/D/L factors for moves that has been made
        final int gameSize = gameExecutionPath.size();
        for(int i = gameSize - 1; i >= 0; i--)
        {
            GameState executedState = gameExecutionPath.get(i);
            if(executedState.getComputerMove() != null)
            {
                computerMovedTo = executedState.getComputerMove();
                continue;
            }

            GameState trainedGameState = trainedModel.get(executedState.getKey());
            if(trainedGameState != null)
            {
                //recalculate effectiveness factor for the field which computer used for the  next move...
                int row = computerMovedTo.getRow();
                int col = computerMovedTo.getCol();
                int totalNumberOfMoves = (int) finalGameState.getFieldsOccupied();
                trainedGameState.getMatrix()[row][col].recalculateEffectiveness(finalGameState.getGameResult(), totalNumberOfMoves);
            }
        }
    }


    /**
     * Finds "best" unoccupied field according to  (win+draw/loss) % ratio
     * @return
     */
    public Field findBestFieldForTheNextMove(GameState trainedModelGameState)
    {
        //convert 2D matrix to stream of fields and leave only free
        Field suggestedMove = null;
        final Field[][] matrix = trainedModelGameState.getMatrix();
        List<Field> freeFieldList = Arrays.stream(matrix).flatMap(f -> Stream.of(f)).filter(Field::isFree).collect(Collectors.toList());
        if( !freeFieldList.isEmpty() )
        {
            //find one field having best (win+draw/loss) ratio
            final Field maxRatioField = freeFieldList.stream().max(Comparator.comparing(Field::effectiveness)).get();

            // filter again fields having maxRatio (there may me more having same ratio)
            //final List<Field> bestFields = freeFieldList.stream().filter(f -> f.effectiveness() == maxRatioField.effectiveness()).collect(Collectors.toList());

            // pick randomly element from the "best" field list
            //suggestedMove = bestFields.get((new Random()).nextInt(bestFields.size()));
            suggestedMove = maxRatioField;
        }
        return suggestedMove;
    }

    /**
     * Retrievs a game state from trained model. If there is not specific game state the new entry is created.
     * @param currentGameState
     */
    public GameState getTrainedModelGameState(final GameState currentGameState)
    {
        GameState trainedGameState = trainedModel.get(currentGameState.getKey());
        if(trainedGameState == null)
        {
            trainedGameState = new GameState(currentGameState.getMatrix());
            trainedModel.put(currentGameState.getKey(), trainedGameState);
        }
        return trainedGameState;
    }

}
