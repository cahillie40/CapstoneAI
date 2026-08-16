package com.example.playerai.service;

import com.example.playerai.dto.MlModelInfoTribuoDTO;
import com.example.playerai.dto.MlTribuoTrainingInfoResponse;
import com.example.playerai.dto.MlTribuoTrainingPreviewRowDTO;
import com.example.playerai.entity.Player;
import com.example.playerai.repository.PlayerRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.tribuo.MutableDataset;
import org.tribuo.Prediction;
import org.tribuo.math.optimisers.AdaGrad;
import org.tribuo.regression.Regressor;
import org.tribuo.regression.sgd.linear.LinearSGDTrainer;
import org.tribuo.regression.sgd.objectives.SquaredLoss;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class MlTribuoTrainingService {

    private static final double TREND_THRESHOLD = 0.5;

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
        return modelManager.getLastTrainingPreviewRows();
    }

    public MlTribuoTrainingInfoResponse trainModel() {
        MutableDataset<Regressor> dataset = tribuoPlayerDatasetFactory.buildDatasetFromPlayers();
        List<Player> trainablePlayers = playerRepository.findAll().stream()
                .filter(tribuoPlayerDatasetFactory::isTrainable)
                .toList();

        if (dataset.size() < 3) {
            throw new IllegalStateException("Not enough complete player records to train the Tribuo model.");
        }

        if (trainablePlayers.size() != dataset.size()) {
            throw new IllegalStateException("Trainable player list does not match dataset size.");
        }

        LinearSGDTrainer trainer = new LinearSGDTrainer(
                new SquaredLoss(),
                new AdaGrad(0.1),
                50,
                1L
        );

        var model = trainer.train(dataset);
        List<Prediction<Regressor>> predictions = model.predict(dataset);

        List<MlTribuoTrainingPreviewRowDTO> previewRows = new ArrayList<>();

        for (int i = 0; i < predictions.size(); i++) {
            Prediction<Regressor> prediction = predictions.get(i);
            Player player = trainablePlayers.get(i);

            double previousScore = round1(extractActual(prediction));
            double currentScore = round1(extractPredicted(prediction));
            double diff = currentScore - previousScore;

            String trend;
            String trendReason;

            if (diff > TREND_THRESHOLD) {
                trend = "IMPROVING";
                trendReason = "Retrained Tribuo model predicts a score " + round1(diff) + " points above the previous score.";
            } else if (diff < -TREND_THRESHOLD) {
                trend = "DECLINING";
                trendReason = "Retrained Tribuo model predicts a score " + round1(Math.abs(diff)) + " points below the previous score.";
            } else {
                trend = "STABLE";
                trendReason = "Retrained Tribuo model predicts a score broadly in line with the previous score.";
            }

            previewRows.add(new MlTribuoTrainingPreviewRowDTO(
                    safeText(player.getName(), "Unknown Player"),
                    safeText(player.getPosition(), "N/A"),
                    safeInteger(player.getAge()),
                    safeInteger(player.getGoals()),
                    safeInteger(player.getAssists()),
                    safeInteger(player.getMinutesPlayed()),
                    safeDouble(player.getExpectedGoals()),
                    safeDouble(player.getExpectedAssists()),
                    previousScore,
                    currentScore,
                    trend,
                    trendReason
            ));
        }

        modelManager.setModel(model);
        modelManager.setTrained(true);
        modelManager.setTrainingRowCount(dataset.size());
        modelManager.setTrainingSource("MySQL players table");
        modelManager.setLastTrainedAt(LocalDateTime.now());
        modelManager.setLastTrainingPreviewRows(
                previewRows.stream()
                        .sorted(Comparator.comparing(MlTribuoTrainingPreviewRowDTO::getPlayerName,
                                Comparator.nullsLast(String::compareToIgnoreCase)))
                        .toList()
        );

        return getTrainingInfo();
    }

    private double extractActual(Prediction<Regressor> prediction) {
        return prediction.getExample().getOutput().getValues()[0];
    }

    private double extractPredicted(Prediction<Regressor> prediction) {
        return prediction.getOutput().getValues()[0];
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