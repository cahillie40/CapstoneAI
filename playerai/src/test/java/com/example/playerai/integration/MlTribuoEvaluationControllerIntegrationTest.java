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
class MlTribuoEvaluationControllerIntegrationTest {

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
    void getEvaluation_returns200AndDefaultSummary() throws Exception {
        mockMvc.perform(get("/ml/tribuo/evaluation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPlayers", is(0)))
                .andExpect(jsonPath("$.trainablePlayers", is(0)))
                .andExpect(jsonPath("$.excludedPlayers", is(0)))
                .andExpect(jsonPath("$.summary").exists());
    }

    @Test
    void evaluateModel_returnsMetricsWhenEnoughPlayersExist() throws Exception {
        playerRepository.save(buildTrainablePlayer("Player One", "Club A", "ST"));
        playerRepository.save(buildTrainablePlayer("Player Two", "Club A", "CM"));
        playerRepository.save(buildTrainablePlayer("Player Three", "Club B", "CB"));
        playerRepository.save(buildTrainablePlayer("Player Four", "Club C", "LW"));

        mockMvc.perform(post("/ml/tribuo/evaluate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mae").isNumber())
                .andExpect(jsonPath("$.rmse").isNumber())
                .andExpect(jsonPath("$.r2").isNumber())
                .andExpect(jsonPath("$.trainingRows").isNumber())
                .andExpect(jsonPath("$.testRows").isNumber())
                .andExpect(jsonPath("$.splitRatio").isNumber())
                .andExpect(jsonPath("$.evaluatedAt").exists())
                .andExpect(jsonPath("$.totalPlayers", is(4)))
                .andExpect(jsonPath("$.trainablePlayers", is(4)))
                .andExpect(jsonPath("$.excludedPlayers", is(0)))
                .andExpect(jsonPath("$.summary").exists());
    }

    @Test
    void evaluateModel_throwsWhenNotEnoughTrainablePlayersExist() {
        playerRepository.save(buildTrainablePlayer("Only One", "Club A", "ST"));
        playerRepository.save(buildTrainablePlayer("Only Two", "Club A", "CM"));

        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(
                Exception.class,
                () -> mockMvc.perform(post("/ml/tribuo/evaluate")).andReturn()
        );

        org.assertj.core.api.Assertions.assertThat(exception.getMessage())
                .contains("Not enough complete player records to evaluate the Tribuo model");
    }

    @Test
    void getEvaluationPlayers_returnsRowsWithExpectedFields() throws Exception {
        playerRepository.save(buildTrainablePlayer("Jude Bellingham", "Real Madrid", "CM"));
        playerRepository.save(buildTrainablePlayer("Vinicius Junior", "Real Madrid", "LW"));

        mockMvc.perform(get("/ml/tribuo/evaluation-players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].playerName").exists())
                .andExpect(jsonPath("$[0].position").exists())
                .andExpect(jsonPath("$[0].previousScore").exists())
                .andExpect(jsonPath("$[0].evaluatedScore").exists())
                .andExpect(jsonPath("$[0].trend").exists())
                .andExpect(jsonPath("$[0].trendReason").exists());
    }

    @Test
    void getEvaluation_reflectsExcludedPlayersCount() throws Exception {
        playerRepository.save(buildTrainablePlayer("Trainable One", "Club A", "ST"));
        playerRepository.save(buildTrainablePlayer("Trainable Two", "Club B", "CM"));

        Player excluded = buildTrainablePlayer("Excluded Player", "Club C", "CB");
        excluded.setFormRating(null);
        playerRepository.save(excluded);

        mockMvc.perform(get("/ml/tribuo/evaluation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPlayers", is(3)))
                .andExpect(jsonPath("$.trainablePlayers", is(2)))
                .andExpect(jsonPath("$.excludedPlayers", is(1)));
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