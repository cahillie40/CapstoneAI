package com.example.playerai.service;

import com.example.playerai.dto.ValidationSummaryDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValidationService {

    public ValidationSummaryDTO getSummary() {
        return new ValidationSummaryDTO(
                "Player Performance Prediction Model",
                "Validated",
                "Rule-based analytical model",
                "The model has been extended with advanced football analytics inputs including xG, xA, key passes, progressive passes, dribbles completed, tackles won, interceptions, ball recoveries, missed matches, and workload indicators. These features improve realism by evaluating attacking quality, creativity, defensive output, and player availability.",
                List.of(
                        "Combines traditional and advanced football metrics",
                        "Uses expected-value metrics such as xG and xA",
                        "Accounts for defensive contribution",
                        "Includes fatigue and availability factors",
                        "Supports more realistic player comparison and projection"
                ),
                List.of(
                        "Current implementation is rule-based rather than trained on a full historical dataset",
                        "Feature weights are manually assigned",
                        "Does not yet include opponent strength or match context",
                        "Does not yet use event-level sequence modelling"
                )
        );
    }
}