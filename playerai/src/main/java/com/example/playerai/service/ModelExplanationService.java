package com.example.playerai.service;

import com.example.playerai.dto.FeatureWeightDTO;
import com.example.playerai.dto.ModelExplanationDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModelExplanationService {

    public ModelExplanationDTO getExplanation() {
        List<FeatureWeightDTO> featureWeights = List.of(
                new FeatureWeightDTO("goals", 2.5, "Scoring goals strongly increases a player's predicted rating."),
                new FeatureWeightDTO("assists", 2.0, "Providing assists improves creative output and attacking value."),
                new FeatureWeightDTO("shotsOnTarget", 0.8, "More shots on target show attacking threat and better finishing involvement."),
                new FeatureWeightDTO("passAccuracy", 0.3, "Accurate passing supports control, retention, and technical quality."),
                new FeatureWeightDTO("expectedGoals", 3.0, "Higher xG means the player is getting into better scoring positions."),
                new FeatureWeightDTO("expectedAssists", 2.5, "Higher xA means the player is creating better chances for teammates."),
                new FeatureWeightDTO("keyPasses", 0.4, "Key passes reflect chance creation volume."),
                new FeatureWeightDTO("progressivePasses", 0.2, "Progressive passes help move the ball forward into dangerous areas."),
                new FeatureWeightDTO("dribblesCompleted", 0.3, "Completed dribbles show ball progression and attacking ability."),
                new FeatureWeightDTO("tacklesWon", 0.25, "Winning tackles improves defensive contribution."),
                new FeatureWeightDTO("interceptions", 0.25, "Interceptions show anticipation and defensive awareness."),
                new FeatureWeightDTO("ballRecoveries", 0.15, "Recovering the ball helps the team regain possession."),
                new FeatureWeightDTO("matchesMissed", -0.6, "Missing matches reduces consistency, readiness, and availability."),
                new FeatureWeightDTO("recentMatchLoad", -2.0, "Heavy recent workload may reduce freshness and increase fatigue risk."),
                new FeatureWeightDTO("injuryStatus", -10.0, "Being injured has a strong negative impact on predicted readiness."),
                new FeatureWeightDTO("redCards", -2.0, "Red cards hurt discipline and reduce expected contribution."),
                new FeatureWeightDTO("yellowCards", -0.5, "Yellow cards slightly reduce discipline score.")
        );

        List<String> riskLogic = List.of(
                "LOW risk: final score is 75 or above",
                "MEDIUM risk: final score is between 50 and 74.9",
                "HIGH risk: final score is below 50"
        );

        return new ModelExplanationDTO(
                "Rule-based football performance scoring model",
                "This model starts from a baseline score and adjusts it using player statistics. Attacking output, creativity, defensive actions, discipline, injury status, and workload all influence the final predicted form rating.",
                featureWeights,
                riskLogic
        );
    }
}