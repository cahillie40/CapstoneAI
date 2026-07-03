package com.example.playerai.dto;

public class MlTribuoEvaluationResponse {
    private Double mae;
    private Double rmse;
    private Double r2;
    private Integer trainingRows;
    private Integer testRows;
    private Double splitRatio;
    private String evaluatedAt;
    private String summary;

    public MlTribuoEvaluationResponse() {
    }

    public MlTribuoEvaluationResponse(
            Double mae,
            Double rmse,
            Double r2,
            Integer trainingRows,
            Integer testRows,
            Double splitRatio,
            String evaluatedAt,
            String summary
    ) {
        this.mae = mae;
        this.rmse = rmse;
        this.r2 = r2;
        this.trainingRows = trainingRows;
        this.testRows = testRows;
        this.splitRatio = splitRatio;
        this.evaluatedAt = evaluatedAt;
        this.summary = summary;
    }

    public Double getMae() {
        return mae;
    }

    public Double getRmse() {
        return rmse;
    }

    public Double getR2() {
        return r2;
    }

    public Integer getTrainingRows() {
        return trainingRows;
    }

    public Integer getTestRows() {
        return testRows;
    }

    public Double getSplitRatio() {
        return splitRatio;
    }

    public String getEvaluatedAt() {
        return evaluatedAt;
    }

    public String getSummary() {
        return summary;
    }
}