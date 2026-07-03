package com.example.playerai.dto;

public class MlTribuoTrainingPreviewRowDTO {
    private String playerName;
    private String position;
    private Integer age;
    private Integer goals;
    private Integer assists;
    private Integer minutesPlayed;
    private Double expectedGoals;
    private Double expectedAssists;
    private Double previousScore;
    private Double currentTargetScore;
    private String trend;
    private String trendReason;

    public MlTribuoTrainingPreviewRowDTO() {
    }

    public MlTribuoTrainingPreviewRowDTO(
            String playerName,
            String position,
            Integer age,
            Integer goals,
            Integer assists,
            Integer minutesPlayed,
            Double expectedGoals,
            Double expectedAssists,
            Double previousScore,
            Double currentTargetScore,
            String trend,
            String trendReason
    ) {
        this.playerName = playerName;
        this.position = position;
        this.age = age;
        this.goals = goals;
        this.assists = assists;
        this.minutesPlayed = minutesPlayed;
        this.expectedGoals = expectedGoals;
        this.expectedAssists = expectedAssists;
        this.previousScore = previousScore;
        this.currentTargetScore = currentTargetScore;
        this.trend = trend;
        this.trendReason = trendReason;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPosition() {
        return position;
    }

    public Integer getAge() {
        return age;
    }

    public Integer getGoals() {
        return goals;
    }

    public Integer getAssists() {
        return assists;
    }

    public Integer getMinutesPlayed() {
        return minutesPlayed;
    }

    public Double getExpectedGoals() {
        return expectedGoals;
    }

    public Double getExpectedAssists() {
        return expectedAssists;
    }

    public Double getPreviousScore() {
        return previousScore;
    }

    public Double getCurrentTargetScore() {
        return currentTargetScore;
    }

    public String getTrend() {
        return trend;
    }

    public String getTrendReason() {
        return trendReason;
    }
}