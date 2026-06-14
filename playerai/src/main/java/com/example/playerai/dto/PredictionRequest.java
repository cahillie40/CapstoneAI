package com.example.playerai.dto;

public class PredictionRequest {

    private Integer age;
    private String position;
    private Integer matchesPlayed;
    private Integer goals;
    private Integer assists;
    private Integer minutesPlayed;
    private Integer yellowCards;
    private Integer redCards;
    private Integer shotsOnTarget;
    private Double passAccuracy;
    private Boolean injuryStatus;

    public PredictionRequest() {
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Integer getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(Integer matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public Integer getGoals() {
        return goals;
    }

    public void setGoals(Integer goals) {
        this.goals = goals;
    }

    public Integer getAssists() {
        return assists;
    }

    public void setAssists(Integer assists) {
        this.assists = assists;
    }

    public Integer getMinutesPlayed() {
        return minutesPlayed;
    }

    public void setMinutesPlayed(Integer minutesPlayed) {
        this.minutesPlayed = minutesPlayed;
    }

    public Integer getYellowCards() {
        return yellowCards;
    }

    public void setYellowCards(Integer yellowCards) {
        this.yellowCards = yellowCards;
    }

    public Integer getRedCards() {
        return redCards;
    }

    public void setRedCards(Integer redCards) {
        this.redCards = redCards;
    }

    public Integer getShotsOnTarget() {
        return shotsOnTarget;
    }

    public void setShotsOnTarget(Integer shotsOnTarget) {
        this.shotsOnTarget = shotsOnTarget;
    }

    public Double getPassAccuracy() {
        return passAccuracy;
    }

    public void setPassAccuracy(Double passAccuracy) {
        this.passAccuracy = passAccuracy;
    }

    public Boolean getInjuryStatus() {
        return injuryStatus;
    }

    public void setInjuryStatus(Boolean injuryStatus) {
        this.injuryStatus = injuryStatus;
    }
}