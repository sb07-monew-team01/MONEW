package com.codeit.monew.user;

import jakarta.validation.Valid;

import java.util.UUID;

public record UserEmailUpdateDto(
        @Valid
        UUID userId,

        // TODO 닉네임 유효성 검사
        String newNickname) {
}
