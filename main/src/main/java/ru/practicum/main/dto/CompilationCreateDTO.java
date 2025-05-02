package ru.practicum.main.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationCreateDTO {
    private List<Long> events;
    private Boolean pinned;
    @NotNull
    @Size(min = 1, max = 50)
    private String title;
}
