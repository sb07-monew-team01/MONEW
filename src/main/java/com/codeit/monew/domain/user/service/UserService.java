package com.codeit.monew.domain.user.service;

import com.codeit.monew.domain.user.dto.UserDto;
import com.codeit.monew.domain.user.dto.request.UserSignInRequest;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.user.exception.UserAlreadyDeletedException;
import com.codeit.monew.domain.user.exception.UserAlreadyExistsException;
import com.codeit.monew.domain.user.exception.UserLoginFailedException;
import com.codeit.monew.domain.user.exception.UserNotFoundException;
import com.codeit.monew.domain.user.repository.UserRepository;
import com.codeit.monew.domain.user.util.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public UserDto signIn(UserSignInRequest dto) {
        Optional<User> byEmail = userRepository.findByEmail(dto.email());
        if(byEmail.isEmpty()){
            User user = new User(dto.email(), dto.nickname(), dto.password());
            User saved = userRepository.save(user);
            return userMapper.from(saved);
        }
        User user = byEmail.get();
        if (user.getDeletedAt() != null)
            throw new UserAlreadyDeletedException(user);
        throw new UserAlreadyExistsException(user);
    }

    public UserDto login(String email, String password) {
        User byEmail = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        if (byEmail.getPassword().equals(password))
            return userMapper.from(byEmail);
        throw new UserLoginFailedException(email);
    }

    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (user.isDeleted()) {
            throw new UserAlreadyDeletedException(user);
        }
        user.updateDeletedAt(LocalDateTime.now());
    }
}
