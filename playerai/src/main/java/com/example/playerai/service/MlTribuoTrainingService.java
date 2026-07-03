package com.example.playerai.service;

import com.example.playerai.dto.MlModelInfoTribuoDTO;
import com.example.playerai.dto.MlTribuoTrainingInfoResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.tribuo.MutableDataset;
import org.tribuo.math.optimisers.AdaGrad;
import org.tribuo.regression.Regressor;
import org.tribuo.regression.sgd.linear.LinearSGDTrainer;
import org.tribuo.regression.sgd.objectives.SquaredLoss;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MlTribuoTrainingService {

    private final MlTribuoModelManager modelManager;

    public MlTribuoTrainingService(MlTribuoModelManager modelManager) {
        this.modelManager = modelManager;
    }

    @PostConstruct
    public void init() {
        trainModel();
    }

    public MlTribuoTrainingInfoResponse getTrainingInfo() {
        return new MlTribuoTrainingInfoResponse(
                "Tribuo Regression Predictor",
                "Tribuo Linear SGD Regression",
                modelManager.isTrained() ? "Model trained and ready" : "Model not trained",
                modelManager.getTrainingRowCount(),
                modelManager.getTrainingSource(),
                modelManager.getLastTrainedAt() != null ? modelManager.getLastTrainedAt().toString() : null,
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
                "This separate screen uses a Tribuo linear regression pipeline in Java Spring Boot without native XGBoost dependencies.",
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

    public MlTribuoTrainingInfoResponse trainModel() {
        MutableDataset<Regressor> dataset = DemoMlTrainingFactory.buildDemoDataset();

        LinearSGDTrainer trainer = new LinearSGDTrainer(
                new SquaredLoss(),
                new AdaGrad(0.1),
                50,
                1L
        );

        modelManager.setModel(trainer.train(dataset));
        modelManager.setTrained(true);
        modelManager.setTrainingRowCount(dataset.size());
        modelManager.setTrainingSource("DemoMlTrainingFactory");
        modelManager.setLastTrainedAt(LocalDateTime.now());

        return getTrainingInfo();
    }
}