package com.codeit.monew.domain.user.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserUpdateRequest(
        @NotNull(message = "유저 아이디는 필수입니다.")
        UUID userId,

        // TODO 닉네임 유효성 검사
        @NotNull(message = "유저 닉네임은 필수입니다.")
        String newNickname
) {
}
