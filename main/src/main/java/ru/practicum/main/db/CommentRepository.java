package ru.practicum.main.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.main.db.entity.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(
            nativeQuery = true,
            value = """
                    select *
                        from comments
                        where event_id = :eventId
                    limit :size offset :from
                    """
    )
    List<Comment> getComments(long eventId, Long from, Long size);

    @Query(
            nativeQuery = true,
            value = """
                    select is_liked
                        from comment_likes
                        where comment_id = :commentId and
                              user_id    = :userId
                    """
    )
    Optional<Boolean> getLikes(long userId, long commentId);
}
