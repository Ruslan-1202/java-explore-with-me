package ru.practicum.stats.client.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.explore.stats.dto.StatsCreateDTO;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class StatsClientService {
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String STATS_SERVER_URL = "http://localhost:9090";

    public void createStats(StatsCreateDTO statsCreateDTO) {
        restTemplate.postForEntity(
                URI.create(STATS_SERVER_URL).resolve("/hit"),
                new HttpEntity<>(statsCreateDTO, jsonHeaders()),
                Void.class
        );
    }

    public ResponseEntity<Object> getStatsAll(String start, String end, String uris, Boolean unique) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start);
        parameters.put("end", end);
        parameters.put("uris", uris);
        parameters.put("unique", unique);

        String path = STATS_SERVER_URL + String.format("/stats?start=%s&end=%s&uris=%s&unique=%s", start, end, uris, unique);
        ResponseEntity<Object> test = restTemplate.exchange(
                path,
                HttpMethod.GET,
                new HttpEntity<>(jsonHeaders()),
                Object.class,
                parameters
        );

        return ResponseEntity.ok(test.getBody());
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
