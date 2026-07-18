package com.example.playerai.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ValidationControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getValidationSummary_returnsExpectedStaticPayload() throws Exception {
        mockMvc.perform(get("/predictions/validation-summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelName", is("Player Performance Prediction Model")))
                .andExpect(jsonPath("$.validationStatus", is("Validated")))
                .andExpect(jsonPath("$.accuracyEstimate", is("Rule-based analytical model")))
                .andExpect(jsonPath("$.summary").exists())
                .andExpect(jsonPath("$.strengths").isArray())
                .andExpect(jsonPath("$.strengths", hasSize(greaterThanOrEqualTo(1))));
    }
}