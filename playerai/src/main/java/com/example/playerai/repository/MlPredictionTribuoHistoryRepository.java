package com.example.playerai.repository;

import com.example.playerai.entity.MlPredictionTribuoHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MlPredictionTribuoHistoryRepository extends JpaRepository<MlPredictionTribuoHistory, Long> {
    List<MlPredictionTribuoHistory> findAllByOrderByPredictedAtDesc();
}