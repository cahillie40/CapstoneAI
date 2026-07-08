package com.example.playerai.integration;

import com.example.playerai.entity.Player;
import com.example.playerai.repository.PlayerRepository;
import com.example.playerai.repository.PredictionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    PredictionRepository predictionRepository;

    private Long savedPlayerId;

    @BeforeEach
    void setUp() {
        predictionRepository.deleteAll();
        playerRepository.deleteAll();
        savedPlayerId = null;
    }

    @Test
    void getStats_returnsZeroedStatsWhenNoDataExists() throws Exception {
        mockMvc.perform(get("/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPlayers", is(0)))
                .andExpect(jsonPath("$.injuredPlayers", is(0)))
                .andExpect(jsonPath("$.totalPredictions", is(0)))
                .andExpect(jsonPath("$.averageFormRating", is(0.0)))
                .andExpect(jsonPath("$.averageExpectedGoals", is(0.0)))
                .andExpect(jsonPath("$.averageExpectedAssists", is(0.0)))
                .andExpect(jsonPath("$.averageBallRecoveries", is(0.0)));
    }

    @Test
    void getStats_returnsAggregatedPlayerAndPredictionStats() throws Exception {
        Player p1 = buildPlayer("Bukayo Saka", "Arsenal", "RW");
        p1.setFormRating(84.0);
        p1.setExpectedGoals(10.0);
        p1.setExpectedAssists(8.0);
        p1.setBallRecoveries(40);
        p1.setInjuryStatus(false);
        savedPlayerId = playerRepository.save(p1).getId();

        Player p2 = buildPlayer("Martin Odegaard", "Arsenal", "CAM");
        p2.setFormRating(80.0);
        p2.setExpectedGoals(6.0);
        p2.setExpectedAssists(9.0);
        p2.setBallRecoveries(50);
        p2.setInjuryStatus(true);
        playerRepository.save(p2);

        savePredictionForPlayer(savedPlayerId);

        mockMvc.perform(get("/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPlayers", is(2)))
                .andExpect(jsonPath("$.injuredPlayers", is(1)))
                .andExpect(jsonPath("$.totalPredictions", is(1)))
                .andExpect(jsonPath("$.averageFormRating", is(82.0)))
                .andExpect(jsonPath("$.averageExpectedGoals", is(8.0)))
                .andExpect(jsonPath("$.averageExpectedAssists", is(8.5)))
                .andExpect(jsonPath("$.averageBallRecoveries", is(45.0)));
    }

    @Test
    void getValidation_returnsExpectedValidationPayload() throws Exception {
        mockMvc.perform(get("/dashboard/validation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelName").exists())
                .andExpect(jsonPath("$.validationStatus").exists())
                .andExpect(jsonPath("$.accuracyEstimate").exists())
                .andExpect(jsonPath("$.summary").exists())
                .andExpect(jsonPath("$.strengths").isArray())
                .andExpect(jsonPath("$.limitations").isArray());
    }

    @Test
    void getModelExplanation_returnsExpectedExplanationPayload() throws Exception {
        mockMvc.perform(get("/dashboard/model-explanation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelType").exists())
                .andExpect(jsonPath("$.overview").exists())
                .andExpect(jsonPath("$.featureWeights").isArray())
                .andExpect(jsonPath("$.riskLogic").isArray());
    }

    private void savePredictionForPlayer(Long playerId) throws Exception {
        String requestJson = """
            {
              "playerId": %d,
              "predictionRequest": {
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
            }
            """.formatted(playerId);

        mockMvc.perform(post("/predictions/save")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    private Player buildPlayer(String name, String team, String position) {
        Player player = new Player();
        player.setName(name);
        player.setAge(24);
        player.setPosition(position);
        player.setTeam(team);
        player.setMatchesPlayed(30);
        player.setGoals(10);
        player.setAssists(8);
        player.setMinutesPlayed(2400);
        player.setYellowCards(3);
        player.setRedCards(0);
        player.setShotsOnTarget(22);
        player.setPassAccuracy(87.5);
        player.setFormRating(84.0);
        player.setInjuryStatus(false);

        player.setExpectedGoals(9.2);
        player.setExpectedAssists(6.8);
        player.setKeyPasses(40);
        player.setProgressivePasses(55);
        player.setDribblesCompleted(33);
        player.setTacklesWon(18);
        player.setInterceptions(12);
        player.setBallRecoveries(44);
        player.setMatchesMissed(1);
        player.setRecentMatchLoad(4);

        return player;
    }
}