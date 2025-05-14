package ru.practicum.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.db.CommentLikeRepository;
import ru.practicum.main.db.entity.Comment;
import ru.practicum.main.db.entity.CommentLike;
import ru.practicum.main.db.entity.User;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;

    @Transactional
    public Boolean setLike(boolean liked, User user, Comment comment) {
        CommentLike commentLike = commentLikeRepository.findByCommentIdAndUserId(user.getId(), comment.getId())
                .orElse(null);

        Boolean beforeLiked = null;
        if (commentLike == null) {
            commentLike = new CommentLike(null, comment, user, liked);
        } else {
            beforeLiked = commentLike.getLiked();
            commentLike.setLiked(liked);
        }

        commentLikeRepository.save(commentLike);

        return beforeLiked;
    }

    public Boolean deleteLike(User user, Comment comment) {

        Boolean beforeLiked;

        CommentLike commentLike = commentLikeRepository.findByCommentIdAndUserId(user.getId(), comment.getId())
                .orElse(null);
        if (commentLike == null) {
            beforeLiked = null;
        } else {
            beforeLiked = commentLike.getLiked();
            commentLikeRepository.delete(commentLike);
        }

        return beforeLiked;
    }
}
