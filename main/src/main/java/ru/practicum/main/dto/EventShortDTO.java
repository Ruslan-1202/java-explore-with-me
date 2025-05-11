package ru.practicum.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDTO {
    private long id;
    private String eventDate;
    private String publishedOn;
    private String state; //"state": "PUBLISHED",
    private String title;
}
