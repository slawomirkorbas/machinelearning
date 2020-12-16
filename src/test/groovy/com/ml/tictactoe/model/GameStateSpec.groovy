package com.ml.tictactoe.model

import spock.lang.Specification
import spock.lang.Unroll

class GameStateSpec extends Specification
{
    @Unroll
    def "GetKey: works as expected"()
    {
        given:
            GameState gameState = new GameState(matrix)

        expect:
            expectedKey == (gameState.getKey())

        where:
            expectedKey | matrix
            "_________" | [ [" ", " ", " " ], [" ", " ", " " ], [" ", " ", " " ] ]
            "xoxxoxxox" | [ ["x", "o", "x" ], ["x", "o", "x" ], ["x", "o", "x" ] ]
            "xox___xox" | [ ["x", "o", "x" ], [" ", " ", " " ], ["x", "o", "x" ] ]
            "_oxxoxxo_" | [ [" ", "o", "x" ], ["x", "o", "x" ], ["x", "o", " " ] ]
            "_oxxox___" | [ [" ", "o", "x" ], ["x", "o", "x" ], [" ", " ", " " ] ]
            "_o__o__o_" | [ [" ", "o", " " ], [" ", "o", " " ], [" ", "o", " " ] ]
    }


    @Unroll
    def 'isOver: returns true if game is finished otherwise false and updates game result'()
    {
        given:
            GameState gameState = new GameState(matrix)

        expect:
            expOver == gameState.isOver("x")
        and:
            expResult == gameState.getGameResult()

        where:
            matrix                                                  | expResult       | expOver
            [[" ", " ", " " ], [" ", " ", " " ], [" ", " ", " " ]]  | null            | false
            [[" ", "x", " " ], [" ", " ", " " ], [" ", " ", " " ]]  | null            | false
            [[" ", "x", " " ], [" ", " ", " " ], [" ", " ", "o" ]]  | null            | false
            [[" ", "x", " " ], [" ", " ", " " ], ["o", " ", "o" ]]  | null            | false
            [["x", "o", " " ], ["x", "o", " " ], ["x", " ", " " ]]  | GameResult.WIN  | true
            [["x", "o", " " ], ["x", "o", " " ], [" ", "o", " " ]]  | GameResult.LOSS | true
            [["x", "o", " " ], ["o", "x", " " ], [" ", "o", "x" ]]  | GameResult.WIN  | true
            [["x", " ", " " ], [" ", "x", " " ], [" ", " ", "x" ]]  | GameResult.WIN  | true
            [[" ", " ", "o" ], [" ", "o", " " ], ["o", " ", " " ]]  | GameResult.LOSS | true
            [["x", "o", "o" ],
             ["o", "x", "x" ],
             ["x", "o", "o" ]]  | GameResult.DRAW | true
    }


    @Unroll
    def 'isNewGame: works as expected'()
    {
        given:
            GameState gameState = new GameState(matrix)

        expect:
            newGame == gameState.isNewGame(computerFigure)

        where:
            matrix                                                  | computerFigure | newGame
            [[" ", " ", " " ], [" ", " ", " " ], [" ", " ", " " ]]  | "x"            | true
            [[" ", " ", " " ], [" ", " ", " " ], [" ", " ", " " ]]  | "o"            | true
            [[" ", "x", " " ], [" ", " ", " " ], [" ", " ", " " ]]  | "o"            | true
            [[" ", "o", " " ], [" ", " ", " " ], [" ", " ", " " ]]  | "x"            | true
            [[" ", "o", " " ], [" ", " ", " " ], [" ", " ", " " ]]  | "o"            | false
            [[" ", "x", " " ], [" ", " ", " " ], [" ", " ", " " ]]  | "x"            | false
            [["x", "o", " " ], ["x", "o", " " ], ["x", " ", " " ]]  | "x"            | false
            [["x", "o", " " ], ["x", "o", " " ], [" ", "o", " " ]]  | "x"            | false
            [["x", "o", " " ], ["o", "x", " " ], [" ", "o", "x" ]]  | "x"            | false
    }
}
