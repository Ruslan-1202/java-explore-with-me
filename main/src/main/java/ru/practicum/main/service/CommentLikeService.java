package ru.practicum.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.db.CommentLikeRepository;
import ru.practicum.main.db.CommentRepository;
import ru.practicum.main.db.entity.Comment;
import ru.practicum.main.db.entity.CommentLike;
import ru.practicum.main.db.entity.User;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void setLike(boolean liked, User user, Comment comment) {
        CommentLike commentLike = commentLikeRepository.findByCommentIdAndUserId(user.getId(), comment.getId())
                .orElse(null);

        Boolean beforeLiked = null;
        if (commentLike == null) {
            commentLike = new CommentLike(null, comment, user, liked);
        } else {
            beforeLiked = commentLike.getLiked();
            commentLike.setLiked(liked);
        }

        calculateLikes(beforeLiked, liked, comment);

        commentLikeRepository.save(commentLike);
    }

    private void calculateLikes(Boolean beforeLike, boolean afterLike, Comment comment) {
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

        commentRepository.addLike(comment.getId(), likes);
        commentRepository.addDisLike(comment.getId(), dislikes);
    }

    public void deleteLike(User user, Comment comment) {
        CommentLike commentLike = commentLikeRepository.findByCommentIdAndUserId(user.getId(), comment.getId())
                .orElse(null);
        if (commentLike != null) {
        }
    }
}
