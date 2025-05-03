package ru.practicum.main.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.main.db.entity.Request;
import ru.practicum.main.enumeration.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Optional<Request> findByUserIdAndEventId (long userId, long eventId);

    List<Request> findByEventId (long eventId);

    Long countByEventId (long eventId);

    List<Request> findByUserId(long userId);

    @Modifying
    @Query(
            value = """
                    update Request
                        set status=:requestStatus
                        where id in (:idsToConfirm)
                    """
    )
    void setStatus(List<Long> idsToConfirm, RequestStatus requestStatus);
}
