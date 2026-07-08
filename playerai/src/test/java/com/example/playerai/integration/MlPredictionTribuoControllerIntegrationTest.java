package com.example.playerai.integration;

import com.example.playerai.entity.MlPredictionTribuoHistory;
import com.example.playerai.repository.MlPredictionTribuoHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

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
class MlPredictionTribuoControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MlPredictionTribuoHistoryRepository historyRepository;

    @BeforeEach
    void setUp() {
        historyRepository.deleteAll();
    }

    @Test
    void getModelInfo_returns200AndExpectedFields() throws Exception {
        mockMvc.perform(get("/ml/tribuo/model-info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelName").exists())
                .andExpect(jsonPath("$.modelType").exists())
                .andExpect(jsonPath("$.trainingStatus").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.supportedFeatures").isArray());
    }

    @Test
    void predict_returnsPredictionAndSavesHistory() throws Exception {
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

        mockMvc.perform(post("/ml/tribuo/predict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerName", is("Bukayo Saka")))
                .andExpect(jsonPath("$.modelName").exists())
                .andExpect(jsonPath("$.modelType").exists())
                .andExpect(jsonPath("$.predictedScore").isNumber())
                .andExpect(jsonPath("$.riskLevel").exists())
                .andExpect(jsonPath("$.confidence").isNumber())
                .andExpect(jsonPath("$.summary").exists())
                .andExpect(jsonPath("$.topFeatures").isArray())
                .andExpect(jsonPath("$.topFeatures", hasSize(greaterThanOrEqualTo(1))));

        mockMvc.perform(get("/ml/tribuo/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].playerName", is("Bukayo Saka")));
    }

    @Test
    void getHistory_returnsSavedPredictionsInDescendingOrder() throws Exception {
        MlPredictionTribuoHistory older = new MlPredictionTribuoHistory();
        older.setPlayerId(1L);
        older.setPlayerName("Older Player");
        older.setAge(24);
        older.setPosition("CM");
        older.setMatchesPlayed(30);
        older.setGoals(7);
        older.setAssists(6);
        older.setMinutesPlayed(2200);
        older.setYellowCards(3);
        older.setRedCards(0);
        older.setShotsOnTarget(15);
        older.setPassAccuracy(84.0);
        older.setInjuryStatus(false);
        older.setExpectedGoals(5.5);
        older.setExpectedAssists(4.3);
        older.setKeyPasses(22);
        older.setProgressivePasses(31);
        older.setDribblesCompleted(14);
        older.setTacklesWon(19);
        older.setInterceptions(11);
        older.setBallRecoveries(42);
        older.setMatchesMissed(0);
        older.setRecentMatchLoad(3);
        older.setModelName("Tribuo Regression Predictor");
        older.setModelType("Tribuo Linear SGD Regression");
        older.setPredictedScore(68.4);
        older.setRiskLevel("MEDIUM");
        older.setConfidence(0.75);
        older.setSummary("Older prediction");
        older.setPredictedAt(LocalDateTime.now().minusDays(1));
        historyRepository.save(older);

        MlPredictionTribuoHistory newer = new MlPredictionTribuoHistory();
        newer.setPlayerId(2L);
        newer.setPlayerName("Newer Player");
        newer.setAge(22);
        newer.setPosition("LW");
        newer.setMatchesPlayed(29);
        newer.setGoals(13);
        newer.setAssists(9);
        newer.setMinutesPlayed(2400);
        newer.setYellowCards(2);
        newer.setRedCards(0);
        newer.setShotsOnTarget(21);
        newer.setPassAccuracy(87.2);
        newer.setInjuryStatus(false);
        newer.setExpectedGoals(9.0);
        newer.setExpectedAssists(7.1);
        newer.setKeyPasses(34);
        newer.setProgressivePasses(49);
        newer.setDribblesCompleted(30);
        newer.setTacklesWon(10);
        newer.setInterceptions(7);
        newer.setBallRecoveries(29);
        newer.setMatchesMissed(1);
        newer.setRecentMatchLoad(4);
        newer.setModelName("Tribuo Regression Predictor");
        newer.setModelType("Tribuo Linear SGD Regression");
        newer.setPredictedScore(82.1);
        newer.setRiskLevel("LOW");
        newer.setConfidence(0.88);
        newer.setSummary("Newer prediction");
        newer.setPredictedAt(LocalDateTime.now());
        historyRepository.save(newer);

        mockMvc.perform(get("/ml/tribuo/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].playerName", is("Newer Player")))
                .andExpect(jsonPath("$[1].playerName", is("Older Player")));
    }
}