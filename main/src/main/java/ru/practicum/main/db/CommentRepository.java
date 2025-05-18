package ru.practicum.main.db;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.main.db.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"event", "user"})
    Page<Comment> findAllByEventId(long eventId, Pageable pageable);

    @Modifying
    @Query(
            value = """
                    update Comment
                        set likes = likes + :likes
                        where id = :commentId
                    """
    )
    void addLike(long commentId, int likes);

    @Modifying
    @Query(
            nativeQuery = true,
            value = """
                    update comments
                        set dislikes = dislikes + :dislikes
                        where id = :commentId
                    """
    )
    void addDisLike(long commentId, int dislikes);
}
