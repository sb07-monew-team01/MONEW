package com.codeit.monew.domain.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("사용자는 이메일, 닉네임, 비밀번호의 정보를 가진다면 가입할 수 있다.")
    void createUser(){
        // given
        String email = "hyo37009@gmail.com";
        String nickname = "hyo37";
        String password = "hyo1234";

        // when
        User newUser = new User(email, nickname, password);

        //then
        assertThat(newUser).isNotNull();
    }

}