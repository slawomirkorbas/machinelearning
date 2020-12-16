package com.ml.tictactoe.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class representing specific field within tic-tac-toe board
 *
 * @author Slawomir Korbas
 */
@Data
@NoArgsConstructor
public class Field implements Serializable
{
    private static final long serialVersionUID = 1L;
    Integer row  = null;
    Integer col  = null;
    String value = null;
    boolean inline = false;

    /** Effectiveness factors recalculated after each game played **/
    double lossF = 0;
    double drawF = 0;
    double winF  = 0;

    public Field(Integer row, Integer col, String value)
    {
        this.row = row;
        this.col = col;
        this.value = value;
    }

    public Field(Integer row, Integer col, String value, ArrayList<Integer> wdl)
    {
        this.row = row;
        this.col = col;
        this.value = value;
    }

    public boolean isFree()
    {
        return value.trim().isEmpty();
    }

    public boolean isOccupied()
    {
        return !value.trim().isEmpty();
    }


    public void recalculateEffectiveness(GameResult result, final int totalMoves)
    {
        final int maxMoves = 9;
        final double factorValue = ((double)maxMoves/(double)totalMoves);
        switch(result)
        {
            case WIN:
                this.winF += factorValue;
                break;
            case DRAW:
                this.drawF += factorValue;
                break;
            case LOSS:
                this.lossF += factorValue;
                break;
        }
    }


    public double effectiveness()
    {
        return (1 + winF + drawF*0.5)/(1 + lossF);
    }
}
