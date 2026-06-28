package com.example.playerai.dto;

import java.time.LocalDateTime;

public class MlPredictionTribuoHistoryResponse {
    private Long id;
    private Long playerId;
    private String playerName;
    private String modelName;
    private String modelType;
    private Double predictedScore;
    private String riskLevel;
    private Double confidence;
    private String summary;
    private LocalDateTime predictedAt;

    public MlPredictionTribuoHistoryResponse() {
    }

    public MlPredictionTribuoHistoryResponse(Long id, Long playerId, String playerName, String modelName,
                                             String modelType, Double predictedScore, String riskLevel,
                                             Double confidence, String summary, LocalDateTime predictedAt) {
        this.id = id;
        this.playerId = playerId;
        this.playerName = playerName;
        this.modelName = modelName;
        this.modelType = modelType;
        this.predictedScore = predictedScore;
        this.riskLevel = riskLevel;
        this.confidence = confidence;
        this.summary = summary;
        this.predictedAt = predictedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getModelName() {
        return modelName;
    }

    public String getModelType() {
        return modelType;
    }

    public Double getPredictedScore() {
        return predictedScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public Double getConfidence() {
        return confidence;
    }

    public String getSummary() {
        return summary;
    }

    public LocalDateTime getPredictedAt() {
        return predictedAt;
    }
}