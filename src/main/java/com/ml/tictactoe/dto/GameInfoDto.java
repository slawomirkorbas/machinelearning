package com.ml.tictactoe.dto;

import com.ml.tictactoe.model.GameState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameInfoDto implements Serializable
{
    private static final long serialVersionUID = 1L;

    GameState gameState;
    Integer modelSize;
}
