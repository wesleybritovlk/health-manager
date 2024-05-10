package com.github.wesleybritovlk.healthmanager.app.customer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.github.wesleybritovlk.healthmanager.app.customer.Customer.Sex;
import com.github.wesleybritovlk.healthmanager.app.customer.CustomerDTO.Request;
import com.github.wesleybritovlk.healthmanager.app.customer.CustomerDTO.Response;
import com.github.wesleybritovlk.healthmanager.handler.GlobalHandlerException;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {
        private final MockMvc mockMvc;
        private final ObjectMapper objectMapper;

        @Autowired
        public CustomerControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
                this.mockMvc = mockMvc;
                this.objectMapper = objectMapper;
        }

        @MockBean
        private CustomerService service;

        @MockBean
        private GlobalHandlerException globalHandlerException;    

        @Test
        void itShouldCreateCustomer_WithCustomerDTORequest() throws Exception {
                Request request = new Request("foo", LocalDate.parse("1997-05-23"), Sex.MALE);
                when(service.create(any(Request.class)))
                                .thenReturn(Map.of("id", UUID.randomUUID(), "full_name", request.name()));
                mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated()).andDo(print());
        }

        @Test
        void itShouldGetCustomerResponseById() throws Exception {
                UUID id = UUID.randomUUID();
                Response response = new Response(id, "foo", LocalDate.parse("1997-05-23"), Sex.MALE,
                                BigDecimal.ZERO.setScale(2), Set.of());
                when(service.findById(any(UUID.class))).thenReturn(response);
                mockMvc.perform(get("/api/customers/{id}", id))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content.id").value(id.toString()))
                                .andExpect(jsonPath("$.content.full_name").value("foo"))
                                .andExpect(jsonPath("$.content.date_birth").value("1997-05-23"))
                                .andExpect(jsonPath("$.content.sex").value("MALE"))
                                .andExpect(jsonPath("$.content.score").value(0.00))
                                .andDo(print());
        }

        @Test
        void itShouldGetAllCustomersResponses_byPageRequest() throws Exception {
                List<Response> responses = List.of(
                                new Response(UUID.randomUUID(), "fooM", LocalDate.parse("1997-05-23"), Sex.MALE,
                                                BigDecimal.ZERO.setScale(2), Set.of()),
                                new Response(UUID.randomUUID(), "fooF", LocalDate.parse("1996-03-30"), Sex.FEMALE,
                                                BigDecimal.ZERO.setScale(2), Set.of()),
                                new Response(UUID.randomUUID(), "fooUN", LocalDate.parse("1993-06-30"), Sex.NOT_KNOW,
                                                BigDecimal.ZERO.setScale(2), Set.of()));
                Page<Response> responsesPage = new PageImpl<>(responses);
                when(service.findAll(any(Pageable.class))).thenReturn(responsesPage);
                mockMvc.perform(get("/api/customers"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content.size()").value(responses.size()))
                                .andDo(print());
        }

        @Test
        void itShouldUpdateCustomer_WithCustomerIdAndCustomerDTORequest() throws Exception {
                UUID id = UUID.randomUUID();
                Request request = new Request("fooUpdated", LocalDate.parse("1997-05-23"), Sex.MALE);
                when(service.update(any(UUID.class), any(Request.class)))
                                .thenReturn(Map.of("id", id, "full_name", request.name()));
                mockMvc.perform(put("/api/customers/{id}", id).contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk()).andDo(print());
        }

        @Test
        void itShouldDeleteCustomerById() throws Exception {
                UUID id = UUID.randomUUID();
                when(service.delete(any(UUID.class))).thenReturn(Map.of("id", id));
                mockMvc.perform(delete("/api/customers/{id}", id))
                                .andExpect(status().isOk())
                                .andDo(print());
        }
}
