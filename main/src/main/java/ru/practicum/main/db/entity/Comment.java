package ru.practicum.main.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments", schema = "public")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    private Event event;
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;
    private String text;
    private Integer likes;
    private Integer dislikes;
    private LocalDateTime created;
    private LocalDateTime modified;
}
