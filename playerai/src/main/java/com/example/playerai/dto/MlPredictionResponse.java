package com.example.playerai.dto;

import java.util.List;

public class MlPredictionResponse {
    private String playerName;
    private String modelType;
    private Double predictedScore;
    private String riskLevel;
    private Double confidence;
    private String summary;
    private List<MlFeatureImpactDTO> topFeatures;

    public MlPredictionResponse() {
    }

    public MlPredictionResponse(String playerName, String modelType, Double predictedScore,
                                String riskLevel, Double confidence, String summary,
                                List<MlFeatureImpactDTO> topFeatures) {
        this.playerName = playerName;
        this.modelType = modelType;
        this.predictedScore = predictedScore;
        this.riskLevel = riskLevel;
        this.confidence = confidence;
        this.summary = summary;
        this.topFeatures = topFeatures;
    }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public String getModelType() { return modelType; }
    public void setModelType(String modelType) { this.modelType = modelType; }
    public Double getPredictedScore() { return predictedScore; }
    public void setPredictedScore(Double predictedScore) { this.predictedScore = predictedScore; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public List<MlFeatureImpactDTO> getTopFeatures() { return topFeatures; }
    public void setTopFeatures(List<MlFeatureImpactDTO> topFeatures) { this.topFeatures = topFeatures; }
}