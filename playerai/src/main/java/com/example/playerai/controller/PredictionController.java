package com.example.playerai.controller;

import com.example.playerai.dto.PredictionRequest;
import com.example.playerai.dto.PredictionResponse;
import com.example.playerai.dto.PredictionSaveRequest;
import com.example.playerai.dto.PredictionHistoryDTO;
import com.example.playerai.service.PredictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/predictions")
@CrossOrigin(origins = "*")
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping("/form-rating")
    public ResponseEntity<PredictionResponse> predictFormRating(
            @RequestBody PredictionRequest request) {
        PredictionResponse response = predictionService.predict(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public ResponseEntity<PredictionHistoryDTO> savePrediction(
            @RequestBody PredictionSaveRequest request) {
        PredictionResponse response = predictionService.predict(request.getPredictionRequest());
        PredictionHistoryDTO saved = predictionService.savePrediction(
                request.getPlayerId(),
                request.getPredictionRequest(),
                response
        );
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/history")
    public ResponseEntity<List<PredictionHistoryDTO>> getAllHistory() {
        List<PredictionHistoryDTO> history = predictionService.getAllHistory();
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/{playerId}")
    public ResponseEntity<List<PredictionHistoryDTO>> getPlayerHistory(@PathVariable Long playerId) {
        List<PredictionHistoryDTO> history = predictionService.getHistoryForPlayer(playerId);
        return ResponseEntity.ok(history);
    }
}