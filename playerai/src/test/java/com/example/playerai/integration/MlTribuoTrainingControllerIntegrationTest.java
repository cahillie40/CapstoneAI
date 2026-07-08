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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MlTribuoTrainingControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    PredictionRepository predictionRepository;

    @BeforeEach
    void setUp() {
        predictionRepository.deleteAll();
        playerRepository.deleteAll();
    }

    @Test
    void getTrainingModelInfo_returns200AndExpectedFields() throws Exception {
        mockMvc.perform(get("/ml/tribuo/training-model-info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelName").exists())
                .andExpect(jsonPath("$.modelType").exists())
                .andExpect(jsonPath("$.trainingStatus").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.supportedFeatures").isArray());
    }

    @Test
    void getTrainingInfo_returnsCountsFromDatabase() throws Exception {
        playerRepository.save(buildTrainablePlayer("Bukayo Saka", "Arsenal", "RW"));
        playerRepository.save(buildTrainablePlayer("Martin Odegaard", "Arsenal", "CAM"));

        Player excluded = buildTrainablePlayer("Unknown Prospect", "Test FC", "CM");
        excluded.setExpectedGoals(null);
        playerRepository.save(excluded);

        mockMvc.perform(get("/ml/tribuo/training-info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelName").exists())
                .andExpect(jsonPath("$.algorithm").exists())
                .andExpect(jsonPath("$.trainingStatus").exists())
                .andExpect(jsonPath("$.totalPlayers", is(3)))
                .andExpect(jsonPath("$.trainablePlayers", is(2)))
                .andExpect(jsonPath("$.excludedPlayers", is(1)))
                .andExpect(jsonPath("$.featuresUsed").isArray());
    }

    @Test
    void trainModel_returnsTrainingSummaryWhenEnoughPlayersExist() throws Exception {
        playerRepository.save(buildTrainablePlayer("Player One", "Club A", "ST"));
        playerRepository.save(buildTrainablePlayer("Player Two", "Club A", "CM"));
        playerRepository.save(buildTrainablePlayer("Player Three", "Club B", "CB"));

        mockMvc.perform(post("/ml/tribuo/train"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelName").exists())
                .andExpect(jsonPath("$.algorithm").exists())
                .andExpect(jsonPath("$.trainingStatus", is("Model trained and ready")))
                .andExpect(jsonPath("$.trainingRowCount", is(3)))
                .andExpect(jsonPath("$.trainingSource", is("MySQL players table")))
                .andExpect(jsonPath("$.lastTrainedAt").exists())
                .andExpect(jsonPath("$.totalPlayers", is(3)))
                .andExpect(jsonPath("$.trainablePlayers", is(3)))
                .andExpect(jsonPath("$.excludedPlayers", is(0)));
    }

    @Test
    void trainModel_throwsWhenNotEnoughTrainablePlayersExist() {
        playerRepository.save(buildTrainablePlayer("Only One", "Club A", "ST"));
        playerRepository.save(buildTrainablePlayer("Only Two", "Club A", "CM"));

        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(
                Exception.class,
                () -> mockMvc.perform(post("/ml/tribuo/train")).andReturn()
        );

        org.assertj.core.api.Assertions.assertThat(exception.getMessage())
                .contains("Not enough complete player records to train the Tribuo model");
    }

    @Test
    void getTrainingDataPreview_returnsPreviewRows() throws Exception {
        playerRepository.save(buildTrainablePlayer("Jude Bellingham", "Real Madrid", "CM"));
        playerRepository.save(buildTrainablePlayer("Vinicius Junior", "Real Madrid", "LW"));

        mockMvc.perform(get("/ml/tribuo/training-data-preview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].playerName").exists())
                .andExpect(jsonPath("$[0].position").exists())
                .andExpect(jsonPath("$[0].age").exists())
                .andExpect(jsonPath("$[0].goals").exists())
                .andExpect(jsonPath("$[0].assists").exists())
                .andExpect(jsonPath("$[0].minutesPlayed").exists())
                .andExpect(jsonPath("$[0].expectedGoals").exists())
                .andExpect(jsonPath("$[0].expectedAssists").exists())
                .andExpect(jsonPath("$[0].previousScore").exists())
                .andExpect(jsonPath("$[0].currentTargetScore").exists())
                .andExpect(jsonPath("$[0].trend").exists())
                .andExpect(jsonPath("$[0].trendReason").exists());
    }

    private Player buildTrainablePlayer(String name, String team, String position) {
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