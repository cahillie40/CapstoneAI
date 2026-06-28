package com.example.playerai.controller;

import com.example.playerai.dto.MlModelInfoDTO;
import com.example.playerai.dto.MlPredictionRequest;
import com.example.playerai.dto.MlPredictionResponse;
import com.example.playerai.service.MlPredictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ml")
@CrossOrigin(origins = "*")
public class MlPredictionController {

    private final MlPredictionService mlPredictionService;

    public MlPredictionController(MlPredictionService mlPredictionService) {
        this.mlPredictionService = mlPredictionService;
    }

    @GetMapping("/model-info")
    public ResponseEntity<MlModelInfoDTO> getModelInfo() {
        return ResponseEntity.ok(mlPredictionService.getModelInfo());
    }

    @PostMapping("/predict")
    public ResponseEntity<MlPredictionResponse> predict(@RequestBody MlPredictionRequest request) {
        return ResponseEntity.ok(mlPredictionService.predict(request));
    }
}