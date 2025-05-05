package ru.practicum.stats.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.explore.stats.dto.StatsCreateDTO;
import ru.practicum.explore.stats.dto.StatsRetDTO;

import java.util.List;


@Service
public class StatClient extends BaseClient {

    @Autowired
    public StatClient(@Value("${client.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public void addHit(StatsCreateDTO statsCreateDTO) {
        post("/hit", statsCreateDTO);
    }

    public List<StatsRetDTO> getStats(String start, String end, List<String> uris, boolean unique) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromPath("/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("uris", uris)
                .queryParam("unique", unique);

        try {
            ResponseEntity<List<StatsRetDTO>> response = rest.exchange(
                    uriComponentsBuilder.build().toString(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    }
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                return List.of();
            }
        } catch (Exception e) {
            return List.of();
        }
    }
}