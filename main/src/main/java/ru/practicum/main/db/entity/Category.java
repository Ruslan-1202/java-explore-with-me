package ru.practicum.main.db.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories", schema = "public")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}
