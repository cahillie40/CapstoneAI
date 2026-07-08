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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CsvExportControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    PredictionRepository predictionRepository;

    private Long playerId;

    @BeforeEach
    void setUp() {
        predictionRepository.deleteAll();
        playerRepository.deleteAll();
        playerId = playerRepository.save(buildPlayer("Bukayo Saka", "Arsenal", "RW")).getId();
    }

    @Test
    void exportPredictionsCsv_returnsHeaderOnlyWhenNoPredictionsExist() throws Exception {
        mockMvc.perform(get("/export/predictions/csv"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", containsString("text/csv")))
                .andExpect(header().string("Content-Disposition", containsString("prediction-history.csv")))
                .andExpect(content().string("ID,Player Name,Player ID,Predicted Form Rating,Risk Level,Summary,Created At\n"));
    }

    @Test
    void exportPredictionsCsv_returnsPredictionRowsWhenHistoryExists() throws Exception {
        savePredictionForPlayer(playerId);

        mockMvc.perform(get("/export/predictions/csv"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", containsString("text/csv")))
                .andExpect(header().string("Content-Disposition", containsString("prediction-history.csv")))
                .andExpect(content().string(containsString("ID,Player Name,Player ID,Predicted Form Rating,Risk Level,Summary,Created At")))
                .andExpect(content().string(containsString("Bukayo Saka")))
                .andExpect(content().string(containsString(String.valueOf(playerId))));
    }

    @Test
    void exportPredictionsCsv_escapesCommaContainingSummary() throws Exception {
        savePredictionForPlayer(playerId);

        mockMvc.perform(get("/export/predictions/csv"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"")))
                .andExpect(content().string(containsString("Bukayo Saka")));
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