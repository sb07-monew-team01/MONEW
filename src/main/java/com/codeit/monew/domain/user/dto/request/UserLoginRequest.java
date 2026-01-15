package com.codeit.monew.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserLoginRequest(
        @NotNull(message = "이메일은 필수입니다.")
        @Email
        String email,

        @NotNull(message = "비밀번호는 필수입니다.")
        String password
) {
}
