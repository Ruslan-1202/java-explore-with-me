package ru.practicum.main.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.CommentCreateDTO;
import ru.practicum.main.dto.CommentDTO;
import ru.practicum.main.dto.CommentPatchDTO;
import ru.practicum.main.service.CommentService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping(value = {"/users", ""})
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{userId}/comments/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDTO createComment(@Valid @RequestBody CommentCreateDTO commentCreateDTO,
                                    @PathVariable long userId,
                                    @PathVariable long eventId) {
        log.debug("createComment: commentCreateDTO=[{}], userId={}, eventId={}", commentCreateDTO, userId, eventId);
        return commentService.createComment(commentCreateDTO, userId, eventId);
    }

    @PatchMapping("/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDTO editCommentByUser(@Valid @RequestBody CommentPatchDTO commentPatchDTO,
                                        @PathVariable long userId,
                                        @PathVariable long commentId) {
        log.debug("editCommentByUser: commentPatchDTO=[{}], userId={}, commentId={}", commentPatchDTO, userId, commentId);
        return commentService.editCommentByUser(commentPatchDTO, userId, commentId);
    }

    @GetMapping("/comments/{eventId}")
    public List<CommentDTO> getCommentByEventId(@PathVariable long eventId,
                                                @RequestParam(required = false, defaultValue = "0") int from,
                                                @RequestParam(required = false, defaultValue = "10") int size) {
        log.debug("getCommentByEventId: eventId={}, from={}, size={}", eventId, from, size);
        return commentService.getCommentByEventId(eventId, from, size);
    }

    @DeleteMapping("/{userId}/comments/{commentId}")
    public void deleteCommentByUser(@PathVariable long userId,
                                    @PathVariable long commentId) {
        log.debug("deleteCommentByUser: userId={}, commentId={}", userId, commentId);
        commentService.deleteCommentByUser(userId, commentId);
    }

    @DeleteMapping("/admin/comments/{commentId}")
    public void deleteCommentByAdmin(@PathVariable long commentId) {
        log.debug("deleteCommentByAdmin: commentId={}", commentId);
        commentService.deleteCommentByAdmin(commentId);
    }

    @PatchMapping("/{userId}/comment-like/{commentId}")
    public CommentDTO likeComment(@PathVariable long userId,
                                  @PathVariable long commentId,
                                  @RequestParam boolean liked) {
        log.debug("likeComment: userId={}, commentId={}, liked={}", userId, commentId, liked);
        return commentService.likeComment(userId, commentId, liked);
    }

    @DeleteMapping("/{userId}/comment-like/{commentId}")
    public CommentDTO unLikeComment(@PathVariable long userId,
                                    @PathVariable long commentId) {
        log.debug("unLikeComment: userId={}, commentId={}", userId, commentId);
        return commentService.unLikeComment(userId, commentId);
    }
}
