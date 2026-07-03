package com.example.playerai.controller;

import com.example.playerai.dto.MlTribuoEvaluationResponse;
import com.example.playerai.service.MlTribuoEvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ml/tribuo")
@CrossOrigin(origins = "*")
public class MlTribuoEvaluationController {

    private final MlTribuoEvaluationService mlTribuoEvaluationService;

    public MlTribuoEvaluationController(MlTribuoEvaluationService mlTribuoEvaluationService) {
        this.mlTribuoEvaluationService = mlTribuoEvaluationService;
    }

    @GetMapping("/evaluation")
    public ResponseEntity<MlTribuoEvaluationResponse> getEvaluation() {
        return ResponseEntity.ok(mlTribuoEvaluationService.getEvaluation());
    }

    @PostMapping("/evaluate")
    public ResponseEntity<MlTribuoEvaluationResponse> evaluateModel() {
        return ResponseEntity.ok(mlTribuoEvaluationService.evaluateModel());
    }
}