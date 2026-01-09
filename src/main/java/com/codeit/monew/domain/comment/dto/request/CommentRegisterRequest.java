package com.codeit.monew.domain.comment.dto.request;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record CommentRegisterRequest (

    @NotNull(message = "뉴스 기사 ID는 필수입니다.")
    UUID articleId,

    @NotNull(message = "사용자 ID는 필수입니다.")
    UUID userId,

    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(min = 1, max = 500, message = "댓글은 1 자 이상, 500자 이하로 입력해야 합니다.")
    String content
){ }
