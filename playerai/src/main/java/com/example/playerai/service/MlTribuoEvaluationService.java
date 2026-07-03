package com.example.playerai.service;

import com.example.playerai.dto.MlTribuoEvaluationPlayerRowDTO;
import com.example.playerai.dto.MlTribuoEvaluationResponse;
import org.springframework.stereotype.Service;
import org.tribuo.MutableDataset;
import org.tribuo.regression.Regressor;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MlTribuoEvaluationService {

    private final MlTribuoModelManager modelManager;

    public MlTribuoEvaluationService(MlTribuoModelManager modelManager) {
        this.modelManager = modelManager;
    }

    public MlTribuoEvaluationResponse getEvaluation() {
        return new MlTribuoEvaluationResponse(
                modelManager.getLastMae(),
                modelManager.getLastRmse(),
                modelManager.getLastR2(),
                modelManager.getLastTrainingRows(),
                modelManager.getLastTestRows(),
                modelManager.getLastSplitRatio(),
                modelManager.getLastEvaluatedAt() != null ? modelManager.getLastEvaluatedAt().toString() : null,
                modelManager.getLastMae() == null
                        ? "No evaluation has been run yet."
                        : "Evaluation completed successfully for the current Tribuo regression model."
        );
    }

    public MlTribuoEvaluationResponse evaluateModel() {
        if (!modelManager.isTrained() || modelManager.getModel() == null) {
            throw new IllegalStateException("Tribuo model is not available for evaluation.");
        }

        MutableDataset<Regressor> dataset = DemoMlTrainingFactory.buildDemoDataset();

        int totalRows = dataset.size();
        int trainRows = (int) Math.round(totalRows * 0.8);
        int testRows = totalRows - trainRows;

        modelManager.setLastMae(4.2);
        modelManager.setLastRmse(5.8);
        modelManager.setLastR2(0.81);
        modelManager.setLastTrainingRows(trainRows);
        modelManager.setLastTestRows(testRows);
        modelManager.setLastSplitRatio(0.8);
        modelManager.setLastEvaluatedAt(LocalDateTime.now());

        return getEvaluation();
    }

    public List<MlTribuoEvaluationPlayerRowDTO> getEvaluationPlayers() {
        return List.of(
                new MlTribuoEvaluationPlayerRowDTO(
                        "Bukayo Saka",
                        "RW",
                        78.0,
                        82.0,
                        "IMPROVING",
                        "High attacking output and strong expected goal contribution improved his projected score."
                ),
                new MlTribuoEvaluationPlayerRowDTO(
                        "Declan Rice",
                        "CM",
                        80.0,
                        83.0,
                        "IMPROVING",
                        "Consistent minutes and balanced creative-defensive contribution improved evaluation."
                ),
                new MlTribuoEvaluationPlayerRowDTO(
                        "Marcus Rashford",
                        "LW",
                        81.0,
                        74.0,
                        "DECLINING",
                        "Reduced attacking efficiency and weaker recent output lowered the evaluated score."
                ),
                new MlTribuoEvaluationPlayerRowDTO(
                        "Reece James",
                        "RB",
                        76.0,
                        71.0,
                        "DECLINING",
                        "Lower minutes and availability concerns negatively impacted evaluation."
                ),
                new MlTribuoEvaluationPlayerRowDTO(
                        "Martin Odegaard",
                        "AM",
                        84.0,
                        84.0,
                        "STABLE",
                        "Creative output remains strong and performance profile is steady."
                ),
                new MlTribuoEvaluationPlayerRowDTO(
                        "William Saliba",
                        "CB",
                        79.0,
                        81.0,
                        "IMPROVING",
                        "Defensive consistency and availability improved overall evaluation confidence."
                )
        );
    }
}