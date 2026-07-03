package com.example.playerai.service;

import com.example.playerai.dto.MlModelInfoTribuoDTO;
import com.example.playerai.dto.MlTribuoTrainingInfoResponse;
import com.example.playerai.dto.MlTribuoTrainingPreviewRowDTO;
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


    public List<MlTribuoTrainingPreviewRowDTO> getTrainingDataPreview() {
        return List.of(
                new MlTribuoTrainingPreviewRowDTO(
                        "Bukayo Saka", "RW", 24, 12, 9, 2100,
                        10.4, 6.8, 78.0, 82.0,
                        "IMPROVING",
                        "Increased attacking output driven by stronger xG and assist contribution."
                ),
                new MlTribuoTrainingPreviewRowDTO(
                        "Declan Rice", "CM", 27, 6, 8, 2450,
                        4.1, 5.2, 80.0, 83.0,
                        "IMPROVING",
                        "Better all-round contribution with strong minutes and creative support."
                ),
                new MlTribuoTrainingPreviewRowDTO(
                        "Marcus Rashford", "LW", 27, 8, 4, 1980,
                        7.1, 3.0, 81.0, 74.0,
                        "DECLINING",
                        "Lower recent output and reduced attacking efficiency decreased projected score."
                ),
                new MlTribuoTrainingPreviewRowDTO(
                        "Reece James", "RB", 25, 2, 5, 1600,
                        1.5, 4.7, 76.0, 71.0,
                        "DECLINING",
                        "Availability concerns and reduced minutes lowered readiness and impact."
                ),
                new MlTribuoTrainingPreviewRowDTO(
                        "Martin Odegaard", "AM", 26, 10, 11, 2250,
                        8.6, 7.9, 84.0, 84.0,
                        "STABLE",
                        "Performance profile remains consistently strong with balanced creative output."
                )
        );
    }
}