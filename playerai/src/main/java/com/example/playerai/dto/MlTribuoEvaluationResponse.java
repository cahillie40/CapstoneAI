package com.example.playerai.dto;

public class MlTribuoEvaluationResponse {

    private Integer trainingRows;
    private Integer testRows;
    private Double splitRatio;
    private String evaluatedAt;
    private Integer totalPlayers;
    private Integer trainablePlayers;
    private Integer excludedPlayers;
    private String summary;

    public MlTribuoEvaluationResponse() {
    }

    public MlTribuoEvaluationResponse(Integer trainingRows,
                                      Integer testRows,
                                      Double splitRatio,
                                      String evaluatedAt,
                                      Integer totalPlayers,
                                      Integer trainablePlayers,
                                      Integer excludedPlayers,
                                      String summary) {
        this.trainingRows = trainingRows;
        this.testRows = testRows;
        this.splitRatio = splitRatio;
        this.evaluatedAt = evaluatedAt;
        this.totalPlayers = totalPlayers;
        this.trainablePlayers = trainablePlayers;
        this.excludedPlayers = excludedPlayers;
        this.summary = summary;
    }

    public Integer getTrainingRows() {
        return trainingRows;
    }

    public void setTrainingRows(Integer trainingRows) {
        this.trainingRows = trainingRows;
    }

    public Integer getTestRows() {
        return testRows;
    }

    public void setTestRows(Integer testRows) {
        this.testRows = testRows;
    }

    public Double getSplitRatio() {
        return splitRatio;
    }

    public void setSplitRatio(Double splitRatio) {
        this.splitRatio = splitRatio;
    }

    public String getEvaluatedAt() {
        return evaluatedAt;
    }

    public void setEvaluatedAt(String evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }

    public Integer getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(Integer totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    public Integer getTrainablePlayers() {
        return trainablePlayers;
    }

    public void setTrainablePlayers(Integer trainablePlayers) {
        this.trainablePlayers = trainablePlayers;
    }

    public Integer getExcludedPlayers() {
        return excludedPlayers;
    }

    public void setExcludedPlayers(Integer excludedPlayers) {
        this.excludedPlayers = excludedPlayers;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}