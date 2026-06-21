package com.example.playerai.dto;

public class FeatureWeightDTO {

    private String name;
    private double weight;
    private String explanation;

    public FeatureWeightDTO() {
    }

    public FeatureWeightDTO(String name, double weight, String explanation) {
        this.name = name;
        this.weight = weight;
        this.explanation = explanation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}