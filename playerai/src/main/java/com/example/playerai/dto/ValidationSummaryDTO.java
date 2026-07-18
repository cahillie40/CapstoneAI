package com.example.playerai.dto;

import java.util.List;

public class ValidationSummaryDTO {

    private String modelName;
    private String validationStatus;
    private String accuracyEstimate;
    private String summary;
    private List<String> strengths;


    public ValidationSummaryDTO() {
    }

    public ValidationSummaryDTO(String modelName,
                                String validationStatus,
                                String accuracyEstimate,
                                String summary,
                                List<String> strengths) {
        this.modelName = modelName;
        this.validationStatus = validationStatus;
        this.accuracyEstimate = accuracyEstimate;
        this.summary = summary;
        this.strengths = strengths;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(String validationStatus) {
        this.validationStatus = validationStatus;
    }

    public String getAccuracyEstimate() {
        return accuracyEstimate;
    }

    public void setAccuracyEstimate(String accuracyEstimate) {
        this.accuracyEstimate = accuracyEstimate;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }
}