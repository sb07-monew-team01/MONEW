package com.codeit.monew.domain.comment.service;

import com.codeit.monew.domain.comment.dto.request.CommentRegisterRequest;
import com.codeit.monew.domain.comment.dto.response.CommentDto;

import java.util.UUID;
public interface CommentService {
    CommentDto create(CommentRegisterRequest request);
    void delete(UUID commentId);
    void hardDelete(UUID commentId);
}
