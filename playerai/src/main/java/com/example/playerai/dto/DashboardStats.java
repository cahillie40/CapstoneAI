package com.example.playerai.dto;

public class DashboardStats {

    private long totalPlayers;
    private long injuredPlayers;
    private long totalPredictions;
    private double averageFormRating;
    private double averageExpectedGoals;
    private double averageExpectedAssists;
    private double averageBallRecoveries;

    public DashboardStats() {
    }

    public DashboardStats(long totalPlayers, long injuredPlayers, long totalPredictions,
                          double averageFormRating, double averageExpectedGoals,
                          double averageExpectedAssists, double averageBallRecoveries) {
        this.totalPlayers = totalPlayers;
        this.injuredPlayers = injuredPlayers;
        this.totalPredictions = totalPredictions;
        this.averageFormRating = averageFormRating;
        this.averageExpectedGoals = averageExpectedGoals;
        this.averageExpectedAssists = averageExpectedAssists;
        this.averageBallRecoveries = averageBallRecoveries;
    }

    public long getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(long totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    public long getInjuredPlayers() {
        return injuredPlayers;
    }

    public void setInjuredPlayers(long injuredPlayers) {
        this.injuredPlayers = injuredPlayers;
    }

    public long getTotalPredictions() {
        return totalPredictions;
    }

    public void setTotalPredictions(long totalPredictions) {
        this.totalPredictions = totalPredictions;
    }

    public double getAverageFormRating() {
        return averageFormRating;
    }

    public void setAverageFormRating(double averageFormRating) {
        this.averageFormRating = averageFormRating;
    }

    public double getAverageExpectedGoals() {
        return averageExpectedGoals;
    }

    public void setAverageExpectedGoals(double averageExpectedGoals) {
        this.averageExpectedGoals = averageExpectedGoals;
    }

    public double getAverageExpectedAssists() {
        return averageExpectedAssists;
    }

    public void setAverageExpectedAssists(double averageExpectedAssists) {
        this.averageExpectedAssists = averageExpectedAssists;
    }

    public double getAverageBallRecoveries() {
        return averageBallRecoveries;
    }

    public void setAverageBallRecoveries(double averageBallRecoveries) {
        this.averageBallRecoveries = averageBallRecoveries;
    }
}