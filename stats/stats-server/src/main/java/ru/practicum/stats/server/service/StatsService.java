package ru.practicum.stats.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.stats.dto.StatsCreateDTO;
import ru.practicum.explore.stats.dto.StatsRetDTO;
import ru.practicum.stats.server.db.StatsRepository;
import ru.practicum.stats.server.mapper.StatsMapper;
import ru.practicum.stats.server.util.Utils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsRepository statsRepository;
    private final StatsMapper statsMapper;

    @Transactional
    public void createStats(StatsCreateDTO statsCreateDTO) {
        if (statsCreateDTO.created == null) {
            statsCreateDTO.created = LocalDateTime.now();
        }
        statsRepository.save(statsMapper.toStatsEntity(statsCreateDTO));
    }

    @Transactional(readOnly = true)
    public List<StatsRetDTO> getStatsAll(String start, String end, String[] uris, Boolean unique) {
        LocalDateTime dateTimeStart;
        LocalDateTime dateTimeEnd;

        if (start == null) {
            dateTimeStart = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
        } else {
            dateTimeStart = Utils.decodeDateTime(start);
        }
        if (end == null) {
            dateTimeEnd = LocalDateTime.of(2100, 1, 1, 0, 0, 0);
        } else {
            dateTimeEnd = Utils.decodeDateTime(end);
        }

        List<String> uriList = Arrays.stream(uris).toList();

        return statsRepository.findStatsWithHits(dateTimeStart, dateTimeEnd, uriList, unique);
    }
}
