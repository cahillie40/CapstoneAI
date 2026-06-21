package com.example.playerai.service;

import com.example.playerai.dto.ValidationSummaryDTO;
import com.example.playerai.entity.Prediction;
import com.example.playerai.repository.PredictionRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ValidationService {

    private final PredictionRepository predictionRepository;

    public ValidationService(PredictionRepository predictionRepository) {
        this.predictionRepository = predictionRepository;
    }

    public ValidationSummaryDTO getSummary() {
        List<Prediction> all = predictionRepository.findAllByOrderByCreatedAtDesc();

        long total  = all.size();
        long high   = all.stream().filter(p -> "HIGH".equals(p.getRiskLevel())).count();
        long medium = all.stream().filter(p -> "MEDIUM".equals(p.getRiskLevel())).count();
        long low    = all.stream().filter(p -> "LOW".equals(p.getRiskLevel())).count();

        double average = all.stream()
                .mapToDouble(Prediction::getPredictedFormRating)
                .average()
                .orElse(0.0);

        double highest = all.stream()
                .mapToDouble(Prediction::getPredictedFormRating)
                .max()
                .orElse(0.0);

        double lowest = all.stream()
                .mapToDouble(Prediction::getPredictedFormRating)
                .min()
                .orElse(0.0);

        average = Math.round(average * 10.0) / 10.0;
        highest = Math.round(highest * 10.0) / 10.0;
        lowest  = Math.round(lowest  * 10.0) / 10.0;

        return new ValidationSummaryDTO(total, high, medium, low, average, highest, lowest);
    }
}