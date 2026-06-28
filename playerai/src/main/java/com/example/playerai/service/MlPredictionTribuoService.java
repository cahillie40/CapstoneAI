package com.example.playerai.service;

import com.example.playerai.dto.MlFeatureImpactTribuoDTO;
import com.example.playerai.dto.MlModelInfoTribuoDTO;
import com.example.playerai.dto.MlPredictionTribuoHistoryResponse;
import com.example.playerai.dto.MlPredictionTribuoRequest;
import com.example.playerai.dto.MlPredictionTribuoResponse;
import com.example.playerai.entity.MlPredictionTribuoHistory;
import com.example.playerai.repository.MlPredictionTribuoHistoryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.tribuo.Example;
import org.tribuo.Feature;
import org.tribuo.Model;
import org.tribuo.MutableDataset;
import org.tribuo.Prediction;
import org.tribuo.impl.ArrayExample;
import org.tribuo.math.optimisers.AdaGrad;
import org.tribuo.regression.Regressor;
import org.tribuo.regression.sgd.linear.LinearSGDTrainer;
import org.tribuo.regression.sgd.objectives.SquaredLoss;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class MlPredictionTribuoService {

    private final MlPredictionTribuoHistoryRepository historyRepository;

    private Model<Regressor> model;
    private boolean trained = false;

    public MlPredictionTribuoService(MlPredictionTribuoHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @PostConstruct
    public void init() {
        try {
            trainDemoModel();
            trained = true;
        } catch (Exception e) {
            trained = false;
            e.printStackTrace();
        }
    }

    public MlModelInfoTribuoDTO getModelInfo() {
        return new MlModelInfoTribuoDTO(
                "Tribuo Regression Predictor",
                "Tribuo Linear SGD Regression",
                trained ? "Model trained and ready" : "Model not trained",
                "This separate screen uses a Tribuo linear regression pipeline in Java Spring Boot without native XGBoost dependencies.",
                List.of(
                        "age", "goals", "assists", "minutesPlayed", "shotsOnTarget", "passAccuracy",
                        "expectedGoals", "expectedAssists", "keyPasses", "progressivePasses",
                        "dribblesCompleted", "tacklesWon", "interceptions", "ballRecoveries",
                        "matchesMissed", "recentMatchLoad", "injuryStatus"
                )
        );
    }

    public MlPredictionTribuoResponse predict(MlPredictionTribuoRequest request) {
        if (!trained || model == null) {
            throw new IllegalStateException("Tribuo model is not available.");
        }

        Example<Regressor> example = new ArrayExample<>(new Regressor("score", 0.0));
        addFeature(example, "age", request.getAge());
        addFeature(example, "goals", request.getGoals());
        addFeature(example, "assists", request.getAssists());
        addFeature(example, "minutesPlayed", request.getMinutesPlayed());
        addFeature(example, "shotsOnTarget", request.getShotsOnTarget());
        addFeature(example, "passAccuracy", request.getPassAccuracy());
        addFeature(example, "expectedGoals", request.getExpectedGoals());
        addFeature(example, "expectedAssists", request.getExpectedAssists());
        addFeature(example, "keyPasses", request.getKeyPasses());
        addFeature(example, "progressivePasses", request.getProgressivePasses());
        addFeature(example, "dribblesCompleted", request.getDribblesCompleted());
        addFeature(example, "tacklesWon", request.getTacklesWon());
        addFeature(example, "interceptions", request.getInterceptions());
        addFeature(example, "ballRecoveries", request.getBallRecoveries());
        addFeature(example, "matchesMissed", request.getMatchesMissed());
        addFeature(example, "recentMatchLoad", request.getRecentMatchLoad());
        addFeature(example, "injuryStatus", Boolean.TRUE.equals(request.getInjuryStatus()) ? 1 : 0);

        Prediction<Regressor> prediction = model.predict(example);
        double predictedScore = prediction.getOutput().getValues()[0];
        predictedScore = Math.max(0, Math.min(100, round1(predictedScore)));

        String riskLevel = predictedScore >= 75 ? "LOW" : (predictedScore >= 50 ? "MEDIUM" : "HIGH");
        double confidence = predictedScore >= 75 ? 0.88 : (predictedScore >= 50 ? 0.75 : 0.63);

        List<MlFeatureImpactTribuoDTO> topFeatures = buildFeatureImportance(request);
        String summary = buildSummary(request, predictedScore, riskLevel);

        MlPredictionTribuoResponse response = new MlPredictionTribuoResponse(
                request.getPlayerName(),
                "Tribuo Regression Predictor",
                "Tribuo Linear SGD Regression",
                predictedScore,
                riskLevel,
                confidence,
                summary,
                topFeatures
        );

        saveHistory(request, response);
        return response;
    }

    public List<MlPredictionTribuoHistoryResponse> getPredictionHistory() {
        return historyRepository.findAllByOrderByPredictedAtDesc()
                .stream()
                .map(history -> new MlPredictionTribuoHistoryResponse(
                        history.getId(),
                        history.getPlayerId(),
                        history.getPlayerName(),
                        history.getModelName(),
                        history.getModelType(),
                        history.getPredictedScore(),
                        history.getRiskLevel(),
                        history.getConfidence(),
                        history.getSummary(),
                        history.getPredictedAt()
                ))
                .toList();
    }

    private void saveHistory(MlPredictionTribuoRequest request, MlPredictionTribuoResponse response) {
        MlPredictionTribuoHistory history = new MlPredictionTribuoHistory();
        history.setPlayerId(request.getPlayerId());
        history.setPlayerName(request.getPlayerName());
        history.setAge(request.getAge());
        history.setPosition(request.getPosition());
        history.setMatchesPlayed(request.getMatchesPlayed());
        history.setGoals(request.getGoals());
        history.setAssists(request.getAssists());
        history.setMinutesPlayed(request.getMinutesPlayed());
        history.setYellowCards(request.getYellowCards());
        history.setRedCards(request.getRedCards());
        history.setShotsOnTarget(request.getShotsOnTarget());
        history.setPassAccuracy(request.getPassAccuracy());
        history.setInjuryStatus(request.getInjuryStatus());
        history.setExpectedGoals(request.getExpectedGoals());
        history.setExpectedAssists(request.getExpectedAssists());
        history.setKeyPasses(request.getKeyPasses());
        history.setProgressivePasses(request.getProgressivePasses());
        history.setDribblesCompleted(request.getDribblesCompleted());
        history.setTacklesWon(request.getTacklesWon());
        history.setInterceptions(request.getInterceptions());
        history.setBallRecoveries(request.getBallRecoveries());
        history.setMatchesMissed(request.getMatchesMissed());
        history.setRecentMatchLoad(request.getRecentMatchLoad());
        history.setModelName(response.getModelName());
        history.setModelType(response.getModelType());
        history.setPredictedScore(response.getPredictedScore());
        history.setRiskLevel(response.getRiskLevel());
        history.setConfidence(response.getConfidence());
        history.setSummary(response.getSummary());
        history.setPredictedAt(LocalDateTime.now());

        historyRepository.save(history);
    }

    private void trainDemoModel() {
        MutableDataset<Regressor> dataset = DemoMlTrainingFactory.buildDemoDataset();

        LinearSGDTrainer trainer = new LinearSGDTrainer(
                new SquaredLoss(),
                new AdaGrad(0.1),
                50,
                1L
        );

        model = trainer.train(dataset);
    }

    private List<MlFeatureImpactTribuoDTO> buildFeatureImportance(MlPredictionTribuoRequest request) {
        List<MlFeatureImpactTribuoDTO> features = new ArrayList<>();

        features.add(new MlFeatureImpactTribuoDTO("expectedGoals", String.valueOf(request.getExpectedGoals()), 0.28, "positive",
                "High expected goals usually improve attacking prediction strength."));
        features.add(new MlFeatureImpactTribuoDTO("assists", String.valueOf(request.getAssists()), 0.18, "positive",
                "Assists increase creative contribution."));
        features.add(new MlFeatureImpactTribuoDTO("shotsOnTarget", String.valueOf(request.getShotsOnTarget()), 0.16, "positive",
                "Shots on target increase attacking threat."));
        features.add(new MlFeatureImpactTribuoDTO("injuryStatus", String.valueOf(request.getInjuryStatus()), 0.15,
                Boolean.TRUE.equals(request.getInjuryStatus()) ? "negative" : "neutral",
                Boolean.TRUE.equals(request.getInjuryStatus()) ? "Injury reduces readiness." : "No injury flag applied."));
        features.add(new MlFeatureImpactTribuoDTO("recentMatchLoad", String.valueOf(request.getRecentMatchLoad()), 0.12,
                request.getRecentMatchLoad() != null && request.getRecentMatchLoad() > 5 ? "negative" : "neutral",
                request.getRecentMatchLoad() != null && request.getRecentMatchLoad() > 5
                        ? "Heavy load may reduce freshness."
                        : "Match load is manageable."));

        return features.stream()
                .sorted(Comparator.comparing(MlFeatureImpactTribuoDTO::getImportance).reversed())
                .toList();
    }

    private String buildSummary(MlPredictionTribuoRequest request, double predictedScore, String riskLevel) {
        StringBuilder summary = new StringBuilder();

        summary.append(request.getPlayerName())
                .append(" is predicted to score ")
                .append(predictedScore)
                .append(" with ")
                .append(riskLevel.toLowerCase())
                .append(" risk. ");

        if (request.getExpectedGoals() != null && request.getExpectedGoals() > 8) {
            summary.append("Strong attacking output is driven by high expected goals. ");
        }

        if (request.getAssists() != null && request.getAssists() > 5) {
            summary.append("Creative contribution is also positive due to assist production. ");
        }

        if (Boolean.TRUE.equals(request.getInjuryStatus())) {
            summary.append("Injury status negatively affects readiness. ");
        }

        if (request.getRecentMatchLoad() != null && request.getRecentMatchLoad() > 5) {
            summary.append("Recent match load may reduce freshness.");
        }

        return summary.toString().trim();
    }

    private void addFeature(Example<Regressor> example, String name, Number value) {
        if (value != null) {
            example.add(new Feature(name, value.doubleValue()));
        }
    }

    private double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}