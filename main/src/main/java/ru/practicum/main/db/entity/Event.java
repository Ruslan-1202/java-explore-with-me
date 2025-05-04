package ru.practicum.main.db.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.main.enumeration.EventState;

import java.time.LocalDateTime;

@Entity
@Table(name = "events", schema = "public")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Setter @Getter @ToString(of = {"id", "title"})
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long            id;
    @Column(name = "annotation", nullable = false)
    private String          annotation;
    @ManyToOne(fetch = FetchType.EAGER)
    private Category        category;
    @Column(name = "description", nullable = false)
    private String          description;
    @Column(name = "event_date")
    private LocalDateTime   eventDate;
    @Column(name = "lat", nullable = false)
    private Double          lat;
    @Column(name = "lon", nullable = false)
    private Double          lon;
    @Column(name = "paid")
    private Boolean         paid;
    @Column(name = "participant_limit")
    private Integer         participantLimit;
    @Column(name = "request_moderation")
    private Boolean         requestModeration;
    @Column(name = "title", nullable = false)
    private String          title;
    @ManyToOne(fetch = FetchType.EAGER)
    private User            user;
    @Column(name = "created", nullable = false)
    private LocalDateTime   created;
    @Enumerated(EnumType.ORDINAL)
    private EventState state;
    @Column(name = "published")
    private LocalDateTime   published;
    @Column(name = "views")
    private Integer         views;
    @Column(name = "confirmed_requests")
    private Integer         confirmedRequests;
}
