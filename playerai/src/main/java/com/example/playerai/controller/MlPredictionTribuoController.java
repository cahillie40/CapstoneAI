package com.example.playerai.controller;

import com.example.playerai.dto.MlModelInfoTribuoDTO;
import com.example.playerai.dto.MlPredictionTribuoRequest;
import com.example.playerai.dto.MlPredictionTribuoResponse;
import com.example.playerai.service.MlPredictionTribuoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ml/tribuo")
@CrossOrigin(origins = "*")
public class MlPredictionTribuoController {

    private final MlPredictionTribuoService mlPredictionTribuoService;

    public MlPredictionTribuoController(MlPredictionTribuoService mlPredictionTribuoService) {
        this.mlPredictionTribuoService = mlPredictionTribuoService;
    }

    @GetMapping("/model-info")
    public ResponseEntity<MlModelInfoTribuoDTO> getModelInfo() {
        return ResponseEntity.ok(mlPredictionTribuoService.getModelInfo());
    }

    @PostMapping("/predict")
    public ResponseEntity<MlPredictionTribuoResponse> predict(@RequestBody MlPredictionTribuoRequest request) {
        return ResponseEntity.ok(mlPredictionTribuoService.predict(request));
    }
}