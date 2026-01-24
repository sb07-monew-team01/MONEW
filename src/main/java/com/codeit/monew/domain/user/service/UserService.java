package com.codeit.monew.domain.user.service;

import com.codeit.monew.domain.user.dto.UserDto;
import com.codeit.monew.domain.user.dto.request.*;

import java.util.UUID;

public interface UserService {

    UserDto signUp(UserSignUpRequest dto);

    UserDto login(UserLoginRequest dto);

    UserDto update(UUID requestId, UserUpdateRequest request);

    void delete(UUID loginId, UUID deleteId);

    void deleteHard(UUID loginId, UUID deleteId);
}
