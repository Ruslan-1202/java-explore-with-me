package ru.practicum.main.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.main.db.entity.Comment;

import java.util.List;

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
            value = """
                    update Comment
                        set likes = likes + :likes
                        where id = :commentId
                    """
    )
    void addLike(long commentId, int likes);

    @Query(
            value = """
                    update Comment
                        set dislikes = dislikes + :dislikes
                        where id = :commentId
                    """
    )
    void addDisLike(long commentId, int dislikes);
}
