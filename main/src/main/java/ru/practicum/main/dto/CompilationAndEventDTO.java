package ru.practicum.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationAndEventDTO {
    private Long compilationId;
    private String title;
    private Boolean pinned;

    private Long[] eventIds;
}
