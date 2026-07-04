package com.example.playerai.service;

import com.example.playerai.dto.MlTribuoEvaluationPlayerRowDTO;
import com.example.playerai.dto.MlTribuoEvaluationResponse;
import com.example.playerai.entity.Player;
import com.example.playerai.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.tribuo.MutableDataset;
import org.tribuo.Prediction;
import org.tribuo.data.text.impl.SimpleStringDataSource;
import org.tribuo.evaluation.TrainTestSplitter;
import org.tribuo.math.optimisers.AdaGrad;
import org.tribuo.regression.Regressor;
import org.tribuo.regression.sgd.linear.LinearSGDTrainer;
import org.tribuo.regression.sgd.objectives.SquaredLoss;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class MlTribuoEvaluationService {

    private static final double DEFAULT_SPLIT_RATIO = 0.8;

    private final MlTribuoModelManager modelManager;
    private final PlayerRepository playerRepository;
    private final TribuoPlayerDatasetFactory tribuoPlayerDatasetFactory;

    public MlTribuoEvaluationService(MlTribuoModelManager modelManager,
                                     PlayerRepository playerRepository,
                                     TribuoPlayerDatasetFactory tribuoPlayerDatasetFactory) {
        this.modelManager = modelManager;
        this.playerRepository = playerRepository;
        this.tribuoPlayerDatasetFactory = tribuoPlayerDatasetFactory;
    }

    public MlTribuoEvaluationResponse getEvaluation() {
        int totalPlayers = tribuoPlayerDatasetFactory.getTotalPlayerCount();
        int trainablePlayers = tribuoPlayerDatasetFactory.getTrainablePlayerCount();
        int excludedPlayers = tribuoPlayerDatasetFactory.getExcludedPlayerCount();

        return new MlTribuoEvaluationResponse(
                modelManager.getLastMae(),
                modelManager.getLastRmse(),
                modelManager.getLastR2(),
                modelManager.getLastTrainingRows(),
                modelManager.getLastTestRows(),
                modelManager.getLastSplitRatio(),
                modelManager.getLastEvaluatedAt() != null ? modelManager.getLastEvaluatedAt().toString() : null,
                totalPlayers,
                trainablePlayers,
                excludedPlayers,
                modelManager.getLastMae() == null
                        ? "No evaluation has been run yet."
                        : "Evaluation completed successfully for the current Tribuo regression model using players from the database."
        );
    }


    public MlTribuoEvaluationResponse evaluateModel() {
        MutableDataset<Regressor> dataset = tribuoPlayerDatasetFactory.buildDatasetFromPlayers();

        if (dataset.size() < 3) {
            throw new IllegalStateException("Not enough complete player records to evaluate the Tribuo model.");
        }

        int totalSize = dataset.size();
        int trainSize = Math.max(1, (int) Math.round(totalSize * DEFAULT_SPLIT_RATIO));
        int testSize = totalSize - trainSize;

        if (testSize < 1) {
            trainSize = totalSize - 1;
            testSize = 1;
        }

        var factory = new org.tribuo.regression.RegressionFactory();

        MutableDataset<Regressor> trainDataset = new MutableDataset<>(
                dataset.getProvenance().getSourceProvenance(),
                factory
        );

        MutableDataset<Regressor> testDataset = new MutableDataset<>(
                dataset.getProvenance().getSourceProvenance(),
                factory
        );

        for (int i = 0; i < totalSize; i++) {
            if (i < trainSize) {
                trainDataset.add(dataset.getExample(i));
            } else {
                testDataset.add(dataset.getExample(i));
            }
        }

        if (trainDataset.size() < 1 || testDataset.size() < 1) {
            throw new IllegalStateException("Unable to create a valid train/test split for Tribuo evaluation.");
        }

        LinearSGDTrainer trainer = new LinearSGDTrainer(
                new SquaredLoss(),
                new AdaGrad(0.1),
                50,
                1L
        );

        var model = trainer.train(trainDataset);
        var predictions = model.predict(testDataset);

        double maeSum = 0.0;
        double squaredErrorSum = 0.0;
        double actualSum = 0.0;

        for (var prediction : predictions) {
            actualSum += extractActual(prediction);
        }

        double meanActual = actualSum / testDataset.size();
        double totalVarianceSum = 0.0;

        for (var prediction : predictions) {
            double actual = extractActual(prediction);
            double predicted = extractPredicted(prediction);
            double error = predicted - actual;

            maeSum += Math.abs(error);
            squaredErrorSum += error * error;

            double variance = actual - meanActual;
            totalVarianceSum += variance * variance;
        }

        double mae = maeSum / testDataset.size();
        double rmse = Math.sqrt(squaredErrorSum / testDataset.size());
        double r2 = totalVarianceSum == 0.0
                ? 1.0
                : 1.0 - (squaredErrorSum / totalVarianceSum);

        modelManager.setModel(model);
        modelManager.setTrained(true);
        modelManager.setTrainingRowCount(dataset.size());
        modelManager.setTrainingSource("MySQL players table");
        modelManager.setLastTrainedAt(LocalDateTime.now());

        modelManager.setLastMae(round4(mae));
        modelManager.setLastRmse(round4(rmse));
        modelManager.setLastR2(round4(r2));
        modelManager.setLastTrainingRows(trainDataset.size());
        modelManager.setLastTestRows(testDataset.size());
        modelManager.setLastSplitRatio(DEFAULT_SPLIT_RATIO);
        modelManager.setLastEvaluatedAt(LocalDateTime.now());

        return getEvaluation();
    }

    public List<MlTribuoEvaluationPlayerRowDTO> getEvaluationPlayers() {
        return playerRepository.findAll().stream()
                .sorted(Comparator.comparing(Player::getName, Comparator.nullsLast(String::compareToIgnoreCase)))
                .map(player -> {
                    double evaluatedScore = round1(safeDouble(player.getFormRating()));
                    double previousScore = round1(evaluatedScore - deriveTrendShift(player));

                    String trend;
                    String trendReason;

                    if (evaluatedScore > previousScore) {
                        trend = "IMPROVING";
                        trendReason = buildImprovingReason(player);
                    } else if (evaluatedScore < previousScore) {
                        trend = "DECLINING";
                        trendReason = buildDecliningReason(player);
                    } else {
                        trend = "STABLE";
                        trendReason = "Performance profile remains steady based on the current player data.";
                    }

                    return new MlTribuoEvaluationPlayerRowDTO(
                            safeText(player.getName(), "Unknown Player"),
                            safeText(player.getPosition(), "N/A"),
                            previousScore,
                            evaluatedScore,
                            trend,
                            trendReason
                    );
                })
                .toList();
    }

    private double extractActual(Prediction<Regressor> prediction) {
        return prediction.getExample().getOutput().getValues()[0];
    }

    private double extractPredicted(Prediction<Regressor> prediction) {
        return prediction.getOutput().getValues()[0];
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

        return shift;
    }

    private String buildImprovingReason(Player player) {
        if (safeInteger(player.getGoals()) >= 10 && safeDouble(player.getExpectedGoals()) >= 8.0) {
            return "Strong goal output and expected goals are lifting the evaluated score.";
        }

        if (safeInteger(player.getAssists()) >= 7 && safeDouble(player.getExpectedAssists()) >= 6.0) {
            return "Creative output and expected assists indicate improved attacking contribution.";
        }

        if (safeInteger(player.getMinutesPlayed()) >= 2000) {
            return "High availability and sustained minutes support an improving trend.";
        }

        return "Current player metrics are stronger than the earlier baseline.";
    }

    private String buildDecliningReason(Player player) {
        if (Boolean.TRUE.equals(player.getInjuryStatus())) {
            return "Injury status is reducing availability and dragging the evaluated score down.";
        }

        if (safeInteger(player.getMatchesMissed()) >= 5) {
            return "Missed matches have disrupted continuity and lowered the evaluation.";
        }

        if (safeInteger(player.getMinutesPlayed()) < 1500) {
            return "Lower minutes played have weakened the current performance profile.";
        }

        return "Current player metrics are below the earlier baseline.";
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

    private double round4(double value) {
        return Math.round(value * 10000.0) / 10000.0;
    }
}