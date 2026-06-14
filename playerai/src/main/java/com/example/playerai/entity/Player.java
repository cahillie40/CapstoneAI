package com.example.playerai.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer age;
    private String position;
    private String team;

    private Integer matchesPlayed;
    private Integer goals;
    private Integer assists;
    private Integer minutesPlayed;
    private Integer yellowCards;
    private Integer redCards;
    private Integer shotsOnTarget;
    private Double passAccuracy;
    private Double formRating;
    private Boolean injuryStatus;
}