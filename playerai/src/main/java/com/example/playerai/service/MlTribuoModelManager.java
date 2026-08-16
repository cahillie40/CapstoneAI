package com.example.playerai.service;

import com.example.playerai.dto.MlTribuoEvaluationPlayerRowDTO;
import com.example.playerai.dto.MlTribuoTrainingPreviewRowDTO;
import org.springframework.stereotype.Component;
import org.tribuo.Model;
import org.tribuo.regression.Regressor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class MlTribuoModelManager {

    private Model<Regressor> model;
    private boolean trained;

    private Integer trainingRowCount = 0;
    private String trainingSource = "DemoMlTrainingFactory";
    private LocalDateTime lastTrainedAt;

    private Double lastMae;
    private Double lastRmse;
    private Double lastR2;
    private Integer lastTrainingRows;
    private Integer lastTestRows;
    private Double lastSplitRatio;
    private LocalDateTime lastEvaluatedAt;

    private List<MlTribuoEvaluationPlayerRowDTO> lastEvaluationPlayers = new ArrayList<>();
    private List<MlTribuoTrainingPreviewRowDTO> lastTrainingPreviewRows = new ArrayList<>();

    public Model<Regressor> getModel() {
        return model;
    }

    public void setModel(Model<Regressor> model) {
        this.model = model;
    }

    public boolean isTrained() {
        return trained;
    }

    public void setTrained(boolean trained) {
        this.trained = trained;
    }

    public Integer getTrainingRowCount() {
        return trainingRowCount;
    }

    public void setTrainingRowCount(Integer trainingRowCount) {
        this.trainingRowCount = trainingRowCount;
    }

    public String getTrainingSource() {
        return trainingSource;
    }

    public void setTrainingSource(String trainingSource) {
        this.trainingSource = trainingSource;
    }

    public LocalDateTime getLastTrainedAt() {
        return lastTrainedAt;
    }

    public void setLastTrainedAt(LocalDateTime lastTrainedAt) {
        this.lastTrainedAt = lastTrainedAt;
    }

    public Double getLastMae() {
        return lastMae;
    }

    public void setLastMae(Double lastMae) {
        this.lastMae = lastMae;
    }

    public Double getLastRmse() {
        return lastRmse;
    }

    public void setLastRmse(Double lastRmse) {
        this.lastRmse = lastRmse;
    }

    public Double getLastR2() {
        return lastR2;
    }

    public void setLastR2(Double lastR2) {
        this.lastR2 = lastR2;
    }

    public Integer getLastTrainingRows() {
        return lastTrainingRows;
    }

    public void setLastTrainingRows(Integer lastTrainingRows) {
        this.lastTrainingRows = lastTrainingRows;
    }

    public Integer getLastTestRows() {
        return lastTestRows;
    }

    public void setLastTestRows(Integer lastTestRows) {
        this.lastTestRows = lastTestRows;
    }

    public Double getLastSplitRatio() {
        return lastSplitRatio;
    }

    public void setLastSplitRatio(Double lastSplitRatio) {
        this.lastSplitRatio = lastSplitRatio;
    }

    public LocalDateTime getLastEvaluatedAt() {
        return lastEvaluatedAt;
    }

    public void setLastEvaluatedAt(LocalDateTime lastEvaluatedAt) {
        this.lastEvaluatedAt = lastEvaluatedAt;
    }

    public List<MlTribuoEvaluationPlayerRowDTO> getLastEvaluationPlayers() {
        return lastEvaluationPlayers;
    }

    public void setLastEvaluationPlayers(List<MlTribuoEvaluationPlayerRowDTO> lastEvaluationPlayers) {
        this.lastEvaluationPlayers = lastEvaluationPlayers != null ? lastEvaluationPlayers : new ArrayList<>();
    }

    public List<MlTribuoTrainingPreviewRowDTO> getLastTrainingPreviewRows() {
        return lastTrainingPreviewRows;
    }

    public void setLastTrainingPreviewRows(List<MlTribuoTrainingPreviewRowDTO> lastTrainingPreviewRows) {
        this.lastTrainingPreviewRows = lastTrainingPreviewRows != null ? lastTrainingPreviewRows : new ArrayList<>();
    }
}