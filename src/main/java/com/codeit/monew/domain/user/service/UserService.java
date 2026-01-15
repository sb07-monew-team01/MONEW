package com.codeit.monew.domain.user.service;

import com.codeit.monew.domain.user.dto.UserDto;
import com.codeit.monew.domain.user.dto.request.UserLoginRequest;
import com.codeit.monew.domain.user.dto.request.UserSignUpRequest;
import com.codeit.monew.domain.user.dto.request.UserUpdateRequest;

import java.util.UUID;

public interface UserService {

    UserDto signUp(UserSignUpRequest dto);

    UserDto login(UserLoginRequest dto);

    UserDto updateUser(UserUpdateRequest dto);

    void delete(UUID userId);

    void deleteHard(UUID userId);
}
