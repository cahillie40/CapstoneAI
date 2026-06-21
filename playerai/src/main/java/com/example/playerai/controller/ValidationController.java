package com.example.playerai.controller;

import com.example.playerai.dto.ValidationSummaryDTO;
import com.example.playerai.service.ValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/predictions")
@CrossOrigin(origins = "*")
public class ValidationController {

    private final ValidationService validationService;

    public ValidationController(ValidationService validationService) {
        this.validationService = validationService;
    }

    @GetMapping("/validation-summary")
    public ResponseEntity<ValidationSummaryDTO> getValidationSummary() {
        return ResponseEntity.ok(validationService.getSummary());
    }
}