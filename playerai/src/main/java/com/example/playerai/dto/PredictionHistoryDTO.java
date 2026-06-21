package com.example.playerai.dto;

import java.time.LocalDateTime;

public class PredictionHistoryDTO {

    private Long id;
    private String playerName;
    private Long playerId;
    private Double predictedFormRating;
    private String riskLevel;
    private String summary;
    private String inputData;
    private LocalDateTime createdAt;

    public PredictionHistoryDTO() {
    }

    public PredictionHistoryDTO(Long id, String playerName, Long playerId,
                                Double predictedFormRating, String riskLevel,
                                String summary, String inputData,
                                LocalDateTime createdAt) {
        this.id = id;
        this.playerName = playerName;
        this.playerId = playerId;
        this.predictedFormRating = predictedFormRating;
        this.riskLevel = riskLevel;
        this.summary = summary;
        this.inputData = inputData;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Double getPredictedFormRating() {
        return predictedFormRating;
    }

    public void setPredictedFormRating(Double predictedFormRating) {
        this.predictedFormRating = predictedFormRating;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}