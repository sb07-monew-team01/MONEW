package com.codeit.monew.domain.comment.service;

import com.codeit.monew.domain.comment.dto.request.*;
import com.codeit.monew.domain.comment.dto.response.CommentDto;
import com.codeit.monew.global.dto.PageResponse;

import java.util.UUID;
public interface CommentService {
    CommentDto create(CommentRegisterRequest request);
    void delete(UUID commentId);
    void deleteHard(UUID commentId);
    CommentDto update(UUID commentId, UUID userId, CommentUpdateRequest request);
    PageResponse<CommentDto> getComments(CommentSearchRequest request);
}
