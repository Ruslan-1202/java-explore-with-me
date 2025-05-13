package ru.practicum.main.service;

import lombok.RequiredArgsConstructor;
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
    private final CommentLikeService commentLikeService;
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
        Comment comment = getCommentById(commentId);
        User user = userService.getUserById(userId);

        if (!comment.getUser().equals(user)) {
            throw new ConflictException("Comment user=" + userId + " not allowed to edit comment=" + commentId);
        }
        return comment;
    }

    private Comment getCommentById(long commentId) {
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
//        MapSqlParameterSource params = new MapSqlParameterSource();
//        params.addValue("commentId", comment.getId());
//
//        jdbc.update("DELETE FROM comment_likes WHERE comment_id = :commentId", params);
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

        User user = userService.getUserById(userId);

        commentLikeService.setLike(liked, user, comment);

        return commentMapper.toCommentDTO(getCommentById(commentId));
    }

    @Transactional
    public CommentDTO unLikeComment(long userId, long commentId) {
        Comment comment = getCommentById(commentId);
        User user = userService.getUserById(userId);

        commentLikeService.deleteLike(user, comment);
        return new CommentDTO();
    }
}
