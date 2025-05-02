package ru.practicum.main.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.main.db.entity.Compilation;
import ru.practicum.main.dto.CompilationAndEventDTO;

import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query(
            nativeQuery = true,
            value = """
                    select event_id
                        from compilation_events
                        where compilation_id = :compilationId
                    """
    )
    List<Long> getCompilationEventIds(Long compilationId);

    @Query(
            nativeQuery = true,
            value = """
                    select *
                        from compilations
                        where (pinned = :pinned or :pinned is null)
                    limit :size offset :from
                    """
    )
    List<Compilation> getCompilations(Boolean pinned, Long from, Long size);

    @Query(nativeQuery = true)
    List<CompilationAndEventDTO> findCompilationsAndEvents(List<Long> compilationsIds);
}
