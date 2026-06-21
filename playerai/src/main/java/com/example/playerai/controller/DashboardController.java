package com.example.playerai.controller;

import com.example.playerai.dto.DashboardStats;
import com.example.playerai.dto.ModelExplanationDTO;
import com.example.playerai.dto.ValidationSummaryDTO;
import com.example.playerai.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }

    @GetMapping("/validation")
    public ResponseEntity<ValidationSummaryDTO> getValidation() {
        return ResponseEntity.ok(dashboardService.getValidationSummary());
    }

    @GetMapping("/model-explanation")
    public ResponseEntity<ModelExplanationDTO> getModelExplanation() {
        return ResponseEntity.ok(dashboardService.getModelExplanation());
    }
}