package com.example.playerai.service;

import com.example.playerai.dto.ModelExplanationDTO;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ModelExplanationService {

    public ModelExplanationDTO getExplanation() {

        List<ModelExplanationDTO.FeatureWeight> weights = List.of(
                new ModelExplanationDTO.FeatureWeight("Goals",           "+2.5 per goal",     "positive", "Each goal scored adds 2.5 to the form rating"),
                new ModelExplanationDTO.FeatureWeight("Assists",         "+2.0 per assist",   "positive", "Each assist adds 2.0 to the form rating"),
                new ModelExplanationDTO.FeatureWeight("Shots on Target", "+0.8 each",         "positive", "Reflects attacking intent and accuracy"),
                new ModelExplanationDTO.FeatureWeight("Pass Accuracy",   "+0.3 per %",        "positive", "Higher pass accuracy indicates better technical ability"),
                new ModelExplanationDTO.FeatureWeight("Minutes Played",  "+0.005 per minute", "positive", "Consistent playing time reflects form and fitness"),
                new ModelExplanationDTO.FeatureWeight("Yellow Cards",    "-0.5 each",         "negative", "Disciplinary issues reduce the score"),
                new ModelExplanationDTO.FeatureWeight("Red Cards",       "-2.0 each",         "negative", "Red cards significantly reduce the score"),
                new ModelExplanationDTO.FeatureWeight("Injury Status",   "-10.0 if injured",  "negative", "Injury is a major risk factor for performance decline"),
                new ModelExplanationDTO.FeatureWeight("Age 24-29",       "+3.0 bonus",        "positive", "Peak performance age range"),
                new ModelExplanationDTO.FeatureWeight("Age 30+",         "-1.5 penalty",      "negative", "Accounts for natural physical decline"),
                new ModelExplanationDTO.FeatureWeight("Position",        "+1.0 to +2.0",      "positive", "Position-specific bonus applied to all outfield roles")
        );

        List<ModelExplanationDTO.RiskLevel> riskLevels = List.of(
                new ModelExplanationDTO.RiskLevel("LOW",    "75 - 100", "Player is in strong form and unlikely to decline"),
                new ModelExplanationDTO.RiskLevel("MEDIUM", "50 - 74",  "Player shows moderate form with some risk indicators"),
                new ModelExplanationDTO.RiskLevel("HIGH",   "0 - 49",   "Player shows signs of decline or has significant risk factors")
        );

        return new ModelExplanationDTO(
                "Weighted Scoring Baseline Model",
                "A rule-based weighted scoring engine that evaluates player performance " +
                        "across key statistical and physical features. Each feature contributes a " +
                        "weighted value to a base score of 50. The final score is capped between " +
                        "0 and 100 and mapped to a risk level. This serves as the baseline model " +
                        "prior to integration of Random Forest or LSTM approaches.",
                weights,
                riskLevels,
                "50.0"
        );
    }
}