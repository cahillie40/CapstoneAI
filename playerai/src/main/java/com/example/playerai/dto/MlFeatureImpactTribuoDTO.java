package com.example.playerai.dto;

public class MlFeatureImpactTribuoDTO {
    private String feature;
    private String value;
    private Double importance;
    private String effect;
    private String explanation;

    public MlFeatureImpactTribuoDTO() {
    }

    public MlFeatureImpactTribuoDTO(String feature, String value, Double importance, String effect, String explanation) {
        this.feature = feature;
        this.value = value;
        this.importance = importance;
        this.effect = effect;
        this.explanation = explanation;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Double getImportance() {
        return importance;
    }

    public void setImportance(Double importance) {
        this.importance = importance;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}