package com.example.playerai.service;

import com.example.playerai.dto.PredictionRequest;
import com.example.playerai.dto.PredictionResponse;
import com.example.playerai.dto.PredictionHistoryDTO;
import com.example.playerai.entity.Prediction;
import com.example.playerai.entity.Player;
import com.example.playerai.repository.PredictionRepository;
import com.example.playerai.repository.PlayerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PredictionService {

    private final PredictionRepository predictionRepository;
    private final PlayerRepository playerRepository;
    private final ObjectMapper objectMapper;

    public PredictionService(PredictionRepository predictionRepository,
                             PlayerRepository playerRepository,
                             ObjectMapper objectMapper) {
        this.predictionRepository = predictionRepository;
        this.playerRepository = playerRepository;
        this.objectMapper = objectMapper;
    }

    public PredictionResponse predict(PredictionRequest request) {
        double score = 50.0;

        if (request.getGoals() != null)          score += request.getGoals() * 2.5;
        if (request.getAssists() != null)         score += request.getAssists() * 2.0;
        if (request.getShotsOnTarget() != null)   score += request.getShotsOnTarget() * 0.8;
        if (request.getPassAccuracy() != null)    score += request.getPassAccuracy() * 0.3;
        if (request.getMinutesPlayed() != null)   score += request.getMinutesPlayed() * 0.005;
        if (request.getYellowCards() != null)     score -= request.getYellowCards() * 0.5;
        if (request.getRedCards() != null)        score -= request.getRedCards() * 2.0;

        if (Boolean.TRUE.equals(request.getInjuryStatus())) score -= 10.0;

        if (request.getPosition() != null) {
            switch (request.getPosition().toLowerCase()) {
                case "goalkeeper" -> score += 2.0;
                case "defender"   -> score += 1.5;
                case "midfielder" -> score += 1.0;
                case "winger"     -> score += 1.5;
                case "forward"    -> score += 2.0;
                case "striker"    -> score += 2.0;
            }
        }

        if (request.getAge() != null) {
            if (request.getAge() >= 24 && request.getAge() <= 29) score += 3.0;
            else if (request.getAge() >= 30)                      score -= 1.5;
        }

        score = Math.max(0, Math.min(100, score));
        double rounded = Math.round(score * 10.0) / 10.0;

        String riskLevel = rounded >= 75 ? "LOW" : rounded >= 50 ? "MEDIUM" : "HIGH";
        String summary = buildSummary(request, rounded, riskLevel);

        return new PredictionResponse(rounded, riskLevel, summary);
    }

    public PredictionHistoryDTO savePrediction(Long playerId, PredictionRequest request,
                                               PredictionResponse response) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        String inputDataJson;
        try {
            inputDataJson = objectMapper.writeValueAsString(request);
        } catch (Exception e) {
            inputDataJson = "";
        }

        Prediction prediction = new Prediction(
                player,
                response.getPredictedFormRating(),
                response.getRiskLevel(),
                response.getSummary(),
                inputDataJson,
                LocalDateTime.now()
        );

        Prediction saved = predictionRepository.save(prediction);
        return toDTO(saved);
    }

    public List<PredictionHistoryDTO> getHistoryForPlayer(Long playerId) {
        return predictionRepository.findByPlayerIdOrderByCreatedAtDesc(playerId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<PredictionHistoryDTO> getAllHistory() {
        return predictionRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Page<PredictionHistoryDTO> getAllHistoryPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return predictionRepository.findAll(pageable).map(this::toDTO);
    }

    private PredictionHistoryDTO toDTO(Prediction p) {
        return new PredictionHistoryDTO(
                p.getId(),
                p.getPlayer().getName(),
                p.getPlayer().getId(),
                p.getPredictedFormRating(),
                p.getRiskLevel(),
                p.getSummary(),
                p.getCreatedAt()
        );
    }

    private String buildSummary(PredictionRequest request, double score, String riskLevel) {
        StringBuilder sb = new StringBuilder();
        sb.append("Predicted form rating: ").append(score).append(". ");
        sb.append("Risk level: ").append(riskLevel).append(". ");
        if (Boolean.TRUE.equals(request.getInjuryStatus()))               sb.append("Player is currently injured which impacts score. ");
        if (request.getGoals() != null && request.getGoals() >= 10)       sb.append("Strong goal contribution. ");
        if (request.getAssists() != null && request.getAssists() >= 8)    sb.append("Strong assist contribution. ");
        if (request.getPassAccuracy() != null && request.getPassAccuracy() >= 85) sb.append("Excellent pass accuracy. ");
        if (request.getRedCards() != null && request.getRedCards() > 0)   sb.append("Red cards negatively impact score. ");
        return sb.toString().trim();
    }
}