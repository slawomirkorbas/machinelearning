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

    /** Effectiveness factors recalculated after each game played **/
    double lossF = 1.00;
    double drawF = 1.00;
    double winF  = 1.00;

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
        this.winF  = (double)wdl.get(0);
        this.drawF = (double)wdl.get(1);
        this.lossF = (double)wdl.get(2);
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
        final double factorValue = 1; //((double)maxMoves/(double)totalMoves);
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
        return  (winF + drawF)/(winF + drawF + lossF) * 100;
    }
}
