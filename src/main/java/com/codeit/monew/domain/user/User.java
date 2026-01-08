package com.codeit.monew.domain.user;

import lombok.Getter;

@Getter
public class User {
    private String email;
    private String nickname;
    private String password;

    public User(String email, String nickname, String password) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
