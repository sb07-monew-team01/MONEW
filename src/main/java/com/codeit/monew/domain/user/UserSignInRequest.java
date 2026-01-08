package com.codeit.monew.domain.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserSignInRequest(
        @NotNull(message = "이메일은 필수입니다.")
        @Email
        String email,

        @NotNull(message = "닉네임은 필수입니다.")
        String nickname,

        @NotNull(message = "비밀번호는 필수입니다.")
        String password
) {
}
