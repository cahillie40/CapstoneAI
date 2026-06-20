package com.example.playerai.dto;

public class DashboardStats {

    private Long totalPlayers;
    private Long totalPredictions;
    private Double averageFormRating;
    private String highestRatedPlayer;
    private Double highestFormRating;
    private Long highRiskCount;
    private Long mediumRiskCount;
    private Long lowRiskCount;

    public DashboardStats() {
    }

    public DashboardStats(Long totalPlayers, Long totalPredictions, Double averageFormRating,
                          String highestRatedPlayer, Double highestFormRating,
                          Long highRiskCount, Long mediumRiskCount, Long lowRiskCount) {
        this.totalPlayers = totalPlayers;
        this.totalPredictions = totalPredictions;
        this.averageFormRating = averageFormRating;
        this.highestRatedPlayer = highestRatedPlayer;
        this.highestFormRating = highestFormRating;
        this.highRiskCount = highRiskCount;
        this.mediumRiskCount = mediumRiskCount;
        this.lowRiskCount = lowRiskCount;
    }

    public Long getTotalPlayers() { return totalPlayers; }
    public void setTotalPlayers(Long totalPlayers) { this.totalPlayers = totalPlayers; }

    public Long getTotalPredictions() { return totalPredictions; }
    public void setTotalPredictions(Long totalPredictions) { this.totalPredictions = totalPredictions; }

    public Double getAverageFormRating() { return averageFormRating; }
    public void setAverageFormRating(Double averageFormRating) { this.averageFormRating = averageFormRating; }

    public String getHighestRatedPlayer() { return highestRatedPlayer; }
    public void setHighestRatedPlayer(String highestRatedPlayer) { this.highestRatedPlayer = highestRatedPlayer; }

    public Double getHighestFormRating() { return highestFormRating; }
    public void setHighestFormRating(Double highestFormRating) { this.highestFormRating = highestFormRating; }

    public Long getHighRiskCount() { return highRiskCount; }
    public void setHighRiskCount(Long highRiskCount) { this.highRiskCount = highRiskCount; }

    public Long getMediumRiskCount() { return mediumRiskCount; }
    public void setMediumRiskCount(Long mediumRiskCount) { this.mediumRiskCount = mediumRiskCount; }

    public Long getLowRiskCount() { return lowRiskCount; }
    public void setLowRiskCount(Long lowRiskCount) { this.lowRiskCount = lowRiskCount; }
}