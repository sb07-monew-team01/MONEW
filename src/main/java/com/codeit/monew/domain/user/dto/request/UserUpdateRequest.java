package com.codeit.monew.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @NotBlank(message = "유저 닉네임은 필수입니다.")
        @Size(min = 1, max = 10, message = "닉네임은 1~10자 사이로 입력해주세요.")
        String nickname
) {
}
