package com.example.playerai.service;

import com.example.playerai.dto.FactorContributionDTO;
import com.example.playerai.dto.PredictionHistoryDTO;
import com.example.playerai.dto.PredictionRequest;
import com.example.playerai.dto.PredictionResponse;
import com.example.playerai.entity.Player;
import com.example.playerai.entity.Prediction;
import com.example.playerai.repository.PlayerRepository;
import com.example.playerai.repository.PredictionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

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

        score += addPositive(allFactors, "goals", request.getGoals(), multiply(request.getGoals(), 2.5),
                "Goal scoring directly improves attacking output.");
        score += addPositive(allFactors, "assists", request.getAssists(), multiply(request.getAssists(), 2.0),
                "Assists reflect creative contribution.");
        score += addPositive(allFactors, "shotsOnTarget", request.getShotsOnTarget(), multiply(request.getShotsOnTarget(), 0.8),
                "Shots on target indicate threat and finishing involvement.");
        score += addPositive(allFactors, "passAccuracy", request.getPassAccuracy(), multiply(request.getPassAccuracy(), 0.3),
                "Pass accuracy supports retention and technical efficiency.");
        score += addPositive(allFactors, "minutesPlayed", request.getMinutesPlayed(), multiply(request.getMinutesPlayed(), 0.005),
                "Minutes played reflect availability and contribution volume.");

        score += addNegative(allFactors, "yellowCards", request.getYellowCards(), multiply(request.getYellowCards(), -0.5),
                "Yellow cards slightly reduce discipline score.");
        score += addNegative(allFactors, "redCards", request.getRedCards(), multiply(request.getRedCards(), -2.0),
                "Red cards create a stronger discipline penalty.");

        if (Boolean.TRUE.equals(request.getInjuryStatus())) {
            score += addNegative(allFactors, "injuryStatus", 1, -10.0,
                    "Injury status significantly reduces projected readiness.");
        }

        score += addPositive(allFactors, "expectedGoals", request.getExpectedGoals(), multiply(request.getExpectedGoals(), 3.0),
                "Expected goals reflect chance quality and attacking positioning.");
        score += addPositive(allFactors, "expectedAssists", request.getExpectedAssists(), multiply(request.getExpectedAssists(), 2.5),
                "Expected assists reflect chance creation quality.");
        score += addPositive(allFactors, "keyPasses", request.getKeyPasses(), multiply(request.getKeyPasses(), 0.4),
                "Key passes support chance creation volume.");
        score += addPositive(allFactors, "progressivePasses", request.getProgressivePasses(), multiply(request.getProgressivePasses(), 0.2),
                "Progressive passing improves attacking progression.");
        score += addPositive(allFactors, "dribblesCompleted", request.getDribblesCompleted(), multiply(request.getDribblesCompleted(), 0.3),
                "Completed dribbles support ball progression and attacking pressure.");
        score += addPositive(allFactors, "tacklesWon", request.getTacklesWon(), multiply(request.getTacklesWon(), 0.25),
                "Tackles won improve defensive contribution.");
        score += addPositive(allFactors, "interceptions", request.getInterceptions(), multiply(request.getInterceptions(), 0.25),
                "Interceptions reflect defensive anticipation.");
        score += addPositive(allFactors, "ballRecoveries", request.getBallRecoveries(), multiply(request.getBallRecoveries(), 0.15),
                "Ball recoveries help regain possession and control.");

        score += addNegative(allFactors, "matchesMissed", request.getMatchesMissed(), multiply(request.getMatchesMissed(), -0.6),
                "Missed matches reduce availability and continuity.");

        if (request.getRecentMatchLoad() != null) {
            double loadPenalty = calculateLoadPenalty(request.getRecentMatchLoad());
            if (loadPenalty < 0) {
                score += addNegative(allFactors, "recentMatchLoad", request.getRecentMatchLoad(), loadPenalty,
                        "Recent workload can reduce freshness and increase fatigue risk.");
            } else if (loadPenalty > 0) {
                score += addPositive(allFactors, "recentMatchLoad", request.getRecentMatchLoad(), loadPenalty,
                        "Balanced recent workload supports match readiness.");
            }
        }

        if (request.getPosition() != null) {
            double positionAdjustment = getPositionAdjustment(request.getPosition());
            if (positionAdjustment > 0) {
                score += addPositive(allFactors, "position", 1, positionAdjustment,
                        "Position-based adjustment reflects role expectations.");
            }
        }

        if (request.getAge() != null) {
            double ageAdjustment = getAgeAdjustment(request.getAge());
            if (ageAdjustment > 0) {
                score += addPositive(allFactors, "age", request.getAge(), ageAdjustment,
                        "Age profile suggests peak-performance range.");
            } else if (ageAdjustment < 0) {
                score += addNegative(allFactors, "age", request.getAge(), ageAdjustment,
                        "Age profile slightly reduces expected physical peak.");
            }
        }

        score = Math.max(0, Math.min(100, score));
        double rounded = round1(score);

        String riskLevel = determineRiskLevel(rounded);

        List<FactorContributionDTO> positiveFactors = allFactors.stream()
                .filter(f -> f.getContribution() != null && f.getContribution() > 0)
                .sorted(Comparator.comparing(FactorContributionDTO::getContribution).reversed())
                .limit(3)
                .collect(Collectors.toList());

        List<FactorContributionDTO> negativeFactors = allFactors.stream()
                .filter(f -> f.getContribution() != null && f.getContribution() < 0)
                .sorted(Comparator.comparing((FactorContributionDTO f) -> Math.abs(f.getContribution())).reversed())
                .limit(3)
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
                allFactors
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
                prediction.getCreatedAt()
        );
    }

    private double addPositive(List<FactorContributionDTO> factors,
                               String feature,
                               Number value,
                               double contribution,
                               String explanation) {
        if (value == null || contribution == 0.0) {
            return 0.0;
        }

        factors.add(new FactorContributionDTO(
                feature,
                value.doubleValue(),
                round1(contribution),
                "positive",
                explanation
        ));

        return contribution;
    }

    private double addNegative(List<FactorContributionDTO> factors,
                               String feature,
                               Number value,
                               double contribution,
                               String explanation) {
        if (value == null || contribution == 0.0) {
            return 0.0;
        }

        factors.add(new FactorContributionDTO(
                feature,
                value.doubleValue(),
                round1(contribution),
                "negative",
                explanation
        ));

        return contribution;
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