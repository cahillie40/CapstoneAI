package com.example.playerai.integration;

import com.example.playerai.repository.PlayerRepository;
import com.example.playerai.repository.PredictionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CsvIngestControllerIntegrationTest {

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
    void importPlayersCsv_importsValidRows() throws Exception {
        String csv = """
            name,age,position,team,matchesPlayed,goals,assists,minutesPlayed,yellowCards,redCards,shotsOnTarget,passAccuracy,formRating,injuryStatus,expectedGoals,expectedAssists,keyPasses,progressivePasses,dribblesCompleted,tacklesWon,interceptions,ballRecoveries,matchesMissed,recentMatchLoad
            Bukayo Saka,23,RW,Arsenal,32,16,10,2650,4,0,28,85.7,84.0,false,11.4,8.2,41,57,39,14,9,37,1,4
            Martin Odegaard,25,CAM,Arsenal,30,8,11,2500,3,0,18,88.2,82.0,false,6.1,9.4,52,61,20,11,8,35,0,4
            """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "players.csv",
                "text/csv",
                csv.getBytes()
        );

        mockMvc.perform(multipart("/import/players/csv").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imported", is(2)))
                .andExpect(jsonPath("$.errors", is(0)))
                .andExpect(jsonPath("$.message", is("Import successful")));
    }

    @Test
    void importPlayersCsv_returnsBadRequestForEmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.csv",
                "text/csv",
                new byte[0]
        );

        mockMvc.perform(multipart("/import/players/csv").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.imported", is(0)))
                .andExpect(jsonPath("$.errors", is(0)))
                .andExpect(jsonPath("$.message", is("File is empty")));
    }

    @Test
    void importPlayersCsv_reportsRowErrorsAndStillImportsValidRows() throws Exception {
        String csv = """
            name,age,position,team,matchesPlayed,goals,assists,minutesPlayed,yellowCards,redCards,shotsOnTarget,passAccuracy,formRating,injuryStatus,expectedGoals,expectedAssists,keyPasses,progressivePasses,dribblesCompleted,tacklesWon,interceptions,ballRecoveries,matchesMissed,recentMatchLoad
            Valid Player,24,CM,Club A,30,7,8,2300,2,0,16,87.0,80.5,false,5.2,6.4,28,39,18,17,12,40,1,4
            Broken Player,not-a-number,CB,Club B,28,3,2,2100,4,0,8,79.5,74.0,false,1.8,1.2,10,22,7,21,14,44,0,3
            """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "players-with-error.csv",
                "text/csv",
                csv.getBytes()
        );

        mockMvc.perform(multipart("/import/players/csv").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imported", is(1)))
                .andExpect(jsonPath("$.errors", is(1)))
                .andExpect(jsonPath("$.message", containsString("Import completed with 1 error(s)")))
                .andExpect(jsonPath("$.message", containsString("Invalid integer for 'age'")));
    }

    @Test
    void importPlayersCsv_supportsSemicolonDelimitedFiles() throws Exception {
        String csv = """
            name;age;position;team;matchesPlayed;goals;assists;minutesPlayed;yellowCards;redCards;shotsOnTarget;passAccuracy;formRating;injuryStatus;expectedGoals;expectedAssists;keyPasses;progressivePasses;dribblesCompleted;tacklesWon;interceptions;ballRecoveries;matchesMissed;recentMatchLoad
            Jude Bellingham;21;CM;Real Madrid;31;9;7;2480;5;0;19;89.1;86.0;false;7.3;5.7;31;48;26;19;13;42;1;5
            """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "players-semicolon.csv",
                "text/csv",
                csv.getBytes()
        );

        mockMvc.perform(multipart("/import/players/csv").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imported", is(1)))
                .andExpect(jsonPath("$.errors", is(0)));
    }
}