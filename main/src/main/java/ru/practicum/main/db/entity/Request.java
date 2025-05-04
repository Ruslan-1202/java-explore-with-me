package ru.practicum.main.db.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.main.enumeration.RequestStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests", schema = "public")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    private Event event;
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;
    private LocalDateTime created;
    @Enumerated(EnumType.ORDINAL)
    private RequestStatus status;
}
