package ru.practicum.stats.client.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.stats.dto.StatsCreateDTO;
import ru.practicum.stats.client.service.StatsClientService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsClientController {
    private final StatsClientService statsClientService;

    @PostMapping("/hit")
    public void createStats(@RequestBody StatsCreateDTO statsCreateDTO) {
        log.info("StatsClientController::createStats\n{}", statsCreateDTO);
        statsClientService.createStats(statsCreateDTO);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStatsAll(
            @RequestParam(name = "start") String start,
            @RequestParam(name = "end") String end,
            @RequestParam(name = "uris", required = false, defaultValue = "") String uris,
            @RequestParam(name = "unique", required = false, defaultValue = "false") Boolean unique
    ) {
        log.info("StatsClientController::getStatsAll");
        return statsClientService.getStatsAll(start, end, uris, unique);
    }
}
