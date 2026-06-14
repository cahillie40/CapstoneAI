package com.example.playerai.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "predictions")
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    private Double predictedFormRating;
    private String riskLevel;
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String inputData;

    private LocalDateTime createdAt;

    public Prediction() {
    }

    public Prediction(Player player, Double predictedFormRating, String riskLevel,
                      String summary, String inputData, LocalDateTime createdAt) {
        this.player = player;
        this.predictedFormRating = predictedFormRating;
        this.riskLevel = riskLevel;
        this.summary = summary;
        this.inputData = inputData;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
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

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}