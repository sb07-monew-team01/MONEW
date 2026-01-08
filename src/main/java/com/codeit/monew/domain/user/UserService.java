package com.codeit.monew.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public UserDto signIn(UserSignInRequest dto) {
        User user = new User(dto.email(), dto.nickname(), dto.password());
        User saved = userRepository.save(user);
        return userMapper.from(saved);
    }
}
