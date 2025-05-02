package ru.practicum.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDTO {
    private List<EventDTO>  events;
    private long id;
    private boolean pinned;
    private String title;
}
