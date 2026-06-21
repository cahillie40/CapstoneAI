package com.example.playerai.controller;

import com.example.playerai.dto.DashboardStats;
import com.example.playerai.dto.ValidationSummaryDTO;
import com.example.playerai.dto.ModelExplanationDTO;
import com.example.playerai.service.DashboardService;
import com.example.playerai.service.ValidationService;
import com.example.playerai.service.ModelExplanationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;
    private final ValidationService validationService;
    private final ModelExplanationService modelExplanationService;

    public DashboardController(DashboardService dashboardService,
                               ValidationService validationService,
                               ModelExplanationService modelExplanationService) {
        this.dashboardService        = dashboardService;
        this.validationService       = validationService;
        this.modelExplanationService = modelExplanationService;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }

    @GetMapping("/validation")
    public ResponseEntity<ValidationSummaryDTO> getValidation() {
        return ResponseEntity.ok(validationService.getSummary());
    }

    @GetMapping("/model-explanation")
    public ResponseEntity<ModelExplanationDTO> getModelExplanation() {
        return ResponseEntity.ok(modelExplanationService.getExplanation());
    }
}