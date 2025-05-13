package ru.practicum.main.db.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comment_likes", schema = "public")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    private Comment comment;
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;
    private Boolean liked;
}
