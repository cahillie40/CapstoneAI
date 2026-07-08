package com.example.playerai.integration;

import com.example.playerai.entity.Player;
import com.example.playerai.repository.PlayerRepository;
import com.example.playerai.repository.PredictionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PlayerControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

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
    void getPlayers_returns200() throws Exception {
        mockMvc.perform(get("/players"))
                .andExpect(status().isOk());
    }

    @Test
    void createPlayer_persistsAndReturnsPlayer() throws Exception {
        Player player = buildPlayer("Bukayo Saka", "Arsenal", "RW");

        mockMvc.perform(post("/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(player)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Bukayo Saka")))
                .andExpect(jsonPath("$.team", is("Arsenal")))
                .andExpect(jsonPath("$.position", is("RW")));
    }

    @Test
    void getPlayerById_returnsPlayerWhenFound() throws Exception {
        Player saved = playerRepository.save(buildPlayer("Jude Bellingham", "Real Madrid", "CM"));

        mockMvc.perform(get("/players/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Jude Bellingham")))
                .andExpect(jsonPath("$.team", is("Real Madrid")));
    }

    @Test
    void getPlayerById_returns404WhenMissing() throws Exception {
        mockMvc.perform(get("/players/{id}", 999999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllPlayers_returnsInsertedPlayers() throws Exception {
        playerRepository.save(buildPlayer("Erling Haaland", "Man City", "ST"));
        playerRepository.save(buildPlayer("Phil Foden", "Man City", "CAM"));

        mockMvc.perform(get("/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void updatePlayer_updatesExistingPlayer() throws Exception {
        Player saved = playerRepository.save(buildPlayer("Cole Palmer", "Chelsea", "RW"));

        Player updated = buildPlayer("Cole Palmer", "Chelsea", "CAM");
        updated.setGoals(18);
        updated.setAssists(11);
        updated.setFormRating(89.0);

        mockMvc.perform(put("/players/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.position", is("CAM")))
                .andExpect(jsonPath("$.goals", is(18)))
                .andExpect(jsonPath("$.assists", is(11)));
    }

    @Test
    void updatePlayer_returns404WhenMissing() throws Exception {
        Player updated = buildPlayer("Missing Player", "No Team", "CM");

        mockMvc.perform(put("/players/{id}", 999999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePlayer_removesExistingPlayer() throws Exception {
        Player saved = playerRepository.save(buildPlayer("Declan Rice", "Arsenal", "CDM"));

        mockMvc.perform(delete("/players/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/players/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePlayer_returns404WhenMissing() throws Exception {
        mockMvc.perform(delete("/players/{id}", 999999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchPlayers_filtersByNameAndReturnsPagedResult() throws Exception {
        playerRepository.save(buildPlayer("Martin Odegaard", "Arsenal", "CAM"));
        playerRepository.save(buildPlayer("Martin Zubimendi", "Real Sociedad", "CM"));
        playerRepository.save(buildPlayer("Vinicius Junior", "Real Madrid", "LW"));

        mockMvc.perform(get("/players/search")
                        .param("name", "Martin")
                        .param("position", "")
                        .param("team", "")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)));
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