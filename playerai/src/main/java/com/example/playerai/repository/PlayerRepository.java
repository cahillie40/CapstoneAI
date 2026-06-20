package com.example.playerai.repository;

import com.example.playerai.entity.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    @Query("SELECT p FROM Player p WHERE " +
            "(:name = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:position = '' OR LOWER(p.position) LIKE LOWER(CONCAT('%', :position, '%'))) AND " +
            "(:team = '' OR LOWER(p.team) LIKE LOWER(CONCAT('%', :team, '%')))")
    Page<Player> searchPlayers(@Param("name") String name,
                               @Param("position") String position,
                               @Param("team") String team,
                               Pageable pageable);
}