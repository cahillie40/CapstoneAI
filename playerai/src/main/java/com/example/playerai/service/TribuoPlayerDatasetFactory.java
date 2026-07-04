package com.example.playerai.service;

import com.example.playerai.entity.Player;
import com.example.playerai.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.tribuo.MutableDataset;
import org.tribuo.impl.ArrayExample;
import org.tribuo.provenance.SimpleDataSourceProvenance;
import org.tribuo.regression.Regressor;
import org.tribuo.regression.RegressionFactory;

import java.util.List;

@Service
public class TribuoPlayerDatasetFactory {

    private final PlayerRepository playerRepository;

    public TribuoPlayerDatasetFactory(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public MutableDataset<Regressor> buildDatasetFromPlayers() {
        RegressionFactory factory = new RegressionFactory();

        MutableDataset<Regressor> dataset = new MutableDataset<>(
                new SimpleDataSourceProvenance("player-database-dataset", factory),
                factory
        );

        List<Player> players = playerRepository.findAll();

        for (Player player : players) {
            if (!isTrainable(player)) {
                continue;
            }

            dataset.add(toExample(player));
        }

        return dataset;
    }

    public int getTotalPlayerCount() {
        return playerRepository.findAll().size();
    }

    public int getTrainablePlayerCount() {
        return (int) playerRepository.findAll().stream()
                .filter(this::isTrainable)
                .count();
    }

    public int getExcludedPlayerCount() {
        return getTotalPlayerCount() - getTrainablePlayerCount();
    }

    public List<Player> getTrainablePlayers() {
        return playerRepository.findAll().stream()
                .filter(this::isTrainable)
                .toList();
    }

    public boolean isTrainable(Player player) {
        return player.getFormRating() != null
                && player.getAge() != null
                && player.getGoals() != null
                && player.getAssists() != null
                && player.getMinutesPlayed() != null
                && player.getShotsOnTarget() != null
                && player.getPassAccuracy() != null
                && player.getExpectedGoals() != null
                && player.getExpectedAssists() != null
                && player.getKeyPasses() != null
                && player.getProgressivePasses() != null
                && player.getDribblesCompleted() != null
                && player.getTacklesWon() != null
                && player.getInterceptions() != null
                && player.getBallRecoveries() != null
                && player.getMatchesMissed() != null
                && player.getRecentMatchLoad() != null
                && player.getInjuryStatus() != null;
    }

    private ArrayExample<Regressor> toExample(Player player) {
        ArrayExample<Regressor> ex =
                new ArrayExample<>(new Regressor("formRating", player.getFormRating()));

        ex.add(new org.tribuo.Feature("age", player.getAge()));
        ex.add(new org.tribuo.Feature("goals", player.getGoals()));
        ex.add(new org.tribuo.Feature("assists", player.getAssists()));
        ex.add(new org.tribuo.Feature("minutesPlayed", player.getMinutesPlayed()));
        ex.add(new org.tribuo.Feature("shotsOnTarget", player.getShotsOnTarget()));
        ex.add(new org.tribuo.Feature("passAccuracy", player.getPassAccuracy()));
        ex.add(new org.tribuo.Feature("expectedGoals", player.getExpectedGoals()));
        ex.add(new org.tribuo.Feature("expectedAssists", player.getExpectedAssists()));
        ex.add(new org.tribuo.Feature("keyPasses", player.getKeyPasses()));
        ex.add(new org.tribuo.Feature("progressivePasses", player.getProgressivePasses()));
        ex.add(new org.tribuo.Feature("dribblesCompleted", player.getDribblesCompleted()));
        ex.add(new org.tribuo.Feature("tacklesWon", player.getTacklesWon()));
        ex.add(new org.tribuo.Feature("interceptions", player.getInterceptions()));
        ex.add(new org.tribuo.Feature("ballRecoveries", player.getBallRecoveries()));
        ex.add(new org.tribuo.Feature("matchesMissed", player.getMatchesMissed()));
        ex.add(new org.tribuo.Feature("recentMatchLoad", player.getRecentMatchLoad()));
        ex.add(new org.tribuo.Feature("injuryStatus", Boolean.TRUE.equals(player.getInjuryStatus()) ? 1.0 : 0.0));

        return ex;
    }
}