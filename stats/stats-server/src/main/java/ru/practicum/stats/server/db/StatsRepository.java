package ru.practicum.stats.server.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.explore.stats.dto.StatsRetDTO;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<StatsEntity, Long> {
    @Query(nativeQuery = true)
    List<StatsRetDTO> findStatsWithHits(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris,
            boolean unique);
}
