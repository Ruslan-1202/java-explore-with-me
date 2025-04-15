package ru.practicum.explore.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsRetDTO {
    public String app;
    public String uri;
    public Long hits;
}
