package com.github.wesleybritovlk.healthmanager.app;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.github.wesleybritovlk.healthmanager.handler.GlobalHandlerException;

@WebMvcTest(ApiController.class)
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GlobalHandlerException globalHandlerException;

    @Test
    void itShouldReturnHomeApiResponse() throws Exception {
        mockMvc.perform(get("/api")).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Health Manager API"))
                .andExpect(jsonPath("$.version").value("0.0.1-SNAPSHOT"))
                .andDo(print());
    }
}
