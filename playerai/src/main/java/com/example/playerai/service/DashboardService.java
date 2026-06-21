package com.example.playerai.service;

import com.example.playerai.dto.DashboardStats;
import com.example.playerai.dto.ModelExplanationDTO;
import com.example.playerai.dto.ValidationSummaryDTO;
import com.example.playerai.entity.Player;
import com.example.playerai.repository.PlayerRepository;
import com.example.playerai.repository.PredictionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final PlayerRepository playerRepository;
    private final PredictionRepository predictionRepository;
    private final ValidationService validationService;
    private final ModelExplanationService modelExplanationService;

    public DashboardService(PlayerRepository playerRepository,
                            PredictionRepository predictionRepository,
                            ValidationService validationService,
                            ModelExplanationService modelExplanationService) {
        this.playerRepository = playerRepository;
        this.predictionRepository = predictionRepository;
        this.validationService = validationService;
        this.modelExplanationService = modelExplanationService;
    }

    public DashboardStats getStats() {
        List<Player> players = playerRepository.findAll();

        long totalPlayers = players.size();
        long injuredPlayers = players.stream()
                .filter(p -> Boolean.TRUE.equals(p.getInjuryStatus()))
                .count();
        long totalPredictions = predictionRepository.count();

        double averageFormRating = round1(players.stream()
                .map(Player::getFormRating)
                .filter(v -> v != null)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0));

        double averageExpectedGoals = round1(players.stream()
                .map(Player::getExpectedGoals)
                .filter(v -> v != null)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0));

        double averageExpectedAssists = round1(players.stream()
                .map(Player::getExpectedAssists)
                .filter(v -> v != null)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0));

        double averageBallRecoveries = round1(players.stream()
                .map(Player::getBallRecoveries)
                .filter(v -> v != null)
                .mapToDouble(Integer::doubleValue)
                .average()
                .orElse(0.0));

        return new DashboardStats(
                totalPlayers,
                injuredPlayers,
                totalPredictions,
                averageFormRating,
                averageExpectedGoals,
                averageExpectedAssists,
                averageBallRecoveries
        );
    }

    public ValidationSummaryDTO getValidationSummary() {
        return validationService.getSummary();
    }

    public ModelExplanationDTO getModelExplanation() {
        return modelExplanationService.getExplanation();
    }

    private double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}