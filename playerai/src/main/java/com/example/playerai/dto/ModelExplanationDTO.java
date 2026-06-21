package com.example.playerai.dto;

import java.util.List;

public class ModelExplanationDTO {

    private String modelType;
    private String overview;
    private List<FeatureWeightDTO> featureWeights;
    private List<String> riskLogic;

    public ModelExplanationDTO() {
    }

    public ModelExplanationDTO(String modelType,
                               String overview,
                               List<FeatureWeightDTO> featureWeights,
                               List<String> riskLogic) {
        this.modelType = modelType;
        this.overview = overview;
        this.featureWeights = featureWeights;
        this.riskLogic = riskLogic;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public List<FeatureWeightDTO> getFeatureWeights() {
        return featureWeights;
    }

    public void setFeatureWeights(List<FeatureWeightDTO> featureWeights) {
        this.featureWeights = featureWeights;
    }

    public List<String> getRiskLogic() {
        return riskLogic;
    }

    public void setRiskLogic(List<String> riskLogic) {
        this.riskLogic = riskLogic;
    }
}