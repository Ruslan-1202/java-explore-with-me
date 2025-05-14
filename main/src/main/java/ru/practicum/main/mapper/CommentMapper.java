package ru.practicum.main.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.main.db.entity.Comment;
import ru.practicum.main.db.entity.Event;
import ru.practicum.main.db.entity.User;
import ru.practicum.main.dto.CommentCreateDTO;
import ru.practicum.main.dto.CommentDTO;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentMapper {
    private final EventMapper eventMapper;
    private final UserMapper userMapper;

    public Comment toComment(CommentCreateDTO commentCreateDTO, User user, Event event) {
        return new Comment(
                null,
                event,
                user,
                commentCreateDTO.getText(),
                0,
                0,
                LocalDateTime.now(),
                null
        );
    }

    public CommentDTO toCommentDTO(Comment comment) {
        return new CommentDTO(
                comment.getId(),
                eventMapper.toEventShortDTO(comment.getEvent()),
                userMapper.userToUserShortDTO(comment.getUser()),
                comment.getText(),
                comment.getLikes(),
                comment.getDislikes(),
                comment.getCreated(),
                comment.getModified()
        );
    }
}
