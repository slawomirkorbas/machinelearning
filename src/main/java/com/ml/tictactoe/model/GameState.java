package com.ml.tictactoe.model;

import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Class representing state of the game.
 *
 * @author Slawomir Korbas
 */
@Data
public class GameState implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** result of the game **/
    GameResult gameResult = null;

    /** matrix of fields representing the tic-tac-toe board**/
    Field[][] matrix = null;

    /** Stores information about last computer move **/
    Field computerMove = null;

    /** Last move in a game causing WIN, LOSS or DRAW **/
    Field finishingMove = null;

    public GameState()
    {
        matrix = new Field[3][3];
        for(int i = 0; i < matrix.length; i++)
        {
            for(int j = 0; j < matrix.length; j++)
            {
                this.matrix[i][j] = new Field(i, j, " ");
            }
        }
    }

    public GameState(String[][] inputMatrix)
    {
        this.matrix = new Field[matrix.length][matrix.length];
        for(int i = 0; i < inputMatrix.length; i++)
        {
            for(int j = 0; j < inputMatrix.length; j++)
            {
                this.matrix[i][j] = new Field(i, j, inputMatrix[i][j]);
            }
        }
    }

    public GameState(ArrayList<ArrayList<String>> inputMatrix)
    {
        this.matrix = new Field[inputMatrix.size()][inputMatrix.size()];
        for(int i = 0; i < inputMatrix.size(); i++)
        {
            for(int j = 0; j < inputMatrix.size(); j++)
            {
                this.matrix[i][j] = new Field(i, j, inputMatrix.get(i).get(j));
            }
        }
    }

    public GameState(ArrayList<ArrayList<String>> inputMatrix, final Field computerMoveField)
    {
        this.matrix = new Field[inputMatrix.size()][inputMatrix.size()];
        for(int i = 0; i < inputMatrix.size(); i++)
        {
            for(int j = 0; j < inputMatrix.size(); j++)
            {
                this.matrix[i][j] = new Field(i, j, inputMatrix.get(i).get(j));
            }
        }
        this.computerMove = computerMoveField;
    }

    public GameState(Field[][] inputMatrix)
    {
        this.matrix = new Field[inputMatrix.length][inputMatrix.length];
        for(int i = 0; i < inputMatrix.length; i++)
        {
            for(int j = 0; j < inputMatrix.length; j++)
            {
                this.matrix[i][j] = new Field(i, j, inputMatrix[i][j].value);
            }
        }
    }

    private class KeyGen
    {
        String key = "";
    }
    /**
     * Returns 8-characters key identifying specific game state.
     * @return string - key eg. "x_xoo___";
     */
    public String getKey()
    {
        final KeyGen keyGen = new KeyGen();
        Arrays.stream(matrix).flatMap(f -> Stream.of(f)).forEach( f -> {
            keyGen.key += f.getValue().trim().isEmpty() ? "_" : f.getValue();
        });
        return keyGen.key;
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;

        return this.getKey().equals(((GameState)o).getKey());
    }

    @Override
    public int hashCode()
    {
        return this.getKey().hashCode();
    }

    /**
     * Returns true if the matrix is empty
     * @return
     */
    public boolean isEmpty()
    {
        if(matrix == null)
        {
            return true;
        }

        //convert 2D matrix to stream of fields and checks if there is any occupied
        return !Arrays.stream(matrix).flatMap(f -> Stream.of(f)).anyMatch(Field::isOccupied);
    }

    /**
     * Returns number of fields occupied on the board
     * @return
     */
    public long getFieldsOccupied()
    {
        return Arrays.stream(matrix).flatMap(f -> Stream.of(f)).filter(Field::isOccupied).count();
    }

    /**
     * Returns first occupied field within the board
     * @return
     */
    public Field getFirstOccupiedField()
    {
        List<Field> occupiedFields = Arrays.stream(matrix).flatMap(f -> Stream.of(f)).filter(Field::isOccupied).collect(Collectors.toList());
        return occupiedFields.size() > 0 ? occupiedFields.get(0) : null;
    }

    /**
     * Update field value and saves "this" decision for further machine learning "update" model process.
     * Throws an exception if the field is already occupied or is beyond matrix limits
     * @param suggestedField
     * @param figure
     */
    public void addNextMove(Field suggestedField, String figure) throws FieldCannotBeSetException
    {
        final int row = suggestedField.getRow();
        final int col = suggestedField.getCol();
        if( row < 0 || row > 2 || col < 0 || col > 2 || matrix[row][col].isOccupied())
        {
            throw new FieldCannotBeSetException();
        }

        matrix[row][col].setValue(figure);

        // stores last computer move ("decision")
        computerMove = suggestedField;
    }


    /**
     * Check if the game is finished by examining GameState's matrix values
     * @param computerFigure - figure used by the machine ('x' or 'o')
     * @return true if the game is over
     */
    public boolean isOver(final String computerFigure)
    {
        if(gameResult == null) //examine matrix and calculate final game result
        {
            for(int i = 0; i < matrix.length; i++)
            {
                boolean inline = false;
                for(int j = 0; j < matrix.length; j++)
                {
                    inline = (matrix[i][0].value.equals(matrix[i][j].value));
                    if(!inline)
                        break;
                }
                if(inline && !matrix[i][0].value.trim().isEmpty())
                {
                    gameResult = matrix[i][0].value.equals(computerFigure) ? GameResult.WIN : GameResult.LOSS;
                    return true;
                }
            }

            for(int j = 0; j < matrix.length; j++)
            {
                boolean inline = false;
                for(int i = 0; i < matrix.length; i++)
                {
                    inline = (matrix[0][j].value.equals(matrix[i][j].value));
                    if(!inline)
                        break;
                }
                if(inline && !matrix[0][j].value.trim().isEmpty())
                {
                    gameResult = matrix[0][j].value.equals(computerFigure) ? GameResult.WIN : GameResult.LOSS;
                    return true;
                }
            }

            if(!matrix[0][0].value.trim().isEmpty() && matrix[0][0].value.equals(matrix[1][1].value) && matrix[1][1].value.equals(matrix[2][2].value))
            {
                gameResult = matrix[0][0].value.equals(computerFigure) ? GameResult.WIN : GameResult.LOSS;
                return true;
            }

            if(!matrix[2][0].value.trim().isEmpty() && matrix[2][0].value.equals(matrix[1][1].value) &&  matrix[1][1].value.equals(matrix[0][2].value))
            {
                gameResult = matrix[0][2].value.equals(computerFigure) ? GameResult.WIN : GameResult.LOSS;
                return true;
            }

        }

        if(gameResult == null)
        {
            gameResult = Arrays.stream(matrix).flatMap(f -> Stream.of(f)).anyMatch(Field::isFree) ? null : GameResult.DRAW;
        }

        return gameResult != null;
    }

    /**
     * Return true if the user just started a game or the game just have been initiated by the machine (empty board).
     * @param computerFigure
     * @return
     */
    public boolean isNewGame(final String computerFigure)
    {
        boolean isNewGame = false;
        List<Field> occupiedFields = Arrays.stream(matrix).flatMap(f -> Stream.of(f)).filter(Field::isOccupied).collect(Collectors.toList());
        if( occupiedFields.size() == 0 )
        {
            isNewGame = true;
        }
        else if( occupiedFields.size() == 1 && !computerFigure.equals(occupiedFields.get(0).getValue()))
        {
            isNewGame = true;
        }
        return isNewGame;
    }

    /**
     * Creates new game state (matrix) which is a transposition of this game state
     * Rows in source matrix become columns in output matrix
     * Columns in source matrix become rows in output matrix
     * @return transpositioned matrix
     */
    public GameState createTransposition()
    {
        Field transposedComputerMove = null;
        final Field[][] transposedMatrix = new Field[matrix.length][matrix.length];
        for(int i=0;i<matrix.length;i++){
            for(int j=0;j<matrix.length;j++){
                transposedMatrix[i][j] = new Field(j, i , matrix[j][i].value);

                // find the move marked as made by the computer and also transpose it
                if(computerMove != null && computerMove.getRow() == j && computerMove.getCol() == i)
                {
                    transposedComputerMove = new Field(j, i, computerMove.getValue());
                }
            }
        }
        GameState transposedGameState = new GameState(transposedMatrix);
        transposedGameState.setComputerMove(transposedComputerMove);
        return transposedGameState;
    }

    /**
     * Creates mirror of the matrix
     * @return
     */
    public GameState createMirror()
    {
        Field mirroredComputerMove = null;
        final Field[][] mirroredMatrix = new Field[matrix.length][matrix.length];
        for(int i=0;i<matrix.length;i++) {
            int imageColumn = 0;
            for(int j=(matrix.length-1); j>=0; j--) {
                mirroredMatrix[i][imageColumn] = new Field(i, j, matrix[i][j].value);

                // find the move marked as made by the computer and also mirror it
                if(computerMove != null && computerMove.getRow() == i && computerMove.getCol() == j)
                {
                    mirroredComputerMove = new Field(i, imageColumn, computerMove.getValue());
                }
                imageColumn++;
            }
        }
        GameState mirrorGameState = new GameState(mirroredMatrix);
        mirrorGameState.setComputerMove(mirroredComputerMove);
        return mirrorGameState;
    }


    /**
     * Format gameState efficiency board with percentage values of field effectiveness concerning potential next move.
     * @return
     */
    public String toEfficiencyBoard()
    {
        String board = formatEfficiency(matrix[0][0]) + "|" + formatEfficiency(matrix[0][1]) + "|" + formatEfficiency(matrix[0][1]) + "\n";
        board += " " + formatEfficiency(matrix[1][0]) + "|" + formatEfficiency(matrix[1][1]) + "|" + formatEfficiency(matrix[1][2]) + "\n";
        board += " " + formatEfficiency(matrix[2][0]) + "|" + formatEfficiency(matrix[2][1]) + "|" + formatEfficiency(matrix[2][2]) + "\n";
        return board;
    }

    private String formatEfficiency(final Field field)
    {
        return field.isFree() ? String.valueOf((int)(Math.round(field.effectiveness()))) : (field.value + " ");
    }
}
