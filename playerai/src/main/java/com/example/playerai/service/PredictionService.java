package com.example.playerai.service;

import com.example.playerai.dto.FactorContributionDTO;
import com.example.playerai.dto.PredictionHistoryDTO;
import com.example.playerai.dto.PredictionRequest;
import com.example.playerai.dto.PredictionResponse;
import com.example.playerai.dto.ScoreStepDTO;
import com.example.playerai.entity.Player;
import com.example.playerai.entity.Prediction;
import com.example.playerai.repository.PlayerRepository;
import com.example.playerai.repository.PredictionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PredictionService {

    private final PredictionRepository predictionRepository;
    private final PlayerRepository playerRepository;
    private final ObjectMapper objectMapper;

    public PredictionService(PredictionRepository predictionRepository,
                             PlayerRepository playerRepository,
                             ObjectMapper objectMapper) {
        this.predictionRepository = predictionRepository;
        this.playerRepository = playerRepository;
        this.objectMapper = objectMapper;
    }

    public PredictionResponse predict(PredictionRequest request) {
        double baselineScore = 50.0;
        double score = baselineScore;

        List<FactorContributionDTO> allFactors = new ArrayList<>();
        List<ScoreStepDTO> scoreSteps = new ArrayList<>();

        scoreSteps.add(new ScoreStepDTO(
                "Baseline score",
                null,
                0.0,
                round1(baselineScore),
                "The model starts from a base score before player-specific adjustments are applied."
        ));

        score = applyFactor(
                allFactors, scoreSteps, score,
                "goals", request.getGoals(), multiply(request.getGoals(), 2.5),
                "positive", "Goal scoring directly improves attacking output."
        );

        score = applyFactor(
                allFactors, scoreSteps, score,
                "assists", request.getAssists(), multiply(request.getAssists(), 2.0),
                "positive", "Assists reflect creative contribution."
        );

        score = applyFactor(
                allFactors, scoreSteps, score,
                "shotsOnTarget", request.getShotsOnTarget(), multiply(request.getShotsOnTarget(), 0.8),
                "positive", "Shots on target indicate threat and finishing involvement."
        );

        score = applyFactor(
                allFactors, scoreSteps, score,
                "passAccuracy", request.getPassAccuracy(), multiply(request.getPassAccuracy(), 0.3),
                "positive", "Pass accuracy supports retention and technical efficiency."
        );

        score = applyFactor(
                allFactors, scoreSteps, score,
                "minutesPlayed", request.getMinutesPlayed(), multiply(request.getMinutesPlayed(), 0.005),
                "positive", "Minutes played reflect availability and contribution volume."
        );

        score = applyFactor(
                allFactors, scoreSteps, score,
                "yellowCards", request.getYellowCards(), multiply(request.getYellowCards(), -0.5),
                "negative", "Yellow cards slightly reduce discipline score."
        );

        score = applyFactor(
                allFactors, scoreSteps, score,
                "redCards", request.getRedCards(), multiply(request.getRedCards(), -2.0),
                "negative", "Red cards create a stronger discipline penalty."
        );

        if (Boolean.TRUE.equals(request.getInjuryStatus())) {
            score = applyFactor(
                    allFactors, scoreSteps, score,
                    "injuryStatus", 1, -10.0,
                    "negative", "Injury status significantly reduces projected readiness."
            );
        }

        score = applyFactor(
                allFactors, scoreSteps, score,
                "expectedGoals", request.getExpectedGoals(), multiply(request.getExpectedGoals(), 3.0),
                "positive", "Expected goals reflect chance quality and attacking positioning."
        );

        score = applyFactor(
                allFactors, scoreSteps, score,
                "expectedAssists", request.getExpectedAssists(), multiply(request.getExpectedAssists(), 2.5),
                "positive", "Expected assists reflect chance creation quality."
        );

        score = applyFactor(
                allFactors, scoreSteps, score,
                "keyPasses", request.getKeyPasses(), multiply(request.getKeyPasses(), 0.4),
                "positive", "Key passes support chance creation volume."
        );

        score = applyFactor(
                allFactors, scoreSteps, score,
                "progressivePasses", request.getProgressivePasses(), multiply(request.getProgressivePasses(), 0.2),
                "positive", "Progressive passing improves attacking progression."
        );

        score = applyFactor(
                allFactors, scoreSteps, score,
                "dribblesCompleted", request.getDribblesCompleted(), multiply(request.getDribblesCompleted(), 0.3),
                "positive", "Completed dribbles support ball progression and attacking pressure."
        );

        score = applyFactor(
                allFactors, scoreSteps, score,
                "tacklesWon", request.getTacklesWon(), multiply(request.getTacklesWon(), 0.25),
                "positive", "Tackles won improve defensive contribution."
        );

        score = applyFactor(
                allFactors, scoreSteps, score,
                "interceptions", request.getInterceptions(), multiply(request.getInterceptions(), 0.25),
                "positive", "Interceptions reflect defensive anticipation."
        );

        score = applyFactor(
                allFactors, scoreSteps, score,
                "ballRecoveries", request.getBallRecoveries(), multiply(request.getBallRecoveries(), 0.15),
                "positive", "Ball recoveries help regain possession and control."
        );

        score = applyFactor(
                allFactors, scoreSteps, score,
                "matchesMissed", request.getMatchesMissed(), multiply(request.getMatchesMissed(), -0.6),
                "negative", "Missed matches reduce availability and continuity."
        );

        if (request.getRecentMatchLoad() != null) {
            double loadPenalty = calculateLoadPenalty(request.getRecentMatchLoad());
            if (loadPenalty != 0.0) {
                score = applyFactor(
                        allFactors, scoreSteps, score,
                        "recentMatchLoad", request.getRecentMatchLoad(), loadPenalty,
                        loadPenalty > 0 ? "positive" : "negative",
                        loadPenalty > 0
                                ? "Balanced recent workload supports match readiness."
                                : "Recent workload can reduce freshness and increase fatigue risk."
                );
            }
        }

        if (request.getPosition() != null) {
            double positionAdjustment = getPositionAdjustment(request.getPosition());
            if (positionAdjustment != 0.0) {
                score = applyFactor(
                        allFactors, scoreSteps, score,
                        "position", 1, positionAdjustment,
                        positionAdjustment > 0 ? "positive" : "negative",
                        "Position-based adjustment reflects role expectations."
                );
            }
        }

        if (request.getAge() != null) {
            double ageAdjustment = getAgeAdjustment(request.getAge());
            if (ageAdjustment != 0.0) {
                score = applyFactor(
                        allFactors, scoreSteps, score,
                        "age", request.getAge(), ageAdjustment,
                        ageAdjustment > 0 ? "positive" : "negative",
                        ageAdjustment > 0
                                ? "Age profile suggests peak-performance range."
                                : "Age profile slightly reduces expected physical peak."
                );
            }
        }

        double unclampedScore = round1(score);
        double clampedScore = Math.max(0, Math.min(100, score));
        double rounded = round1(clampedScore);

        if (unclampedScore != rounded) {
            scoreSteps.add(new ScoreStepDTO(
                    "Score cap applied",
                    unclampedScore,
                    round1(rounded - unclampedScore),
                    rounded,
                    "The raw score exceeded the model range, so it was capped to stay between 0 and 100."
            ));
        }

        scoreSteps.add(new ScoreStepDTO(
                "Final score",
                null,
                0.0,
                rounded,
                "The final predicted form rating after all positive and negative adjustments and score limits."
        ));

        String riskLevel = determineRiskLevel(rounded);

        List<FactorContributionDTO> positiveFactors = allFactors.stream()
                .filter(f -> f.getContribution() != null && f.getContribution() > 0)
                .sorted(Comparator.comparing(FactorContributionDTO::getContribution).reversed())
                .collect(Collectors.toList());

        List<FactorContributionDTO> negativeFactors = allFactors.stream()
                .filter(f -> f.getContribution() != null && f.getContribution() < 0)
                .sorted(Comparator.comparing((FactorContributionDTO f) -> Math.abs(f.getContribution())).reversed())
                .collect(Collectors.toList());

        List<FactorContributionDTO> sortedAllFactors = allFactors.stream()
                .sorted(Comparator.comparing((FactorContributionDTO f) -> Math.abs(f.getContribution())).reversed())
                .collect(Collectors.toList());

        String summary = buildSummary(rounded, riskLevel, positiveFactors, negativeFactors);

        return new PredictionResponse(
                null,
                baselineScore,
                rounded,
                riskLevel,
                summary,
                positiveFactors,
                negativeFactors,
                sortedAllFactors,
                scoreSteps
        );
    }

    public PredictionHistoryDTO savePrediction(Long playerId, PredictionRequest request,
                                               PredictionResponse response) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        String inputDataJson;
        try {
            inputDataJson = objectMapper.writeValueAsString(request);
        } catch (Exception e) {
            inputDataJson = "";
        }

        Prediction prediction = new Prediction(
                player,
                response.getPredictedFormRating(),
                response.getRiskLevel(),
                response.getSummary(),
                inputDataJson,
                LocalDateTime.now()
        );

        Prediction saved = predictionRepository.save(prediction);
        return toDTO(saved);
    }

    public List<PredictionHistoryDTO> getHistoryForPlayer(Long playerId) {
        return predictionRepository.findByPlayerIdOrderByCreatedAtDesc(playerId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<PredictionHistoryDTO> getAllHistory() {
        return predictionRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Page<PredictionHistoryDTO> getAllHistoryPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return predictionRepository.findAll(pageable).map(this::toDTO);
    }

    private PredictionHistoryDTO toDTO(Prediction prediction) {
        return new PredictionHistoryDTO(
                prediction.getId(),
                prediction.getPlayer().getName(),
                prediction.getPlayer().getId(),
                prediction.getPredictedFormRating(),
                prediction.getRiskLevel(),
                prediction.getSummary(),
                prediction.getInputData(),
                prediction.getCreatedAt()
        );
    }

    private double applyFactor(List<FactorContributionDTO> factors,
                               List<ScoreStepDTO> scoreSteps,
                               double currentScore,
                               String feature,
                               Number value,
                               double contribution,
                               String direction,
                               String explanation) {
        if (value == null || contribution == 0.0) {
            return currentScore;
        }

        double updatedScore = currentScore + contribution;

        factors.add(new FactorContributionDTO(
                feature,
                value.doubleValue(),
                round1(contribution),
                direction,
                explanation
        ));

        scoreSteps.add(new ScoreStepDTO(
                feature,
                value.doubleValue(),
                round1(contribution),
                round1(updatedScore),
                explanation
        ));

        return updatedScore;
    }

    private double multiply(Number value, double weight) {
        if (value == null) {
            return 0.0;
        }
        return value.doubleValue() * weight;
    }

    private double calculateLoadPenalty(Integer recentMatchLoad) {
        if (recentMatchLoad == null) {
            return 0.0;
        }

        if (recentMatchLoad <= 3) {
            return 0.5;
        } else if (recentMatchLoad <= 5) {
            return 0.0;
        } else if (recentMatchLoad <= 7) {
            return -2.0;
        } else {
            return -4.0;
        }
    }

    private double getPositionAdjustment(String position) {
        if (position == null) {
            return 0.0;
        }

        return switch (position.toLowerCase()) {
            case "goalkeeper" -> 2.0;
            case "defender" -> 1.5;
            case "midfielder" -> 1.0;
            case "winger" -> 1.5;
            case "forward" -> 2.0;
            case "striker" -> 2.0;
            default -> 0.0;
        };
    }

    private double getAgeAdjustment(Integer age) {
        if (age == null) {
            return 0.0;
        }

        if (age >= 24 && age <= 29) {
            return 3.0;
        } else if (age >= 30) {
            return -1.5;
        }
        return 0.0;
    }

    private String determineRiskLevel(double score) {
        if (score >= 75) {
            return "LOW";
        } else if (score >= 50) {
            return "MEDIUM";
        } else {
            return "HIGH";
        }
    }

    private String buildSummary(double score,
                                String riskLevel,
                                List<FactorContributionDTO> positiveFactors,
                                List<FactorContributionDTO> negativeFactors) {
        StringBuilder sb = new StringBuilder();

        sb.append("Predicted form rating: ").append(score).append(". ");
        sb.append("Risk level: ").append(riskLevel).append(". ");

        if (!positiveFactors.isEmpty()) {
            sb.append("Main positives: ");
            for (int i = 0; i < positiveFactors.size(); i++) {
                FactorContributionDTO factor = positiveFactors.get(i);
                sb.append(factor.getFeature())
                        .append(" (")
                        .append(factor.getContribution())
                        .append(")");
                if (i < positiveFactors.size() - 1) {
                    sb.append(", ");
                } else {
                    sb.append(". ");
                }
            }
        }

        if (!negativeFactors.isEmpty()) {
            sb.append("Main risks: ");
            for (int i = 0; i < negativeFactors.size(); i++) {
                FactorContributionDTO factor = negativeFactors.get(i);
                sb.append(factor.getFeature())
                        .append(" (")
                        .append(factor.getContribution())
                        .append(")");
                if (i < negativeFactors.size() - 1) {
                    sb.append(", ");
                } else {
                    sb.append(". ");
                }
            }
        }

        return sb.toString().trim();
    }

    private double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}