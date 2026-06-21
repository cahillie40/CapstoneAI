package com.example.playerai.dto;

import java.util.List;

public class PredictionResponse {

    private String playerName;
    private Double baselineScore;
    private Double predictedFormRating;
    private String riskLevel;
    private String summary;
    private List<FactorContributionDTO> positiveFactors;
    private List<FactorContributionDTO> negativeFactors;
    private List<FactorContributionDTO> allFactors;
    private List<ScoreStepDTO> scoreSteps;

    public PredictionResponse() {
    }

    public PredictionResponse(String playerName,
                              Double baselineScore,
                              Double predictedFormRating,
                              String riskLevel,
                              String summary,
                              List<FactorContributionDTO> positiveFactors,
                              List<FactorContributionDTO> negativeFactors,
                              List<FactorContributionDTO> allFactors,
                              List<ScoreStepDTO> scoreSteps) {
        this.playerName = playerName;
        this.baselineScore = baselineScore;
        this.predictedFormRating = predictedFormRating;
        this.riskLevel = riskLevel;
        this.summary = summary;
        this.positiveFactors = positiveFactors;
        this.negativeFactors = negativeFactors;
        this.allFactors = allFactors;
        this.scoreSteps = scoreSteps;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Double getBaselineScore() {
        return baselineScore;
    }

    public void setBaselineScore(Double baselineScore) {
        this.baselineScore = baselineScore;
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

    public List<FactorContributionDTO> getPositiveFactors() {
        return positiveFactors;
    }

    public void setPositiveFactors(List<FactorContributionDTO> positiveFactors) {
        this.positiveFactors = positiveFactors;
    }

    public List<FactorContributionDTO> getNegativeFactors() {
        return negativeFactors;
    }

    public void setNegativeFactors(List<FactorContributionDTO> negativeFactors) {
        this.negativeFactors = negativeFactors;
    }

    public List<FactorContributionDTO> getAllFactors() {
        return allFactors;
    }

    public void setAllFactors(List<FactorContributionDTO> allFactors) {
        this.allFactors = allFactors;
    }

    public List<ScoreStepDTO> getScoreSteps() {
        return scoreSteps;
    }

    public void setScoreSteps(List<ScoreStepDTO> scoreSteps) {
        this.scoreSteps = scoreSteps;
    }
}