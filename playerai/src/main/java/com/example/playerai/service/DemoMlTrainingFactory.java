package com.example.playerai.service;

import org.tribuo.MutableDataset;
import org.tribuo.impl.ArrayExample;
import org.tribuo.provenance.SimpleDataSourceProvenance;
import org.tribuo.regression.Regressor;
import org.tribuo.regression.RegressionFactory;

public final class DemoMlTrainingFactory {

    private DemoMlTrainingFactory() {
    }

    public static MutableDataset<Regressor> buildDemoDataset() {
        RegressionFactory factory = new RegressionFactory();

        MutableDataset<Regressor> dataset = new MutableDataset<>(
                new SimpleDataSourceProvenance("demo-ml-dataset", factory),
                factory
        );

        dataset.add(example(82, 24, 18, 6, 2200, 40, 88, 14.5, 7.2, 42, 95, 35, 8, 10, 65, 1, 4, 0));
        dataset.add(example(74, 29, 10, 9, 2100, 28, 84, 9.1, 8.4, 54, 80, 20, 12, 18, 72, 0, 5, 0));
        dataset.add(example(61, 33, 6, 15, 1890, 18, 89, 7.0, 10.5, 60, 45, 14, 8, 11, 58, 2, 0, 0));
        dataset.add(example(48, 31, 4, 3, 1200, 10, 79, 3.8, 2.4, 18, 22, 8, 4, 7, 34, 6, 8, 1));
        dataset.add(example(91, 26, 25, 8, 2600, 52, 90, 18.8, 9.1, 48, 110, 40, 10, 15, 85, 0, 3, 0));
        dataset.add(example(55, 30, 7, 4, 1500, 14, 82, 4.1, 3.5, 16, 30, 10, 7, 8, 40, 4, 7, 1));

        return dataset;
    }

    private static ArrayExample<Regressor> example(
            double score,
            int age,
            int goals,
            int assists,
            int minutesPlayed,
            int shotsOnTarget,
            double passAccuracy,
            double expectedGoals,
            double expectedAssists,
            int keyPasses,
            int progressivePasses,
            int dribblesCompleted,
            int tacklesWon,
            int interceptions,
            int ballRecoveries,
            int matchesMissed,
            int recentMatchLoad,
            int injuryStatus
    ) {
        ArrayExample<Regressor> ex = new ArrayExample<>(new Regressor("score", score));
        ex.add(new org.tribuo.Feature("age", age));
        ex.add(new org.tribuo.Feature("goals", goals));
        ex.add(new org.tribuo.Feature("assists", assists));
        ex.add(new org.tribuo.Feature("minutesPlayed", minutesPlayed));
        ex.add(new org.tribuo.Feature("shotsOnTarget", shotsOnTarget));
        ex.add(new org.tribuo.Feature("passAccuracy", passAccuracy));
        ex.add(new org.tribuo.Feature("expectedGoals", expectedGoals));
        ex.add(new org.tribuo.Feature("expectedAssists", expectedAssists));
        ex.add(new org.tribuo.Feature("keyPasses", keyPasses));
        ex.add(new org.tribuo.Feature("progressivePasses", progressivePasses));
        ex.add(new org.tribuo.Feature("dribblesCompleted", dribblesCompleted));
        ex.add(new org.tribuo.Feature("tacklesWon", tacklesWon));
        ex.add(new org.tribuo.Feature("interceptions", interceptions));
        ex.add(new org.tribuo.Feature("ballRecoveries", ballRecoveries));
        ex.add(new org.tribuo.Feature("matchesMissed", matchesMissed));
        ex.add(new org.tribuo.Feature("recentMatchLoad", recentMatchLoad));
        ex.add(new org.tribuo.Feature("injuryStatus", injuryStatus));
        return ex;
    }
}