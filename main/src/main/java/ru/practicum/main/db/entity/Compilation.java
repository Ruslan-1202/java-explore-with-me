package ru.practicum.main.db.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.main.dto.CompilationAndEventDTO;

@NamedNativeQueries(
        @NamedNativeQuery(
                name = "Compilation.findCompilationsAndEvents",
                resultSetMapping = "Compilation.CompilationAndEventDTO.Mapping",
                resultClass = CompilationAndEventDTO.class,
                query = """
                        select  c.id        as compilationId,
                                c.title     as title,
                                c.pinned    as pinned,
                                (select event_id from compilation_events where compilation_id = c.id limit 1) as eventIds
                            from compilations c
                            where c.id in (:compilationsIds);
                        """
        )
)
@SqlResultSetMappings(
        @SqlResultSetMapping(
                name = "Compilation.CompilationAndEventDTO.Mapping",
                classes = @ConstructorResult(targetClass = CompilationAndEventDTO.class,
                        columns = {
                                @ColumnResult(name = "compilationId", type = Long.class),
                                @ColumnResult(name = "title", type = String.class),
                                @ColumnResult(name = "pinned", type = Boolean.class),
                                @ColumnResult(name = "eventIds", type = Long[].class)
                        }
                )
        )
)
@Entity
@Table(name = "compilations", schema = "public")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean pinned;
    private String title;
}
