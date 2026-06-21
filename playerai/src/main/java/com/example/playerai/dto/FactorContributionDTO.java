package com.example.playerai.dto;

public class FactorContributionDTO {

    private String feature;
    private Double value;
    private Double contribution;
    private String direction;
    private String explanation;

    public FactorContributionDTO() {
    }

    public FactorContributionDTO(String feature,
                                 Double value,
                                 Double contribution,
                                 String direction,
                                 String explanation) {
        this.feature = feature;
        this.value = value;
        this.contribution = contribution;
        this.direction = direction;
        this.explanation = explanation;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getContribution() {
        return contribution;
    }

    public void setContribution(Double contribution) {
        this.contribution = contribution;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}