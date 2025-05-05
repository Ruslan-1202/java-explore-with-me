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
                        select  c.compilation_id    as compilationId,
                                c.event_id          as eventId
                            from compilation_events c
                            where c.compilation_id in (:compilationsIds);
                        """
        )
)
@SqlResultSetMappings(
        @SqlResultSetMapping(
                name = "Compilation.CompilationAndEventDTO.Mapping",
                classes = @ConstructorResult(targetClass = CompilationAndEventDTO.class,
                        columns = {
                                @ColumnResult(name = "compilationId", type = Long.class),
                                @ColumnResult(name = "eventId", type = Long.class)
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
