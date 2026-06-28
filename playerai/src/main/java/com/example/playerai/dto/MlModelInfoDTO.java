package com.example.playerai.dto;

import java.util.List;

public class MlModelInfoDTO {
    private String modelName;
    private String modelType;
    private String trainingStatus;
    private String description;
    private List<String> supportedFeatures;

    public MlModelInfoDTO() {
    }

    public MlModelInfoDTO(String modelName, String modelType, String trainingStatus,
                          String description, List<String> supportedFeatures) {
        this.modelName = modelName;
        this.modelType = modelType;
        this.trainingStatus = trainingStatus;
        this.description = description;
        this.supportedFeatures = supportedFeatures;
    }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    public String getModelType() { return modelType; }
    public void setModelType(String modelType) { this.modelType = modelType; }
    public String getTrainingStatus() { return trainingStatus; }
    public void setTrainingStatus(String trainingStatus) { this.trainingStatus = trainingStatus; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getSupportedFeatures() { return supportedFeatures; }
    public void setSupportedFeatures(List<String> supportedFeatures) { this.supportedFeatures = supportedFeatures; }
}