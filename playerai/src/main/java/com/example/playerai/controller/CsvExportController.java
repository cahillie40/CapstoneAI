package com.example.playerai.controller;

import com.example.playerai.entity.Prediction;
import com.example.playerai.repository.PredictionRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/export")
@CrossOrigin(origins = "*")
public class CsvExportController {

    private final PredictionRepository predictionRepository;

    public CsvExportController(PredictionRepository predictionRepository) {
        this.predictionRepository = predictionRepository;
    }

    @GetMapping("/predictions/csv")
    public ResponseEntity<String> exportPredictionsCsv() {
        List<Prediction> predictions = predictionRepository.findAllByOrderByCreatedAtDesc();

        StringBuilder csv = new StringBuilder();

        // Header row
        csv.append("ID,Player Name,Player ID,Predicted Form Rating,Risk Level,Summary,Created At\n");

        // Data rows
        for (Prediction p : predictions) {
            csv.append(p.getId()).append(",");
            csv.append(escapeCsv(p.getPlayer().getName())).append(",");
            csv.append(p.getPlayer().getId()).append(",");
            csv.append(p.getPredictedFormRating()).append(",");
            csv.append(escapeCsv(p.getRiskLevel())).append(",");
            csv.append(escapeCsv(p.getSummary())).append(",");
            csv.append(p.getCreatedAt()).append("\n");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "prediction-history.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csv.toString());
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}