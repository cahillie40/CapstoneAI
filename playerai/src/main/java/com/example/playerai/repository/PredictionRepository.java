package com.example.playerai.repository;

import com.example.playerai.entity.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    List<Prediction> findByPlayerIdOrderByCreatedAtDesc(Long playerId);
    List<Prediction> findAllByOrderByCreatedAtDesc();
}