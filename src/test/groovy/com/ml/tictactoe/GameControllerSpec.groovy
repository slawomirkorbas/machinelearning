package com.ml.tictactoe

import com.fasterxml.jackson.databind.ObjectMapper
import com.ml.tictactoe.model.GameState
import groovy.transform.TypeChecked
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.Rollback
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

//@TypeChecked
@SpringBootTest
@Rollback
class GameControllerSpec extends Specification
{
    MockMvc mockMvc

    @Autowired
    protected WebApplicationContext webApplicationContext

    @Autowired
    ObjectMapper objectMapper

    @Before
    void setupMockMvc()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
    }

    def "moveAndLearn: works as expected"()
    {
        given:
            GameState gameState = new GameState([[" ", " ", " " ], [" ", "o", " " ], [" ", " ", " " ]] as ArrayList<ArrayList<String>>)
            String gameStateJson = objectMapper.writeValueAsString(gameState)

        when:
            ResultActions result = mockMvc.perform(post("/tictactoe/moveAndLearn")
                                        .param("userFigure", "o")
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .content(gameStateJson))

        then:
            result.andExpect(status().isOk())
                    //.andExpect(content().json("{matrix:...}}"))
    }
}
