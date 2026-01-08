package com.codeit.monew.domain.user;

public class User {
    private String email;
    private String nickname;
    private String password;

    public User(String email, String nickname, String password) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
    }
}
