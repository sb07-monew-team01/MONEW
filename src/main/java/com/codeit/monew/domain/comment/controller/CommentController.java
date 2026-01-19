package com.codeit.monew.domain.comment.controller;

import com.codeit.monew.domain.comment.dto.request.CommentOrderBy;
import com.codeit.monew.domain.comment.dto.request.CommentRegisterRequest;
import com.codeit.monew.domain.comment.dto.request.CommentUpdateRequest;
import com.codeit.monew.domain.comment.dto.request.SortDirection;
import com.codeit.monew.domain.comment.dto.response.CommentDto;
import com.codeit.monew.domain.comment.dto.response.CommentPageResponse;
import com.codeit.monew.domain.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RequestMapping("/api/comments")
@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public CommentPageResponse getComments(
            @RequestParam UUID articleId,
            @RequestHeader(value = "Monew-Request-User-ID") UUID userId,
            @RequestParam CommentOrderBy orderBy,
            @RequestParam SortDirection direction,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) String after,
            @RequestParam(defaultValue = "20") int limit
    ) {
        LocalDateTime afterDateTime =
                after != null ? LocalDateTime.parse(after) : null;

        return commentService.getComments(
                articleId,
                userId,
                orderBy,
                direction,
                cursor,
                LocalDateTime.parse(after),   // 여기서 타입 변환
                limit
        );
    }


    @PostMapping
    public ResponseEntity<CommentDto> create(@Valid @RequestBody CommentRegisterRequest request) {
        CommentDto created = commentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDto> update(
            @PathVariable UUID commentId,
            @RequestHeader("Monew-Request-User-ID") UUID userId,
            @Valid @RequestBody CommentUpdateRequest request) {
        CommentDto updated = commentService.update(commentId, userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable UUID commentId) {
        commentService.delete(commentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{commentId}/hard")
    public ResponseEntity<Void> deleteHard(@PathVariable UUID commentId) {
        commentService.deleteHard(commentId);
        return ResponseEntity.noContent().build();
    }
}