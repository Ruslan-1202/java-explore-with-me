package ru.practicum.explore.stats.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StatsCreateDTO implements Serializable {
    public String app;
    public String uri;
    public String ip;
    public LocalDateTime created;
}
