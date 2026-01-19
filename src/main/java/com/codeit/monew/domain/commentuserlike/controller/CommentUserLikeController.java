package com.codeit.monew.domain.commentuserlike.controller;

import com.codeit.monew.domain.commentuserlike.dto.CommentUserLikeDto;
import com.codeit.monew.domain.commentuserlike.service.CommentUserLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments/{commentId}/comment-likes")
public class CommentUserLikeController {
    private final CommentUserLikeService commentUserLikeService;

    @PostMapping
    public ResponseEntity<CommentUserLikeDto> like(
            @PathVariable UUID commentId,
            @RequestParam UUID userId) {
        CommentUserLikeDto response = commentUserLikeService.like(userId, commentId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> unlike(
            @PathVariable UUID commentId,
            @RequestParam UUID userId) {
        commentUserLikeService.like(userId, commentId);
        return ResponseEntity.noContent().build();
    }


}
