package com.example.playerai.dto;

import java.util.List;

public class MlTribuoTrainingInfoResponse {
    private String modelName;
    private String modelType;
    private String trainingStatus;
    private Integer trainingRowCount;
    private String trainingSource;
    private String lastTrainedAt;
    private List<String> supportedFeatures;

    public MlTribuoTrainingInfoResponse() {
    }

    public MlTribuoTrainingInfoResponse(
            String modelName,
            String modelType,
            String trainingStatus,
            Integer trainingRowCount,
            String trainingSource,
            String lastTrainedAt,
            List<String> supportedFeatures
    ) {
        this.modelName = modelName;
        this.modelType = modelType;
        this.trainingStatus = trainingStatus;
        this.trainingRowCount = trainingRowCount;
        this.trainingSource = trainingSource;
        this.lastTrainedAt = lastTrainedAt;
        this.supportedFeatures = supportedFeatures;
    }

    public String getModelName() {
        return modelName;
    }

    public String getModelType() {
        return modelType;
    }

    public String getTrainingStatus() {
        return trainingStatus;
    }

    public Integer getTrainingRowCount() {
        return trainingRowCount;
    }

    public String getTrainingSource() {
        return trainingSource;
    }

    public String getLastTrainedAt() {
        return lastTrainedAt;
    }

    public List<String> getSupportedFeatures() {
        return supportedFeatures;
    }
}