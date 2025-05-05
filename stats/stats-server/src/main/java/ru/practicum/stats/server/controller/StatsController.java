package ru.practicum.stats.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.stats.dto.StatsCreateDTO;
import ru.practicum.explore.stats.dto.StatsRetDTO;
import ru.practicum.stats.server.service.StatsService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void createStats(@RequestBody StatsCreateDTO statsCreateDTO) {
        log.info("StatsController::createStats\n{}", statsCreateDTO);
        statsService.createStats(statsCreateDTO);
    }

    @GetMapping("/stats")
    public List<StatsRetDTO> getStatsAll(
            @RequestParam(name = "start", required = false) String start,
            @RequestParam(name = "end", required = false) String end,
            @RequestParam(name = "uris", required = false, defaultValue = "") String[] uris,
            @RequestParam(name = "unique", required = false, defaultValue = "false") Boolean unique
    ) {
        log.info("StatsController::getStatsAll");
        return statsService.getStatsAll(start, end, uris, unique);
    }
}
