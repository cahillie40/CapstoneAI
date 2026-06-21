package com.example.playerai.dto;

public class ValidationSummaryDTO {

    private long totalPredictions;
    private long highRiskCount;
    private long mediumRiskCount;
    private long lowRiskCount;
    private double averageRating;
    private double highRiskPercentage;
    private double mediumRiskPercentage;
    private double lowRiskPercentage;
    private double highestRating;
    private double lowestRating;
    private String mostCommonRiskLevel;

    public ValidationSummaryDTO() {}

    public ValidationSummaryDTO(long totalPredictions, long highRiskCount, long mediumRiskCount,
                                long lowRiskCount, double averageRating, double highestRating,
                                double lowestRating) {
        this.totalPredictions   = totalPredictions;
        this.highRiskCount      = highRiskCount;
        this.mediumRiskCount    = mediumRiskCount;
        this.lowRiskCount       = lowRiskCount;
        this.averageRating      = averageRating;
        this.highestRating      = highestRating;
        this.lowestRating       = lowestRating;

        if (totalPredictions > 0) {
            this.highRiskPercentage   = Math.round((highRiskCount   * 100.0 / totalPredictions) * 10.0) / 10.0;
            this.mediumRiskPercentage = Math.round((mediumRiskCount * 100.0 / totalPredictions) * 10.0) / 10.0;
            this.lowRiskPercentage    = Math.round((lowRiskCount    * 100.0 / totalPredictions) * 10.0) / 10.0;
        }

        if (lowRiskCount >= mediumRiskCount && lowRiskCount >= highRiskCount) {
            this.mostCommonRiskLevel = "LOW";
        } else if (mediumRiskCount >= highRiskCount) {
            this.mostCommonRiskLevel = "MEDIUM";
        } else {
            this.mostCommonRiskLevel = "HIGH";
        }
    }

    public long getTotalPredictions()       { return totalPredictions; }
    public long getHighRiskCount()          { return highRiskCount; }
    public long getMediumRiskCount()        { return mediumRiskCount; }
    public long getLowRiskCount()           { return lowRiskCount; }
    public double getAverageRating()        { return averageRating; }
    public double getHighRiskPercentage()   { return highRiskPercentage; }
    public double getMediumRiskPercentage() { return mediumRiskPercentage; }
    public double getLowRiskPercentage()    { return lowRiskPercentage; }
    public double getHighestRating()        { return highestRating; }
    public double getLowestRating()         { return lowestRating; }
    public String getMostCommonRiskLevel()  { return mostCommonRiskLevel; }

    public void setTotalPredictions(long totalPredictions)             { this.totalPredictions = totalPredictions; }
    public void setHighRiskCount(long highRiskCount)                   { this.highRiskCount = highRiskCount; }
    public void setMediumRiskCount(long mediumRiskCount)               { this.mediumRiskCount = mediumRiskCount; }
    public void setLowRiskCount(long lowRiskCount)                     { this.lowRiskCount = lowRiskCount; }
    public void setAverageRating(double averageRating)                 { this.averageRating = averageRating; }
    public void setHighRiskPercentage(double highRiskPercentage)       { this.highRiskPercentage = highRiskPercentage; }
    public void setMediumRiskPercentage(double mediumRiskPercentage)   { this.mediumRiskPercentage = mediumRiskPercentage; }
    public void setLowRiskPercentage(double lowRiskPercentage)         { this.lowRiskPercentage = lowRiskPercentage; }
    public void setHighestRating(double highestRating)                 { this.highestRating = highestRating; }
    public void setLowestRating(double lowestRating)                   { this.lowestRating = lowestRating; }
    public void setMostCommonRiskLevel(String mostCommonRiskLevel)     { this.mostCommonRiskLevel = mostCommonRiskLevel; }
}