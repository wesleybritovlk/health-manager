package com.github.wesleybritovlk.healthmanager.app.healthproblem;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wesleybritovlk.healthmanager.app.healthproblem.HealthProblemDTO.Request;
import com.github.wesleybritovlk.healthmanager.app.healthproblem.HealthProblemDTO.Response;
import com.github.wesleybritovlk.healthmanager.handler.GlobalHandlerException;

@WebMvcTest(HealthProblemController.class)
class HealthProblemControllerTest {
        private MockMvc mockMvc;
        private final ObjectMapper objectMapper;

        @Autowired
        public HealthProblemControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
                this.mockMvc = mockMvc;
                this.objectMapper = objectMapper;
        }

        @MockBean
        private HealthProblemService service;

        @MockBean
        private GlobalHandlerException globalHandlerException;

        @Test
        void itShouldCreateHealthProblem_WithHealthProblemDTORequest() throws Exception {
                Request request = new Request(UUID.randomUUID(), "problem", BigInteger.ONE);
                when(service.create(any(Request.class)))
                                .thenReturn(Map.of("id", UUID.randomUUID(), "problem_name", request.hpName()));
                mockMvc.perform(post("/api/health-problems").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated()).andDo(print());
        }

        @Test
        void itShouldGetHealthProblemResponseById() throws Exception {
                UUID id = UUID.randomUUID();
                UUID customerId = UUID.randomUUID();
                Response response = new Response(id, customerId, "problem", BigInteger.ONE);
                when(service.findById(any(UUID.class))).thenReturn(response);
                mockMvc.perform(get("/api/health-problems/{id}", id))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content.id").value(id.toString()))
                                .andExpect(jsonPath("$.content.customer_id").value(customerId.toString()))
                                .andExpect(jsonPath("$.content.problem_name").value("problem"))
                                .andExpect(jsonPath("$.content.severity").value(1))
                                .andDo(print());
        }

        @Test
        void itShouldGetAllHealthProblemsResponses_byPageRequest() throws Exception {
                List<Response> responses = List.of(
                                new Response(UUID.randomUUID(), UUID.randomUUID(), "problem", BigInteger.ONE),
                                new Response(UUID.randomUUID(), UUID.randomUUID(), "problem1", BigInteger.TWO),
                                new Response(UUID.randomUUID(), UUID.randomUUID(), "problem2", BigInteger.ONE));
                Page<Response> responsesPage = new PageImpl<>(responses);
                when(service.findAll(any(Pageable.class))).thenReturn(responsesPage);
                mockMvc.perform(get("/api/health-problems"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content.size()").value(responses.size()))
                                .andDo(print());
        }

        @Test
        void itShouldUpdateHealthProblem_WithHealthProblemIdAndHealthProblemDTORequest() throws Exception {
                UUID id = UUID.randomUUID();
                Request request = new Request(UUID.randomUUID(), "problemUpdated", BigInteger.TWO);
                when(service.update(any(UUID.class), any(Request.class)))
                                .thenReturn(Map.of("id", id, "problem_name", request.hpName()));
                mockMvc.perform(put("/api/health-problems/{id}", id).contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk()).andDo(print());
        }

        @Test
        void itShouldDeleteHealthProblemById() throws Exception {
                UUID id = UUID.randomUUID();
                when(service.delete(any(UUID.class))).thenReturn(Map.of("id", id));
                mockMvc.perform(delete("/api/health-problems/{id}", id))
                                .andExpect(status().isOk())
                                .andDo(print());
        }
}
