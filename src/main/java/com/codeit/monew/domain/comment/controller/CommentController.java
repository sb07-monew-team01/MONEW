package com.codeit.monew.domain.comment.controller;

import com.codeit.monew.domain.comment.dto.request.CommentRegisterRequest;
import com.codeit.monew.domain.comment.dto.response.CommentDto;
import com.codeit.monew.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/comments")
@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentService commentService;

    public ResponseEntity<CommentDto> create(@RequestBody CommentRegisterRequest request) {
        CommentDto created = commentService.createComment(request);
        return ResponseEntity.ok(created);
    }
}