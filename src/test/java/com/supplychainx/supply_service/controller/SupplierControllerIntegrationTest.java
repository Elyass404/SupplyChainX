package com.supplychainx.supply_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplychainx.supply_service.dto.request.SupplierRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @SpringBootTest: Starts the whole application (Server, Beans, DB connection)
@SpringBootTest
// @AutoConfigureMockMvc: Creates the "Fake Postman" for us
@AutoConfigureMockMvc
// @ActiveProfiles("test"): Tells Spring to load 'application-test.yml' (H2 DB)
@ActiveProfiles("test")
public class SupplierControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // the tool to send requests

    @Autowired
    private ObjectMapper objectMapper; // it converts Java Objects -> JSON String

    @Test
    void createSupplier_ShouldSaveAndReturn201() throws Exception {
        // 1. ARRANGE: Create the data we want to send
        // now I will build an object of the supplier request using the builder
        SupplierRequest request = SupplierRequest.builder()
                .name("ilyass company")
                .email("ilyass@company.com")
                .rating(7.5)
                .leadTime(10)
                .build();

        // 2. ACT & ASSERT: Send POST request and verify response
        mockMvc.perform(post("/api/v1/suppliers")
                        .contentType(MediaType.APPLICATION_JSON) // We are sending JSON
                        .content(objectMapper.writeValueAsString(request))) // Convert object to JSON string

                // Check the results
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.name").value("ilyass company")) // Check JSON response body
                .andExpect(jsonPath("$.email").value("ilyass@company.com"))
                .andExpect(jsonPath("$.id").exists()); // Verify an ID was generated
    }
}