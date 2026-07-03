package com.example.playerai.controller;

import com.example.playerai.dto.MlModelInfoTribuoDTO;
import com.example.playerai.dto.MlTribuoTrainingInfoResponse;
import com.example.playerai.service.MlTribuoTrainingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ml/tribuo")
@CrossOrigin(origins = "*")
public class MlTribuoTrainingController {

    private final MlTribuoTrainingService mlTribuoTrainingService;

    public MlTribuoTrainingController(MlTribuoTrainingService mlTribuoTrainingService) {
        this.mlTribuoTrainingService = mlTribuoTrainingService;
    }

    @GetMapping("/training-model-info")
    public ResponseEntity<MlModelInfoTribuoDTO> getModelInfo() {
        return ResponseEntity.ok(mlTribuoTrainingService.getModelInfo());
    }

    @GetMapping("/training-info")
    public ResponseEntity<MlTribuoTrainingInfoResponse> getTrainingInfo() {
        return ResponseEntity.ok(mlTribuoTrainingService.getTrainingInfo());
    }

    @PostMapping("/train")
    public ResponseEntity<MlTribuoTrainingInfoResponse> trainModel() {
        return ResponseEntity.ok(mlTribuoTrainingService.trainModel());
    }
}