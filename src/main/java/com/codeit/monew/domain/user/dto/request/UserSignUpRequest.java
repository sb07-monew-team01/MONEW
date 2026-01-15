package com.codeit.monew.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserSignUpRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        @Email
        String email,

        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname,

        @NotBlank(message = "비밀번호는 필수입니다.")
        String password
) {
}
