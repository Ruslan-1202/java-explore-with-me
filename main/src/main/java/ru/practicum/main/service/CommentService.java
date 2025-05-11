package ru.practicum.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.db.CommentRepository;
import ru.practicum.main.db.entity.Comment;
import ru.practicum.main.db.entity.User;
import ru.practicum.main.dto.CommentCreateDTO;
import ru.practicum.main.dto.CommentDTO;
import ru.practicum.main.dto.CommentPatchDTO;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.CommentMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final EventService eventService;
    private final UserService userService;
    private final NamedParameterJdbcOperations jdbc;

    private CommentDTO editComment(CommentPatchDTO commentPatchDTO, Comment comment) {
        if (commentPatchDTO.getText() != null) {
            comment.setText(commentPatchDTO.getText());
        } else {
            throw new BadRequestException("Nothing to edit");
        }

        comment.setModified(LocalDateTime.now());

        return commentMapper.toCommentDTO(commentRepository.save(comment));
    }

    private Comment getUserComment(long userId, long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment id=" + commentId + " not found"));
        User user = userService.getUserById(userId);

        if (!comment.getUser().equals(user)) {
            throw new ConflictException("Comment user=" + userId + " not allowed to edit comment=" + commentId);
        }
        return comment;
    }

    public Comment getCommentById(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment id= " + commentId + " not found"));
    }

    @Transactional
    public CommentDTO createComment(CommentCreateDTO commentCreateDTO, long userId, long eventId) {
        Comment comment = commentMapper.toComment(commentCreateDTO, userService.getUserById(userId), eventService.getEventById(eventId));
        return commentMapper.toCommentDTO(commentRepository.save(comment));
    }

    @Transactional
    public CommentDTO editCommentByUser(CommentPatchDTO commentPatchDTO, long userId, long commentId) {
        Comment comment = getUserComment(userId, commentId);
        return editComment(commentPatchDTO, comment);
    }

    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentByEventId(long eventId, Long from, Long size) {
        return commentRepository.getComments(eventId, from, size).stream()
                .map(commentMapper::toCommentDTO)
                .toList();
    }

    @Transactional
    public void deleteCommentByUser(long userId, long commentId) {
        Comment comment = getUserComment(userId, commentId);
        deleteComment(comment);
    }

    private void deleteComment(Comment comment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("commentId", comment.getId());

        jdbc.update("DELETE FROM comment_likes WHERE comment_id = :commentId", params);
        commentRepository.delete(comment);
    }

    @Transactional
    public void deleteCommentByAdmin(long commentId) {
        Comment comment = getCommentById(commentId);
        deleteComment(comment);
    }

    @Transactional
    public CommentDTO likeComment(long userId, long commentId, boolean liked) {
        Comment comment = getCommentById(commentId);

        if (comment.getUser().getId() == userId) {
            throw new ConflictException("Don't like your comment");
        }

        Boolean likedExists = commentRepository.getLikes(userId, commentId)
                .orElse(null);

        if (likedExists == null) {
            insertLike(userId, commentId, liked, comment);
        } else {
            updateLike(userId, commentId, liked, likedExists, comment);
        }

        return commentMapper.toCommentDTO(commentRepository.save(comment));
    }

    private void updateLike(long userId, long commentId, boolean liked, boolean likedExists, Comment comment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("commentId", commentId);
        params.addValue("userId", userId);


        String updateOrDelete;
        //повторное действие снимает предыдущее, потому запись удаляем, количество уменьшаем
        if (likedExists ^ liked) {
            updateOrDelete = "DELETE FROM comment_likes";
            deleteLike(comment, liked);
        } else {
            updateOrDelete = "UPDATE comment_likes SET is_liked = :liked";
            params.addValue("liked", liked);

            setLike(comment, liked);
            deleteLike(comment, !liked);
        }

        String query = updateOrDelete + " WHERE comment_id=:commentId and user_id=:userId";
        jdbc.update(query, params);
    }

    private void insertLike(long userId, long commentId, boolean liked, Comment comment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("commentId", commentId);
        params.addValue("userId", userId);
        params.addValue("liked", liked);
        setLike(comment, liked);
        jdbc.update("INSERT INTO comment_likes (comment_id, user_id, is_liked) VALUES(:commentId, :userId, :liked)", params);
    }

    private void setLike(Comment comment, boolean liked) {
        if (liked) {
            comment.setLikes(comment.getLikes() + 1);
        } else {
            comment.setDislikes(comment.getDislikes() + 1);
        }
    }

    private void deleteLike(Comment comment, boolean liked) {
        if (liked) {
            comment.setLikes(comment.getLikes() - 1);
        } else {
            comment.setDislikes(comment.getDislikes() - 1);
        }
    }
}
