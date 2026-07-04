package com.example.playerai.service;

import com.example.playerai.dto.MlModelInfoTribuoDTO;
import com.example.playerai.dto.MlTribuoTrainingInfoResponse;
import com.example.playerai.dto.MlTribuoTrainingPreviewRowDTO;
import com.example.playerai.entity.Player;
import com.example.playerai.repository.PlayerRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.tribuo.MutableDataset;
import org.tribuo.math.optimisers.AdaGrad;
import org.tribuo.regression.Regressor;
import org.tribuo.regression.sgd.linear.LinearSGDTrainer;
import org.tribuo.regression.sgd.objectives.SquaredLoss;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class MlTribuoTrainingService {

    private final MlTribuoModelManager modelManager;
    private final TribuoPlayerDatasetFactory tribuoPlayerDatasetFactory;
    private final PlayerRepository playerRepository;

    public MlTribuoTrainingService(MlTribuoModelManager modelManager,
                                   TribuoPlayerDatasetFactory tribuoPlayerDatasetFactory,
                                   PlayerRepository playerRepository) {
        this.modelManager = modelManager;
        this.tribuoPlayerDatasetFactory = tribuoPlayerDatasetFactory;
        this.playerRepository = playerRepository;
    }

    @PostConstruct
    public void init() {
        try {
            if (!playerRepository.findAll().isEmpty()) {
                trainModel();
            }
        } catch (Exception ignored) {
            // Avoid blocking app startup if database data is not ready yet.
        }
    }

    public MlTribuoTrainingInfoResponse getTrainingInfo() {
        int totalPlayers = tribuoPlayerDatasetFactory.getTotalPlayerCount();
        int trainablePlayers = tribuoPlayerDatasetFactory.getTrainablePlayerCount();
        int excludedPlayers = tribuoPlayerDatasetFactory.getExcludedPlayerCount();

        return new MlTribuoTrainingInfoResponse(
                "Tribuo Regression Predictor",
                "Tribuo Linear SGD Regression",
                modelManager.isTrained() ? "Model trained and ready" : "Model not trained",
                modelManager.getTrainingRowCount(),
                modelManager.getTrainingSource(),
                modelManager.getLastTrainedAt() != null ? modelManager.getLastTrainedAt().toString() : null,
                totalPlayers,
                trainablePlayers,
                excludedPlayers,
                List.of(
                        "age",
                        "goals",
                        "assists",
                        "minutesPlayed",
                        "shotsOnTarget",
                        "passAccuracy",
                        "expectedGoals",
                        "expectedAssists",
                        "keyPasses",
                        "progressivePasses",
                        "dribblesCompleted",
                        "tacklesWon",
                        "interceptions",
                        "ballRecoveries",
                        "matchesMissed",
                        "recentMatchLoad",
                        "injuryStatus"
                )
        );
    }


    public MlModelInfoTribuoDTO getModelInfo() {
        return new MlModelInfoTribuoDTO(
                "Tribuo Regression Predictor",
                "Tribuo Linear SGD Regression",
                modelManager.isTrained() ? "Model trained and ready" : "Model not trained",
                "This training screen uses real players from the MySQL database and trains a Tribuo linear regression model to learn player form rating from football performance features.",
                List.of(
                        "age",
                        "goals",
                        "assists",
                        "minutesPlayed",
                        "shotsOnTarget",
                        "passAccuracy",
                        "expectedGoals",
                        "expectedAssists",
                        "keyPasses",
                        "progressivePasses",
                        "dribblesCompleted",
                        "tacklesWon",
                        "interceptions",
                        "ballRecoveries",
                        "matchesMissed",
                        "recentMatchLoad",
                        "injuryStatus"
                )
        );
    }

    public List<MlTribuoTrainingPreviewRowDTO> getTrainingDataPreview() {
        return playerRepository.findAll().stream()
                .filter(player -> player.getFormRating() != null)
                .sorted(Comparator.comparing(Player::getName, Comparator.nullsLast(String::compareToIgnoreCase)))
                .map(player -> {
                    double currentScore = player.getFormRating();
                    double previousScore = round1(currentScore - deriveTrendShift(player));

                    String trend;
                    String trendReason;

                    if (currentScore > previousScore) {
                        trend = "IMPROVING";
                        trendReason = buildImprovingReason(player);
                    } else if (currentScore < previousScore) {
                        trend = "DECLINING";
                        trendReason = buildDecliningReason(player);
                    } else {
                        trend = "STABLE";
                        trendReason = "Recent performance profile is broadly unchanged.";
                    }

                    return new MlTribuoTrainingPreviewRowDTO(
                            safeText(player.getName(), "Unknown Player"),
                            safeText(player.getPosition(), "N/A"),
                            safeInteger(player.getAge()),
                            safeInteger(player.getGoals()),
                            safeInteger(player.getAssists()),
                            safeInteger(player.getMinutesPlayed()),
                            safeDouble(player.getExpectedGoals()),
                            safeDouble(player.getExpectedAssists()),
                            previousScore,
                            round1(currentScore),
                            trend,
                            trendReason
                    );
                })
                .toList();
    }

    public MlTribuoTrainingInfoResponse trainModel() {
        MutableDataset<Regressor> dataset = tribuoPlayerDatasetFactory.buildDatasetFromPlayers();

        if (dataset.size() < 3) {
            throw new IllegalStateException("Not enough complete player records to train the Tribuo model.");
        }

        LinearSGDTrainer trainer = new LinearSGDTrainer(
                new SquaredLoss(),
                new AdaGrad(0.1),
                50,
                1L
        );

        modelManager.setModel(trainer.train(dataset));
        modelManager.setTrained(true);
        modelManager.setTrainingRowCount(dataset.size());
        modelManager.setTrainingSource("MySQL players table");
        modelManager.setLastTrainedAt(LocalDateTime.now());

        return getTrainingInfo();
    }

    private double deriveTrendShift(Player player) {
        double shift = 0.0;

        if (safeInteger(player.getGoals()) >= 10) {
            shift += 2.0;
        }
        if (safeInteger(player.getAssists()) >= 7) {
            shift += 1.5;
        }
        if (safeDouble(player.getExpectedGoals()) >= 8.0) {
            shift += 1.0;
        }
        if (safeDouble(player.getExpectedAssists()) >= 6.0) {
            shift += 1.0;
        }
        if (safeInteger(player.getMinutesPlayed()) < 1500) {
            shift -= 1.5;
        }
        if (Boolean.TRUE.equals(player.getInjuryStatus())) {
            shift -= 2.5;
        }
        if (safeInteger(player.getMatchesMissed()) >= 5) {
            shift -= 1.5;
        }

        if (shift == 0.0) {
            return 0.0;
        }

        return shift;
    }

    private String buildImprovingReason(Player player) {
        if (Boolean.TRUE.equals(player.getInjuryStatus())) {
            return "Despite injury risk, recent output still supports an improving trend.";
        }

        if (safeInteger(player.getGoals()) >= 10 && safeDouble(player.getExpectedGoals()) >= 8.0) {
            return "Strong goal output and expected goals are lifting the current training score.";
        }

        if (safeInteger(player.getAssists()) >= 7 && safeDouble(player.getExpectedAssists()) >= 6.0) {
            return "Creative contribution and expected assists indicate an upward trend.";
        }

        return "Recent performance indicators are stronger than the earlier baseline.";
    }

    private String buildDecliningReason(Player player) {
        if (Boolean.TRUE.equals(player.getInjuryStatus())) {
            return "Injury status is reducing availability and lowering the current training outlook.";
        }

        if (safeInteger(player.getMatchesMissed()) >= 5) {
            return "Missed matches have reduced continuity and lowered the projected score.";
        }

        if (safeInteger(player.getMinutesPlayed()) < 1500) {
            return "Lower minutes played have weakened the current training profile.";
        }

        return "Recent performance indicators are below the earlier baseline.";
    }

    private int safeInteger(Integer value) {
        return value != null ? value : 0;
    }

    private double safeDouble(Double value) {
        return value != null ? value : 0.0;
    }

    private String safeText(String value, String fallback) {
        return value != null && !value.isBlank() ? value : fallback;
    }

    private double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}