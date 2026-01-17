package com.codeit.monew.domain.commentuserlike.service;

import com.codeit.monew.domain.commentuserlike.dto.CommentUserLikeDto;

import java.util.UUID;

public interface CommentUserLikeService {
    CommentUserLikeDto like(UUID userId, UUID commentId);

}
