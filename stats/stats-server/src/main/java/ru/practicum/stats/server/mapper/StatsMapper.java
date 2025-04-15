package ru.practicum.stats.server.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.explore.stats.dto.StatsCreateDTO;
import ru.practicum.explore.stats.dto.StatsRetDTO;
import ru.practicum.stats.server.db.StatsEntity;

@Service
public class StatsMapper {


    public StatsRetDTO toStatsRetDTO(StatsEntity statsEntity, long hits) {
        return StatsRetDTO.builder()
                .app(statsEntity.getApp())
                .uri(statsEntity.getUri())
                .hits(hits)
                .build();
    }

    public StatsEntity toStatsEntity(StatsCreateDTO statsCreateDTO) {
        return new StatsEntity(
                null,
                statsCreateDTO.app,
                statsCreateDTO.uri,
                statsCreateDTO.ip,
                statsCreateDTO.created
                //  parseDateTime(statsCreateDTO.created)
        );
    }
}
