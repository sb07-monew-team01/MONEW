package com.codeit.monew.user;

import com.codeit.monew.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    @DisplayName("사용자는 이메일, 닉네임, 비밀번호의 정보를 가진다면 생성할 수 있다.")
    void createUser() {
        // given
        String email = "hyo37009@gmail.com";
        String nickname = "hyo37";
        String password = "hyo1234";

        // when
        User newUser = new User(email, nickname, password);

        //then
        assertThat(newUser).isNotNull();
    }

    @Test
    @DisplayName("닉네임을 수정할 수 있다.")
    void changeNickname() {
        // given
        User user = new User("hyo37009@gmail.com", "nickname", "password");

        // when
        String newNickname = "itsMe";
        user.updateNickname(newNickname);

        //then
        assertThat(user.getNickname()).isEqualTo(newNickname);
    }
}