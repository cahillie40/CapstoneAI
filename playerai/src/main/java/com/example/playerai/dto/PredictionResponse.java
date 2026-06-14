package com.example.playerai.dto;

public class PredictionResponse {

    private Double predictedFormRating;
    private String riskLevel;
    private String summary;

    public PredictionResponse() {
    }

    public PredictionResponse(Double predictedFormRating, String riskLevel, String summary) {
        this.predictedFormRating = predictedFormRating;
        this.riskLevel = riskLevel;
        this.summary = summary;
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
}