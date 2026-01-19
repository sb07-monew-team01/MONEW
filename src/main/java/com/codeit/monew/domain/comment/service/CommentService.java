package com.codeit.monew.domain.comment.service;

import com.codeit.monew.domain.comment.dto.request.CommentOrderBy;
import com.codeit.monew.domain.comment.dto.request.CommentRegisterRequest;
import com.codeit.monew.domain.comment.dto.request.CommentUpdateRequest;
import com.codeit.monew.domain.comment.dto.request.SortDirection;
import com.codeit.monew.domain.comment.dto.response.CommentDto;
import com.codeit.monew.domain.comment.dto.response.CommentPageResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;
public interface CommentService {
    CommentDto create(CommentRegisterRequest request);
    void delete(UUID commentId);
    void deleteHard(UUID commentId);
    CommentDto update(UUID commentId, CommentUpdateRequest request);
    CommentPageResponse getComments(UUID articleId, UUID userId, CommentOrderBy orderBy, SortDirection direction, String cursor, LocalDateTime afterDateTime, int limit);
}
