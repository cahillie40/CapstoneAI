package com.example.playerai.dto;

public class MlPredictionTribuoRequest {
    private Long playerId;
    private String playerName;
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
    private Double expectedGoals;
    private Double expectedAssists;
    private Integer keyPasses;
    private Integer progressivePasses;
    private Integer dribblesCompleted;
    private Integer tacklesWon;
    private Integer interceptions;
    private Integer ballRecoveries;
    private Integer matchesMissed;
    private Integer recentMatchLoad;

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
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

    public Double getExpectedGoals() {
        return expectedGoals;
    }

    public void setExpectedGoals(Double expectedGoals) {
        this.expectedGoals = expectedGoals;
    }

    public Double getExpectedAssists() {
        return expectedAssists;
    }

    public void setExpectedAssists(Double expectedAssists) {
        this.expectedAssists = expectedAssists;
    }

    public Integer getKeyPasses() {
        return keyPasses;
    }

    public void setKeyPasses(Integer keyPasses) {
        this.keyPasses = keyPasses;
    }

    public Integer getProgressivePasses() {
        return progressivePasses;
    }

    public void setProgressivePasses(Integer progressivePasses) {
        this.progressivePasses = progressivePasses;
    }

    public Integer getDribblesCompleted() {
        return dribblesCompleted;
    }

    public void setDribblesCompleted(Integer dribblesCompleted) {
        this.dribblesCompleted = dribblesCompleted;
    }

    public Integer getTacklesWon() {
        return tacklesWon;
    }

    public void setTacklesWon(Integer tacklesWon) {
        this.tacklesWon = tacklesWon;
    }

    public Integer getInterceptions() {
        return interceptions;
    }

    public void setInterceptions(Integer interceptions) {
        this.interceptions = interceptions;
    }

    public Integer getBallRecoveries() {
        return ballRecoveries;
    }

    public void setBallRecoveries(Integer ballRecoveries) {
        this.ballRecoveries = ballRecoveries;
    }

    public Integer getMatchesMissed() {
        return matchesMissed;
    }

    public void setMatchesMissed(Integer matchesMissed) {
        this.matchesMissed = matchesMissed;
    }

    public Integer getRecentMatchLoad() {
        return recentMatchLoad;
    }

    public void setRecentMatchLoad(Integer recentMatchLoad) {
        this.recentMatchLoad = recentMatchLoad;
    }
}