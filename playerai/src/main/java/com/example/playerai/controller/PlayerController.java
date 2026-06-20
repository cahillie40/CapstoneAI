package com.example.playerai.controller;

import com.example.playerai.entity.Player;
import com.example.playerai.repository.PlayerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/players")
@CrossOrigin(origins = "*")
public class PlayerController {

    private final PlayerRepository playerRepository;

    public PlayerController(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @GetMapping
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Player>> searchPlayers(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String position,
            @RequestParam(defaultValue = "") String team,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Player> result = playerRepository.searchPlayers(name, position, team, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Long id) {
        return playerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Player createPlayer(@RequestBody Player player) {
        return playerRepository.save(player);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable Long id,
                                               @RequestBody Player updated) {
        return playerRepository.findById(id).map(player -> {
            player.setName(updated.getName());
            player.setAge(updated.getAge());
            player.setPosition(updated.getPosition());
            player.setTeam(updated.getTeam());
            player.setMatchesPlayed(updated.getMatchesPlayed());
            player.setGoals(updated.getGoals());
            player.setAssists(updated.getAssists());
            player.setMinutesPlayed(updated.getMinutesPlayed());
            player.setYellowCards(updated.getYellowCards());
            player.setRedCards(updated.getRedCards());
            player.setShotsOnTarget(updated.getShotsOnTarget());
            player.setPassAccuracy(updated.getPassAccuracy());
            player.setFormRating(updated.getFormRating());
            player.setInjuryStatus(updated.getInjuryStatus());
            return ResponseEntity.ok(playerRepository.save(player));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        if (!playerRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        playerRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}