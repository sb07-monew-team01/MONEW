package com.codeit.monew.domain.user;

import com.codeit.monew.domain.user.exception.UserLoginFailedException;
import com.codeit.monew.domain.user.exception.UserNotFoundException;
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

    public UserDto login(String email, String password) {
        User byEmail = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        if (byEmail.getPassword().equals(password))
            return userMapper.from(byEmail);
        throw new UserLoginFailedException(email);
    }
}
