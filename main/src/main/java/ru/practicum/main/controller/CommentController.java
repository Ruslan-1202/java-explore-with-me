package ru.practicum.main.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.*;
import ru.practicum.main.service.CommentService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/users/{userId}/comments/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDTO createComment(@Valid @RequestBody CommentCreateDTO commentCreateDTO,
                                    @PathVariable long userId,
                                    @PathVariable long eventId) {
        log.debug("createComment: commentCreateDTO=[{}], userId={}, eventId={}", commentCreateDTO, userId, eventId);
        return commentService.createComment(commentCreateDTO, userId, eventId);
    }

    @PatchMapping("/users/{userId}/comments/{commentId}")
    public CommentDTO editCommentByUser(@Valid @RequestBody CommentPatchDTO commentPatchDTO,
                                        @PathVariable long userId,
                                        @PathVariable long commentId) {
        log.debug("editCommentByUser: commentPatchDTO=[{}], userId={}, commentId={}", commentPatchDTO, userId, commentId);
        return commentService.editCommentByUser(commentPatchDTO, userId, commentId);
    }

    @GetMapping("/comments/{eventId}")
    public List<CommentDTO> getCommentByEventId(@PathVariable long eventId,
                                                @RequestParam(required = false, defaultValue = "0") Long from,
                                                @RequestParam(required = false, defaultValue = "10") Long size) {
        log.debug("getCommentByEventId: eventId={}, from={}, size={}", eventId, from, size);
        return commentService.getCommentByEventId(eventId, from, size);
    }

    @DeleteMapping("/users/{userId}/comments/{commentId}")
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

    @PatchMapping("/users/{userId}/comments/{commentId}/like/{liked}")
    public CommentDTO likeComment(@PathVariable long userId,
                                  @PathVariable long commentId,
                                  @PathVariable boolean liked) {
        log.debug("likeComment: userId={}, commentId={}, liked={}", userId, commentId, liked);
        return commentService.likeComment(userId, commentId, liked);
    }
}
