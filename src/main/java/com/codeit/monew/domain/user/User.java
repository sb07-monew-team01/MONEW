package com.codeit.monew.domain.user;

import com.codeit.monew.domain.BaseUpdatableEntity;
import lombok.Getter;

@Getter
public class User extends BaseUpdatableEntity {
    private final String email;
    private String nickname;
    private final String password;

    public User(String email, String nickname, String password) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
