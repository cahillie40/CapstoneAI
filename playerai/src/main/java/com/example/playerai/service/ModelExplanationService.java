package com.example.playerai.service;

import com.example.playerai.dto.FeatureWeightDTO;
import com.example.playerai.dto.ModelExplanationDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModelExplanationService {

    public ModelExplanationDTO getExplanation() {
        List<FeatureWeightDTO> featureWeights = List.of(
                new FeatureWeightDTO("goals", 2.5, "Direct attacking output"),
                new FeatureWeightDTO("assists", 2.0, "Creative end-product"),
                new FeatureWeightDTO("shotsOnTarget", 0.8, "Shot quality proxy"),
                new FeatureWeightDTO("passAccuracy", 0.3, "Retention and passing efficiency"),
                new FeatureWeightDTO("expectedGoals", 3.0, "Quality of scoring chances"),
                new FeatureWeightDTO("expectedAssists", 2.5, "Quality of chance creation"),
                new FeatureWeightDTO("keyPasses", 0.4, "Chance creation volume"),
                new FeatureWeightDTO("progressivePasses", 0.2, "Ball progression through passing"),
                new FeatureWeightDTO("dribblesCompleted", 0.3, "Ball progression and take-on success"),
                new FeatureWeightDTO("tacklesWon", 0.25, "Defensive duel success"),
                new FeatureWeightDTO("interceptions", 0.25, "Defensive anticipation"),
                new FeatureWeightDTO("ballRecoveries", 0.15, "Regaining possession"),
                new FeatureWeightDTO("matchesMissed", -0.6, "Availability risk"),
                new FeatureWeightDTO("recentMatchLoad", -2.0, "Fatigue adjustment when recent match load is high"),
                new FeatureWeightDTO("injuryStatus", -10.0, "Strong injury penalty"),
                new FeatureWeightDTO("redCards", -2.0, "Discipline penalty"),
                new FeatureWeightDTO("yellowCards", -0.5, "Minor discipline penalty")
        );

        List<String> riskLogic = List.of(
                "LOW risk: score >= 75",
                "MEDIUM risk: score between 50 and 74.9",
                "HIGH risk: score < 50"
        );

        return new ModelExplanationDTO(
                "Weighted Rule-Based Scoring Model",
                "The model calculates a predicted form score by combining attacking production, creative output, ball progression, defensive activity, discipline, injury state, and workload indicators. Advanced football analytics fields improve the explanatory power of the score.",
                featureWeights,
                riskLogic
        );
    }
}