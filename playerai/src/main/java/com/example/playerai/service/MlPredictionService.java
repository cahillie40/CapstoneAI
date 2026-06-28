package com.example.playerai.service;

import com.example.playerai.dto.MlFeatureImpactDTO;
import com.example.playerai.dto.MlModelInfoDTO;
import com.example.playerai.dto.MlPredictionRequest;
import com.example.playerai.dto.MlPredictionResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class MlPredictionService {

    public MlModelInfoDTO getModelInfo() {
        return new MlModelInfoDTO(
                "Capstone ML Predictor",
                "Java ML Predictor (XGBoost-style screen)",
                "Demo / Ready for integration",
                "This screen is designed for a machine learning prediction workflow in Java Spring Boot. It can later be connected to Tribuo or XGBoost4J for real model inference.",
                List.of(
                        "age", "position", "goals", "assists", "shotsOnTarget", "passAccuracy",
                        "expectedGoals", "expectedAssists", "keyPasses", "progressivePasses",
                        "dribblesCompleted", "tacklesWon", "interceptions", "ballRecoveries",
                        "matchesMissed", "recentMatchLoad", "injuryStatus"
                )
        );
    }

    public MlPredictionResponse predict(MlPredictionRequest request) {
        double score = 52.0;
        List<MlFeatureImpactDTO> features = new ArrayList<>();

        score = addFeature(features, score, "expectedGoals", request.getExpectedGoals(), 0.28, "positive",
                "High xG suggests strong scoring chance quality.", safe(request.getExpectedGoals()) * 4.0);

        score = addFeature(features, score, "assists", request.getAssists(), 0.18, "positive",
                "Assist volume supports creative output.", safe(request.getAssists()) * 1.5);

        score = addFeature(features, score, "shotsOnTarget", request.getShotsOnTarget(), 0.16, "positive",
                "Shots on target indicate attacking threat.", safe(request.getShotsOnTarget()) * 0.7);

        score = addFeature(features, score, "recentMatchLoad", request.getRecentMatchLoad(), 0.12,
                safe(request.getRecentMatchLoad()) > 5 ? "negative" : "neutral",
                safe(request.getRecentMatchLoad()) > 5
                        ? "Heavy recent load may reduce freshness."
                        : "Recent match load is manageable.",
                safe(request.getRecentMatchLoad()) > 5 ? -3.0 : 0.5);

        score = addFeature(features, score, "injuryStatus",
                Boolean.TRUE.equals(request.getInjuryStatus()) ? 1 : 0,
                0.15,
                Boolean.TRUE.equals(request.getInjuryStatus()) ? "negative" : "neutral",
                Boolean.TRUE.equals(request.getInjuryStatus())
                        ? "Injury lowers short-term readiness."
                        : "No current injury flag.",
                Boolean.TRUE.equals(request.getInjuryStatus()) ? -8.0 : 0.0);

        score = addFeature(features, score, "progressivePasses", request.getProgressivePasses(), 0.11, "positive",
                "Progressive passing supports ball advancement.", safe(request.getProgressivePasses()) * 0.15);

        score = addFeature(features, score, "ballRecoveries", request.getBallRecoveries(), 0.09, "positive",
                "Ball recoveries contribute defensive value.", safe(request.getBallRecoveries()) * 0.08);

        score = Math.max(0, Math.min(100, score));
        double rounded = round1(score);

        String riskLevel = rounded >= 75 ? "LOW" : (rounded >= 50 ? "MEDIUM" : "HIGH");
        double confidence = rounded >= 75 ? 0.89 : (rounded >= 50 ? 0.76 : 0.64);

        List<MlFeatureImpactDTO> topFeatures = features.stream()
                .sorted(Comparator.comparing(MlFeatureImpactDTO::getImportance).reversed())
                .limit(5)
                .toList();

        String summary = "The ML predictor estimates this player's likely performance using attacking, creative, defensive, workload, and availability signals.";

        return new MlPredictionResponse(
                request.getPlayerName(),
                "Java ML Predictor",
                rounded,
                riskLevel,
                confidence,
                summary,
                topFeatures
        );
    }

    private double addFeature(List<MlFeatureImpactDTO> features,
                              double score,
                              String feature,
                              Object value,
                              double importance,
                              String effect,
                              String explanation,
                              double contribution) {
        features.add(new MlFeatureImpactDTO(
                feature,
                String.valueOf(value),
                importance,
                effect,
                explanation
        ));
        return score + contribution;
    }

    private double safe(Number value) {
        return value == null ? 0.0 : value.doubleValue();
    }

    private double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}