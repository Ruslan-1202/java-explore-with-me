package ru.practicum.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
    public List<CommentDTO> getCommentByEventId(long eventId, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return commentRepository.findAllByEventId(eventId, page).stream()
                .map(commentMapper::toCommentDTO)
                .toList();
    }

    @Transactional
    public void deleteCommentByUser(long userId, long commentId) {
        Comment comment = getUserComment(userId, commentId);
        deleteComment(comment);
    }

    private void deleteComment(Comment comment) {
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

        Boolean beforeLiked = commentLikeService.setLike(liked, user, comment);

        CommentDTO commentDTO = commentMapper.toCommentDTO(comment);

        calculateLikes(beforeLiked, liked, commentDTO);

        return commentDTO;
    }

    private void calculateLikes(Boolean beforeLike, boolean afterLike, CommentDTO commentDTO) {
        if (beforeLike != null && (beforeLike ^ afterLike)) {
            return;
        }

        int likes;
        int dislikes;

        if (afterLike) {
            likes = 1;
            dislikes = beforeLike == null ? 0 : -1;
        } else {
            likes = beforeLike == null ? 0 : -1;
            dislikes = 1;
        }

        commentRepository.addLike(commentDTO.getId(), likes);
        commentRepository.addDisLike(commentDTO.getId(), dislikes);
        commentDTO.setLikes(commentDTO.getLikes() + likes);
        commentDTO.setDislikes(commentDTO.getDislikes() + dislikes);
    }

    @Transactional
    public CommentDTO unLikeComment(long userId, long commentId) {
        Comment comment = getCommentById(commentId);
        User user = userService.getUserById(userId);
        CommentDTO commentDTO = commentMapper.toCommentDTO(comment);

        Boolean beforeLike = commentLikeService.deleteLike(user, comment);
        if (beforeLike != null) {
            if (beforeLike) {
                commentRepository.addLike(comment.getId(), -1);
                commentDTO.setLikes(commentDTO.getLikes() - 1);
            } else {
                commentRepository.addDisLike(comment.getId(), -1);
                commentDTO.setDislikes(commentDTO.getDislikes() - 1);
            }
        }
        return commentDTO;
    }
}
