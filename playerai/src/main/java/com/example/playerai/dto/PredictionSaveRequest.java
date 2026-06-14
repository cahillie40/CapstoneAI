package com.example.playerai.dto;

public class PredictionSaveRequest {

    private Long playerId;
    private PredictionRequest predictionRequest;

    public PredictionSaveRequest() {
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public PredictionRequest getPredictionRequest() {
        return predictionRequest;
    }

    public void setPredictionRequest(PredictionRequest predictionRequest) {
        this.predictionRequest = predictionRequest;
    }
}