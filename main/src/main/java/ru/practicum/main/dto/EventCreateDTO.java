package ru.practicum.main.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(of = {"title", "category"})
public class EventCreateDTO {
    @Size(min = 20, max = 2000)
    @NotNull
    @NotBlank
    private String annotation;
    @NotNull
    private Long category;
    @Size(min = 20, max = 7000)
    @NotNull
    @NotBlank
    private String description;
    @NotNull
    private String eventDate;
    @NotNull
    private LocationDTO location;
    private Boolean paid;
    @PositiveOrZero
    private Integer participantLimit;
    private Boolean requestModeration;
    @Size(min = 3, max = 120)
    @NotNull
    @NotBlank
    private String title;
}
