package com.example.playerai.controller;

import com.example.playerai.entity.Player;
import com.example.playerai.repository.PlayerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/import")
@CrossOrigin(origins = "*")
public class CsvIngestController {

    private final PlayerRepository playerRepository;

    public CsvIngestController(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @PostMapping("/players/csv")
    public ResponseEntity<CsvImportResult> importPlayers(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new CsvImportResult(0, 0, "File is empty"));
        }

        List<Player> players = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int rowNumber = 1;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                rowNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    Player player = parseLine(line);
                    players.add(player);
                } catch (Exception e) {
                    errors.add("Row " + rowNumber + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new CsvImportResult(0, 0, "Failed to read file: " + e.getMessage()));
        }

        if (!players.isEmpty()) {
            playerRepository.saveAll(players);
        }

        String message = errors.isEmpty()
                ? "Import successful"
                : "Import completed with " + errors.size() + " error(s): " + String.join("; ", errors);

        return ResponseEntity.ok(new CsvImportResult(players.size(), errors.size(), message));
    }

    private Player parseLine(String line) {
        String[] cols = splitCsvLineAuto(line);

        if (cols.length < 24) {
            throw new IllegalArgumentException("Expected 24 columns, found " + cols.length);
        }

        Player player = new Player();
        player.setName(clean(cols[0]));
        player.setAge(parseInt(cols[1], "age"));
        player.setPosition(clean(cols[2]));
        player.setTeam(clean(cols[3]));
        player.setMatchesPlayed(parseInt(cols[4], "matchesPlayed"));
        player.setGoals(parseInt(cols[5], "goals"));
        player.setAssists(parseInt(cols[6], "assists"));
        player.setMinutesPlayed(parseInt(cols[7], "minutesPlayed"));
        player.setYellowCards(parseInt(cols[8], "yellowCards"));
        player.setRedCards(parseInt(cols[9], "redCards"));
        player.setShotsOnTarget(parseInt(cols[10], "shotsOnTarget"));
        player.setPassAccuracy(parseDouble(cols[11], "passAccuracy"));
        player.setFormRating(parseDouble(cols[12], "formRating"));
        player.setInjuryStatus(parseBoolean(cols[13], "injuryStatus"));

        player.setExpectedGoals(parseDouble(cols[14], "expectedGoals"));
        player.setExpectedAssists(parseDouble(cols[15], "expectedAssists"));
        player.setKeyPasses(parseInt(cols[16], "keyPasses"));
        player.setProgressivePasses(parseInt(cols[17], "progressivePasses"));
        player.setDribblesCompleted(parseInt(cols[18], "dribblesCompleted"));
        player.setTacklesWon(parseInt(cols[19], "tacklesWon"));
        player.setInterceptions(parseInt(cols[20], "interceptions"));
        player.setBallRecoveries(parseInt(cols[21], "ballRecoveries"));
        player.setMatchesMissed(parseInt(cols[22], "matchesMissed"));
        player.setRecentMatchLoad(parseInt(cols[23], "recentMatchLoad"));

        return player;
    }

    private String[] splitCsvLineAuto(String line) {
        String[] commaSplit = splitCsvLine(line, ',');
        if (commaSplit.length >= 24) {
            return commaSplit;
        }

        String[] semicolonSplit = splitCsvLine(line, ';');
        if (semicolonSplit.length >= 24) {
            return semicolonSplit;
        }

        return commaSplit;
    }

    private String[] splitCsvLine(String line, char delimiter) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == delimiter && !inQuotes) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }

    private String clean(String value) {
        return value.trim().replace("\"", "");
    }

    private int parseInt(String value, String field) {
        try {
            return Integer.parseInt(clean(value));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid integer for '" + field + "': " + value.trim());
        }
    }

    private double parseDouble(String value, String field) {
        try {
            return Double.parseDouble(clean(value));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid decimal for '" + field + "': " + value.trim());
        }
    }

    private boolean parseBoolean(String value, String field) {
        String cleaned = clean(value).toLowerCase();
        if (cleaned.equals("true") || cleaned.equals("1") || cleaned.equals("yes")) return true;
        if (cleaned.equals("false") || cleaned.equals("0") || cleaned.equals("no")) return false;
        throw new IllegalArgumentException("Invalid boolean for '" + field + "': " + value.trim());
    }

    public static class CsvImportResult {
        private int imported;
        private int errors;
        private String message;

        public CsvImportResult(int imported, int errors, String message) {
            this.imported = imported;
            this.errors = errors;
            this.message = message;
        }

        public int getImported() {
            return imported;
        }

        public int getErrors() {
            return errors;
        }

        public String getMessage() {
            return message;
        }
    }
}