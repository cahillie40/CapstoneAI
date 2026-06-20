package com.example.playerai.service;

import com.example.playerai.dto.DashboardStats;
import com.example.playerai.entity.Prediction;
import com.example.playerai.repository.PlayerRepository;
import com.example.playerai.repository.PredictionRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DashboardService {

    private final PlayerRepository playerRepository;
    private final PredictionRepository predictionRepository;

    public DashboardService(PlayerRepository playerRepository,
                            PredictionRepository predictionRepository) {
        this.playerRepository = playerRepository;
        this.predictionRepository = predictionRepository;
    }

    public DashboardStats getStats() {
        Long totalPlayers = playerRepository.count();
        Long totalPredictions = predictionRepository.count();

        List<Prediction> allPredictions = predictionRepository.findAllByOrderByCreatedAtDesc();

        Double averageFormRating = allPredictions.stream()
                .mapToDouble(Prediction::getPredictedFormRating)
                .average()
                .orElse(0.0);

        averageFormRating = Math.round(averageFormRating * 10.0) / 10.0;

        Prediction highest = allPredictions.stream()
                .max((a, b) -> Double.compare(a.getPredictedFormRating(), b.getPredictedFormRating()))
                .orElse(null);

        String highestRatedPlayer = highest != null ? highest.getPlayer().getName() : "N/A";
        Double highestFormRating = highest != null ? highest.getPredictedFormRating() : 0.0;

        Long highRiskCount = allPredictions.stream()
                .filter(p -> "HIGH".equals(p.getRiskLevel()))
                .count();

        Long mediumRiskCount = allPredictions.stream()
                .filter(p -> "MEDIUM".equals(p.getRiskLevel()))
                .count();

        Long lowRiskCount = allPredictions.stream()
                .filter(p -> "LOW".equals(p.getRiskLevel()))
                .count();

        return new DashboardStats(
                totalPlayers,
                totalPredictions,
                averageFormRating,
                highestRatedPlayer,
                highestFormRating,
                highRiskCount,
                mediumRiskCount,
                lowRiskCount
        );
    }
}