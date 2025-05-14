package ru.practicum.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private Long id;
    private EventShortDTO eventShortDTO;
    private UserShortDTO userShortDTO;
    private String text;
    private Integer likes;
    private Integer dislikes;
    private LocalDateTime created;
    private LocalDateTime modified;
}
