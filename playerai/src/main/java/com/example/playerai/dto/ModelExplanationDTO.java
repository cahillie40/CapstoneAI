package com.example.playerai.dto;

import java.util.List;

public class ModelExplanationDTO {

    private String modelType;
    private String description;
    private List<FeatureWeight> featureWeights;
    private List<RiskLevel> riskLevels;
    private String baselineScore;

    public ModelExplanationDTO() {}

    public ModelExplanationDTO(String modelType, String description,
                               List<FeatureWeight> featureWeights,
                               List<RiskLevel> riskLevels,
                               String baselineScore) {
        this.modelType      = modelType;
        this.description    = description;
        this.featureWeights = featureWeights;
        this.riskLevels     = riskLevels;
        this.baselineScore  = baselineScore;
    }

    public String getModelType()                    { return modelType; }
    public String getDescription()                  { return description; }
    public List<FeatureWeight> getFeatureWeights()  { return featureWeights; }
    public List<RiskLevel> getRiskLevels()          { return riskLevels; }
    public String getBaselineScore()                { return baselineScore; }

    public void setModelType(String modelType)                          { this.modelType = modelType; }
    public void setDescription(String description)                      { this.description = description; }
    public void setFeatureWeights(List<FeatureWeight> featureWeights)   { this.featureWeights = featureWeights; }
    public void setRiskLevels(List<RiskLevel> riskLevels)               { this.riskLevels = riskLevels; }
    public void setBaselineScore(String baselineScore)                  { this.baselineScore = baselineScore; }

    // Inner classes
    public static class FeatureWeight {
        private String feature;
        private String weight;
        private String direction;
        private String description;

        public FeatureWeight(String feature, String weight, String direction, String description) {
            this.feature     = feature;
            this.weight      = weight;
            this.direction   = direction;
            this.description = description;
        }

        public String getFeature()      { return feature; }
        public String getWeight()       { return weight; }
        public String getDirection()    { return direction; }
        public String getDescription()  { return description; }
    }

    public static class RiskLevel {
        private String level;
        private String range;
        private String meaning;

        public RiskLevel(String level, String range, String meaning) {
            this.level   = level;
            this.range   = range;
            this.meaning = meaning;
        }

        public String getLevel()    { return level; }
        public String getRange()    { return range; }
        public String getMeaning()  { return meaning; }
    }
}