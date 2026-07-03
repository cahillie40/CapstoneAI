package com.example.playerai.dto;

public class MlTribuoEvaluationPlayerRowDTO {
    private String playerName;
    private String position;
    private Double previousScore;
    private Double evaluatedScore;
    private String trend;
    private String trendReason;

    public MlTribuoEvaluationPlayerRowDTO() {
    }

    public MlTribuoEvaluationPlayerRowDTO(
            String playerName,
            String position,
            Double previousScore,
            Double evaluatedScore,
            String trend,
            String trendReason
    ) {
        this.playerName = playerName;
        this.position = position;
        this.previousScore = previousScore;
        this.evaluatedScore = evaluatedScore;
        this.trend = trend;
        this.trendReason = trendReason;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPosition() {
        return position;
    }

    public Double getPreviousScore() {
        return previousScore;
    }

    public Double getEvaluatedScore() {
        return evaluatedScore;
    }

    public String getTrend() {
        return trend;
    }

    public String getTrendReason() {
        return trendReason;
    }
}