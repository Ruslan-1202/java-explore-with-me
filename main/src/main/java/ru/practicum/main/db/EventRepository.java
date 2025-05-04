package ru.practicum.main.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.main.db.entity.Event;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    @Query(
            nativeQuery = true,
            value = """
                    select *
                        from events
                        where user_id = :userId
                    limit :size offset :from
                    """
    )
    List<Event> getEventsByUserId(Long userId, Long from, Long size);

    Optional<Event> getEventByIdAndUserId(Long id, Long userId);
}
