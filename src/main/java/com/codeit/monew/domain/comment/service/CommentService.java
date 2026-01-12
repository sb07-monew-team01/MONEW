package com.codeit.monew.domain.comment.service;

import com.codeit.monew.domain.comment.dto.request.CommentRegisterRequest;
import com.codeit.monew.domain.comment.dto.response.CommentDto;

public interface CommentService {
    CommentDto create(CommentRegisterRequest request);

}
