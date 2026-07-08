package com.example.playerai.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MlPredictionControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getModelInfo_returns200AndExpectedFields() throws Exception {
        mockMvc.perform(get("/ml/model-info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelName", is("Capstone ML Predictor")))
                .andExpect(jsonPath("$.modelType", is("Java ML Predictor (XGBoost-style screen)")))
                .andExpect(jsonPath("$.trainingStatus", is("Demo / Ready for integration")))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.supportedFeatures").isArray())
                .andExpect(jsonPath("$.supportedFeatures", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void predict_returnsPredictionResponse() throws Exception {
        String requestJson = """
            {
              "playerId": 1,
              "playerName": "Bukayo Saka",
              "age": 23,
              "position": "RW",
              "matchesPlayed": 32,
              "goals": 16,
              "assists": 10,
              "minutesPlayed": 2650,
              "yellowCards": 4,
              "redCards": 0,
              "shotsOnTarget": 28,
              "passAccuracy": 85.7,
              "injuryStatus": false,
              "expectedGoals": 11.4,
              "expectedAssists": 8.2,
              "keyPasses": 41,
              "progressivePasses": 57,
              "dribblesCompleted": 39,
              "tacklesWon": 14,
              "interceptions": 9,
              "ballRecoveries": 37,
              "matchesMissed": 1,
              "recentMatchLoad": 4
            }
            """;

        mockMvc.perform(post("/ml/predict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerName", is("Bukayo Saka")))
                .andExpect(jsonPath("$.modelType", is("Java ML Predictor")))
                .andExpect(jsonPath("$.predictedScore").isNumber())
                .andExpect(jsonPath("$.riskLevel").exists())
                .andExpect(jsonPath("$.confidence").isNumber())
                .andExpect(jsonPath("$.summary").exists())
                .andExpect(jsonPath("$.topFeatures").isArray())
                .andExpect(jsonPath("$.topFeatures", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void predict_setsLowRiskForStrongInputs() throws Exception {
        String requestJson = """
            {
              "playerId": 2,
              "playerName": "Elite Player",
              "age": 24,
              "position": "FW",
              "matchesPlayed": 34,
              "goals": 22,
              "assists": 12,
              "minutesPlayed": 2900,
              "yellowCards": 2,
              "redCards": 0,
              "shotsOnTarget": 40,
              "passAccuracy": 88.5,
              "injuryStatus": false,
              "expectedGoals": 15.0,
              "expectedAssists": 9.0,
              "keyPasses": 45,
              "progressivePasses": 60,
              "dribblesCompleted": 35,
              "tacklesWon": 10,
              "interceptions": 6,
              "ballRecoveries": 30,
              "matchesMissed": 0,
              "recentMatchLoad": 3
            }
            """;

        mockMvc.perform(post("/ml/predict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riskLevel", is("LOW")));
    }

    @Test
    void predict_setsHigherRiskWhenInjuredAndOverloaded() throws Exception {
        String requestJson = """
            {
              "playerId": 3,
              "playerName": "Fatigued Player",
              "age": 29,
              "position": "CM",
              "matchesPlayed": 20,
              "goals": 2,
              "assists": 2,
              "minutesPlayed": 1400,
              "yellowCards": 5,
              "redCards": 1,
              "shotsOnTarget": 4,
              "passAccuracy": 75.0,
              "injuryStatus": true,
              "expectedGoals": 1.2,
              "expectedAssists": 1.0,
              "keyPasses": 8,
              "progressivePasses": 10,
              "dribblesCompleted": 4,
              "tacklesWon": 8,
              "interceptions": 5,
              "ballRecoveries": 16,
              "matchesMissed": 5,
              "recentMatchLoad": 7
            }
            """;

        mockMvc.perform(post("/ml/predict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riskLevel").value(org.hamcrest.Matchers.anyOf(is("MEDIUM"), is("HIGH"))));
    }
}