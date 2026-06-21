package com.example.playerai.dto;

public class ScoreStepDTO {

    private String label;
    private Double inputValue;
    private Double contribution;
    private Double runningTotal;
    private String explanation;

    public ScoreStepDTO() {
    }

    public ScoreStepDTO(String label,
                        Double inputValue,
                        Double contribution,
                        Double runningTotal,
                        String explanation) {
        this.label = label;
        this.inputValue = inputValue;
        this.contribution = contribution;
        this.runningTotal = runningTotal;
        this.explanation = explanation;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Double getInputValue() {
        return inputValue;
    }

    public void setInputValue(Double inputValue) {
        this.inputValue = inputValue;
    }

    public Double getContribution() {
        return contribution;
    }

    public void setContribution(Double contribution) {
        this.contribution = contribution;
    }

    public Double getRunningTotal() {
        return runningTotal;
    }

    public void setRunningTotal(Double runningTotal) {
        this.runningTotal = runningTotal;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}