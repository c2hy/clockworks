package io.github.c2hy.zilean.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FirstHttpApi.class)
class FirstHttpApiTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void respondHelloText() throws Exception {
        mockMvc.perform(get("/hello-text"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}