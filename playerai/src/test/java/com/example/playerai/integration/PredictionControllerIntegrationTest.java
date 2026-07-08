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
class PredictionControllerIntegrationTest {

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
        savedPlayerId = playerRepository.save(buildPlayer("Bukayo Saka", "Arsenal", "RW")).getId();
    }

    @Test
    void predictFormRating_returnsPredictionResponse() throws Exception {
        String requestJson = """
            {
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

        mockMvc.perform(post("/predictions/form-rating")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baselineScore").isNumber())
                .andExpect(jsonPath("$.predictedFormRating").isNumber())
                .andExpect(jsonPath("$.riskLevel").exists())
                .andExpect(jsonPath("$.summary").exists())
                .andExpect(jsonPath("$.positiveFactors").isArray())
                .andExpect(jsonPath("$.negativeFactors").isArray())
                .andExpect(jsonPath("$.allFactors").isArray())
                .andExpect(jsonPath("$.scoreSteps").isArray());
    }

    @Test
    void savePrediction_persistsAndReturnsHistoryDto() throws Exception {
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
            """.formatted(savedPlayerId);

        mockMvc.perform(post("/predictions/save")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.playerId", is(savedPlayerId.intValue())))
                .andExpect(jsonPath("$.playerName", is("Bukayo Saka")))
                .andExpect(jsonPath("$.predictedFormRating").isNumber())
                .andExpect(jsonPath("$.riskLevel").exists())
                .andExpect(jsonPath("$.summary").exists())
                .andExpect(jsonPath("$.inputData").exists())
                .andExpect(jsonPath("$.createdAt").exists());

        mockMvc.perform(get("/predictions/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].playerId", is(savedPlayerId.intValue())))
                .andExpect(jsonPath("$[0].playerName", is("Bukayo Saka")));
    }

    @Test
    void getAllHistoryPaged_returnsPagedHistory() throws Exception {
        savePredictionForPlayer(savedPlayerId);
        savePredictionForPlayer(savedPlayerId);

        mockMvc.perform(get("/predictions/history/paged")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.number", is(0)));
    }

    @Test
    void getPlayerHistory_returnsHistoryForSpecificPlayer() throws Exception {
        savePredictionForPlayer(savedPlayerId);

        Long otherPlayerId = playerRepository.save(buildPlayer("Martin Odegaard", "Arsenal", "CAM")).getId();
        savePredictionForPlayer(otherPlayerId);

        mockMvc.perform(get("/predictions/history/{playerId}", savedPlayerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].playerId", is(savedPlayerId.intValue())));
    }

    private void savePredictionForPlayer(Long playerId) throws Exception {
        String requestJson = """
            {
              "playerId": %d,
              "predictionRequest": {
                "age": 24,
                "position": "CM",
                "matchesPlayed": 30,
                "goals": 8,
                "assists": 7,
                "minutesPlayed": 2400,
                "yellowCards": 3,
                "redCards": 0,
                "shotsOnTarget": 18,
                "passAccuracy": 87.0,
                "injuryStatus": false,
                "expectedGoals": 6.5,
                "expectedAssists": 5.9,
                "keyPasses": 29,
                "progressivePasses": 44,
                "dribblesCompleted": 20,
                "tacklesWon": 17,
                "interceptions": 11,
                "ballRecoveries": 38,
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