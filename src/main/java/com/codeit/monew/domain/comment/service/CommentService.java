package com.codeit.monew.domain.comment.service;

import com.codeit.monew.domain.comment.dto.request.CommentRegisterRequest;
import com.codeit.monew.domain.comment.dto.response.CommentDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
@Transactional
public interface CommentService {
    CommentDto create(CommentRegisterRequest request);
    CommentDto delete(UUID commentId);
}
