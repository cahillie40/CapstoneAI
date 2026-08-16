package com.example.playerai.service;

import com.example.playerai.dto.MlTribuoEvaluationPlayerRowDTO;
import com.example.playerai.dto.MlTribuoEvaluationResponse;
import com.example.playerai.entity.Player;
import com.example.playerai.repository.PlayerRepository;
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
public class MlTribuoEvaluationService {

    private static final double DEFAULT_SPLIT_RATIO = 0.8;
    private static final double TREND_THRESHOLD = 0.5;

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
        List<Player> trainablePlayers = playerRepository.findAll().stream()
                .filter(tribuoPlayerDatasetFactory::isTrainable)
                .toList();

        if (dataset.size() < 3) {
            throw new IllegalStateException("Not enough complete player records to evaluate the Tribuo model.");
        }

        if (trainablePlayers.size() != dataset.size()) {
            throw new IllegalStateException("Trainable player list does not match dataset size.");
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

        List<Prediction<Regressor>> testPredictions = model.predict(testDataset);
        List<Prediction<Regressor>> allPredictions = model.predict(dataset);

        double maeSum = 0.0;
        double squaredErrorSum = 0.0;
        double actualSum = 0.0;

        for (var prediction : testPredictions) {
            actualSum += extractActual(prediction);
        }

        double meanActual = actualSum / testDataset.size();
        double totalVarianceSum = 0.0;

        for (var prediction : testPredictions) {
            double actual = extractActual(prediction);
            double predicted = extractPredicted(prediction);
            double error = predicted - actual;

            maeSum += Math.abs(error);
            squaredErrorSum += error * error;

            double variance = actual - meanActual;
            totalVarianceSum += variance * variance;
        }

        List<MlTribuoEvaluationPlayerRowDTO> evaluationRows = new ArrayList<>();

        for (int i = 0; i < allPredictions.size(); i++) {
            Prediction<Regressor> prediction = allPredictions.get(i);
            Player player = trainablePlayers.get(i);

            double previousScore = round1(extractActual(prediction));
            double evaluatedScore = round1(extractPredicted(prediction));
            double diff = evaluatedScore - previousScore;

            String trend;
            String trendReason;

            if (diff > TREND_THRESHOLD) {
                trend = "IMPROVING";
                trendReason = "Predicted score is " + round1(diff) + " points above the previous score after evaluation.";
            } else if (diff < -TREND_THRESHOLD) {
                trend = "DECLINING";
                trendReason = "Predicted score is " + round1(Math.abs(diff)) + " points below the previous score after evaluation.";
            } else {
                trend = "STABLE";
                trendReason = "Predicted score remains broadly in line with the previous score after evaluation.";
            }

            evaluationRows.add(new MlTribuoEvaluationPlayerRowDTO(
                    safeText(player.getName(), "Unknown Player"),
                    safeText(player.getPosition(), "N/A"),
                    previousScore,
                    evaluatedScore,
                    trend,
                    trendReason
            ));
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
        modelManager.setLastEvaluationPlayers(
                evaluationRows.stream()
                        .sorted(Comparator.comparing(MlTribuoEvaluationPlayerRowDTO::getPlayerName,
                                Comparator.nullsLast(String::compareToIgnoreCase)))
                        .toList()
        );

        return getEvaluation();
    }

    public List<MlTribuoEvaluationPlayerRowDTO> getEvaluationPlayers() {
        return modelManager.getLastEvaluationPlayers();
    }

    private double extractActual(Prediction<Regressor> prediction) {
        return prediction.getExample().getOutput().getValues()[0];
    }

    private double extractPredicted(Prediction<Regressor> prediction) {
        return prediction.getOutput().getValues()[0];
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