package com.example.playerai.dto;

import java.util.List;

public class MlTribuoTrainingInfoResponse {

    private String modelName;
    private String algorithm;
    private String trainingStatus;
    private Integer trainingRowCount;
    private String trainingSource;
    private String lastTrainedAt;
    private Integer totalPlayers;
    private Integer trainablePlayers;
    private Integer excludedPlayers;
    private List<String> featuresUsed;

    public MlTribuoTrainingInfoResponse() {
    }

    public MlTribuoTrainingInfoResponse(String modelName,
                                        String algorithm,
                                        String trainingStatus,
                                        Integer trainingRowCount,
                                        String trainingSource,
                                        String lastTrainedAt,
                                        Integer totalPlayers,
                                        Integer trainablePlayers,
                                        Integer excludedPlayers,
                                        List<String> featuresUsed) {
        this.modelName = modelName;
        this.algorithm = algorithm;
        this.trainingStatus = trainingStatus;
        this.trainingRowCount = trainingRowCount;
        this.trainingSource = trainingSource;
        this.lastTrainedAt = lastTrainedAt;
        this.totalPlayers = totalPlayers;
        this.trainablePlayers = trainablePlayers;
        this.excludedPlayers = excludedPlayers;
        this.featuresUsed = featuresUsed;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getTrainingStatus() {
        return trainingStatus;
    }

    public void setTrainingStatus(String trainingStatus) {
        this.trainingStatus = trainingStatus;
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

    public String getLastTrainedAt() {
        return lastTrainedAt;
    }

    public void setLastTrainedAt(String lastTrainedAt) {
        this.lastTrainedAt = lastTrainedAt;
    }

    public Integer getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(Integer totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    public Integer getTrainablePlayers() {
        return trainablePlayers;
    }

    public void setTrainablePlayers(Integer trainablePlayers) {
        this.trainablePlayers = trainablePlayers;
    }

    public Integer getExcludedPlayers() {
        return excludedPlayers;
    }

    public void setExcludedPlayers(Integer excludedPlayers) {
        this.excludedPlayers = excludedPlayers;
    }

    public List<String> getFeaturesUsed() {
        return featuresUsed;
    }

    public void setFeaturesUsed(List<String> featuresUsed) {
        this.featuresUsed = featuresUsed;
    }
}