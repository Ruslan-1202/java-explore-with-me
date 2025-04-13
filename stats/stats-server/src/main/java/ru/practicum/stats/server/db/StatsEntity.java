package ru.practicum.stats.server.db;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.explore.stats.dto.StatsRetDTO;

import java.time.LocalDateTime;

@NamedNativeQuery(
        name = "StatsEntity.findStatsWithHits",
        resultSetMapping = "StatsRetDTO.Mapping",
        resultClass = StatsRetDTO.class,
        query = """
                        select
                           app,
                           uri,
                           (case :unique
                                when false then count(id)
                                else count(distinct ip) 
                            end) as hits
                        from stats
                    where created between :start and :end
                      and (uri in (:uris) or :uris is null)
                    group by app, uri
                    order by hits desc;
                """
)
@SqlResultSetMapping(
        name = "StatsRetDTO.Mapping",
        classes = @ConstructorResult(targetClass = StatsRetDTO.class,
                columns = {
                        @ColumnResult(name = "app"),
                        @ColumnResult(name = "uri"),
                        @ColumnResult(name = "hits")
                }
        )
)
@Entity
@Table(name = "stats", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // — уникальный идентификатор

    @Column(name = "app", nullable = false)
    private String app;

    @Column(name = "uri", nullable = false)
    private String uri;

    @Column(name = "ip", nullable = false)
    private String ip;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}
