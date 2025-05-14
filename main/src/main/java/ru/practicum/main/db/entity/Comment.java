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
    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @Column(length = 512)
    private String text;
    private int likes = 0;
    private int dislikes = 0;
    private LocalDateTime created;
    private LocalDateTime modified;
}
